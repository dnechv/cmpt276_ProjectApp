package com.example.memoryconnect.repository;

// Repository -> data source management + interaction

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.memoryconnect.model.PhotoEntry;
import com.example.memoryconnect.model.Patient;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PatientRepository {
    private final DatabaseReference databaseReference; // Firebase reference
    private final StorageReference storageReference;  // Firebase storage reference



    public PatientRepository(Application application) {
        // Initialize Firebase references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("patients");
        storageReference = FirebaseStorage.getInstance().getReference("patientPhotos");

    }

    /**
     * Saves patient information to Firebase Realtime Database.
     *
     * @param patient The Patient object to save.
     * @param onCompleteListener Listener to handle the completion of the save operation.
     */
    public void savePatient(Patient patient, OnCompleteListener<Void> onCompleteListener) {
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
        StorageReference photoRef = storageReference.child(UUID.randomUUID().toString());
        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot ->
                        photoRef.getDownloadUrl().addOnSuccessListener(onSuccessListener))
                .addOnFailureListener(onFailureListener);
    }



    /**
     * Syncs patients from Firebase and notifies listener on completion.
     *
     * @param listener Listener to handle sync completion.
     */
    public void syncPatientsFromFirebase(OnSyncCompleteListener listener) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Patient> patients = new ArrayList<>();
                for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                    Patient patient = patientSnapshot.getValue(Patient.class);
                    if (patient != null) {
                        patients.add(patient);
                    }
                }

                Log.d("PatientRepository", "Fetched Patients from Firebase: " + patients.size());
                listener.onSyncComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PatientRepository", "syncPatientsFromFirebase:onCancelled", error.toException());
            }
        });
    }

    /**
     * Fetches all patients from Firebase and returns LiveData.
     *
     * @return LiveData<List<Patient>> of patients.
     */
    public LiveData<List<Patient>> getAllPatients() {
        MutableLiveData<List<Patient>> patientsLiveData = new MutableLiveData<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Patient> patients = new ArrayList<>();
                for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                    Patient patient = patientSnapshot.getValue(Patient.class);
                    if (patient != null) {
                        Log.d("PatientRepository", "Loaded patient: " + patient.getName());
                        patients.add(patient);
                    }
                }

                patientsLiveData.setValue(patients);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PatientRepository", "getAllPatients:onCancelled", error.toException());
            }
        });

        return patientsLiveData;
    }

    /**
     * Fetches a specific patient by ID from Firebase and returns LiveData.
     *
     * @param patientId The ID of the patient.
     * @return LiveData<Patient> of the requested patient.
     */
    public LiveData<Patient> getPatientById(String patientId) {
        MutableLiveData<Patient> patientLiveData = new MutableLiveData<>();

        databaseReference.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patient = snapshot.getValue(Patient.class);
                patientLiveData.setValue(patient);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("PatientRepository", "getPatientById:onCancelled", error.toException());
            }
        });

        return patientLiveData;
    }

    /**
     * Fetches timeline entries for a specific patient from Firebase and returns LiveData.
     *
     * @param patientId The ID of the patient.
     * @return LiveData<List<PhotoEntry>> containing the patient's timeline entries.
     */
    public LiveData<List<PhotoEntry>> getTimelineEntries(String patientId) {
        MutableLiveData<List<PhotoEntry>> timelineEntriesLiveData = new MutableLiveData<>();

        // Firebase reference to the specific patient's timeline entries
        DatabaseReference timelineRef = databaseReference.child(patientId).child("timelineEntries");

        timelineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PhotoEntry> timelineEntries = new ArrayList<>();
                for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
                    PhotoEntry entry = entrySnapshot.getValue(PhotoEntry.class);
                    if (entry != null) {
                        timelineEntries.add(entry);
                    }
                }
                Log.d("PatientRepository", "Fetched timeline entries: " + timelineEntries.size());
                timelineEntriesLiveData.setValue(timelineEntries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PatientRepository", "Failed to fetch timeline entries: " + error.getMessage());
            }
        });

        return timelineEntriesLiveData;
    }



    // Interface for sync completion
    public interface OnSyncCompleteListener {
        void onSyncComplete();
    }
}