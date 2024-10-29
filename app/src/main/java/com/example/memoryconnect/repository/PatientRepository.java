package com.example.memoryconnect.repository;


import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.memoryconnect.model.Patient;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PatientRepository {
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;

    public PatientRepository() {
        // Initialize Firebase Realtime Database reference for "patients" node
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("patients");

        // Initialize Firebase Storage reference for patient photos
        storageReference = FirebaseStorage.getInstance().getReference("patientPhotos");
    }

    /**
     * Saves patient information to Firebase Realtime Database.
     *
     * @param patient The Patient object to save.
     * @param onCompleteListener Listener to handle the completion of the save operation.
     */
    public void savePatient(Patient patient, OnCompleteListener<Void> onCompleteListener) {
        // Save the patient using their unique ID as the key
        databaseReference.child(patient.getId()).setValue(patient)
                .addOnCompleteListener(onCompleteListener);
    }

    /**
     * Uploads a patient's photo to Firebase Storage and retrieves the download URL.
     *
     * @param photoUri The Uri of the photo to upload.
     * @param onSuccessListener Listener to handle success and retrieve the photo URL.
     * @param onFailureListener Listener to handle any errors during the upload.
     */
    public void uploadPatientPhoto(Uri photoUri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        // Create a unique reference for each photo using UUID
        StorageReference photoRef = storageReference.child(UUID.randomUUID().toString());

        // Upload the file to Firebase Storage
        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot ->
                        // Retrieve the download URL upon successful upload
                        photoRef.getDownloadUrl().addOnSuccessListener(onSuccessListener))
                .addOnFailureListener(onFailureListener);
    }

    public LiveData<List<Patient>> getAllPatients() {
        MutableLiveData<List<Patient>> patientsLiveData = new MutableLiveData<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("patients");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Patient> patients = new ArrayList<>();
               // Log.d("PatientRepository", "Snapshot exists: " + snapshot.exists());

                for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                    Patient patient = patientSnapshot.getValue(Patient.class);
                    if (patient != null) {
                        //Log.d("PatientRepository", "Loaded patient: " + patient.getName());
                        patients.add(patient);
                    }
                }

                // Update LiveData with loaded patients
                patientsLiveData.setValue(patients);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PatientRepository", "loadPatients:onCancelled", error.toException());
            }
        });

        return patientsLiveData;
    }


    public LiveData<Patient> getPatientById(String patientId) {
        MutableLiveData<Patient> patientLiveData = new MutableLiveData<>();

        // Reference to the specific patient in the database
        databaseReference.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Convert the DataSnapshot to a Patient object
                Patient patient = snapshot.getValue(Patient.class);
                patientLiveData.setValue(patient); // Update LiveData with the retrieved patient
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("PatientRepository", "getPatientById:onCancelled", error.toException());
            }
        });

        return patientLiveData;
    }


}
