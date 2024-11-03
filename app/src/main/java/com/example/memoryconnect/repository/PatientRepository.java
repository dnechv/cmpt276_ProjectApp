package com.example.memoryconnect.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

//import room
import androidx.room.Room;

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


//import local database
import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.local_database.LocaldatabaseDao;




import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import for background thread
import java.util.concurrent.ExecutorService;

public class PatientRepository {
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;

    //local database
    private LocaldatabaseDao localdatabaseDao;

    public PatientRepository(Application application) {
        // Initialize Firebase Realtime Database reference for "patients" node
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("patients");

        // Initialize Firebase Storage reference for patient photos
        storageReference = FirebaseStorage.getInstance().getReference("patientPhotos");

        //keeping the database and storage references for the patients - saves offline only if tapped for version 1
        //TODO - Full local databse using ROOM

        //intialize the local database
        LocalDatabase localDatabase = Room.databaseBuilder(application, LocalDatabase.class, "local_database").build();
        localdatabaseDao = localDatabase.localdatabaseDao();//dao


        //sync room and local databases
        synchLocalDatabase();


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


    //Local database functions:

    //sync local database with the firebase database
    private void synchLocalDatabase() {
        // Get all patients from Firebase Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Patient> patients = new ArrayList<>();//list of patients

                for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                    Patient patient = patientSnapshot.getValue(Patient.class);
                    if (patient != null) {
                        patients.add(patient);
                    }
                }

                // Insert all patients into the local database
                ExecutorService executorService = java.util.concurrent.Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    localdatabaseDao.deleteAll();
                    localdatabaseDao.insert(patients);
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PatientRepository", "synchLocalDatabase:onCancelled", error.toException());
            }
        });
    }




    //get all patients from the local database
    public LiveData<List<Patient>> getAllPatientsFromLocalDatabase() {
        return localdatabaseDao.getAllPatients();
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
