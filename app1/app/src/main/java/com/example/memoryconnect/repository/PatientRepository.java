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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PatientRepository {
    private final DatabaseReference databaseReference; // Firebase reference
    private final StorageReference storageReference;  // Firebase storage reference

    public PatientRepository(Application application) {
        // Initialize Firebase references
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("patients");
        storageReference = FirebaseStorage.getInstance().getReference("patientPhotos");

        // Enable syncing
        databaseReference.keepSynced(true);
    }

    // Save patient information to Firebase
    public void savePatient(Patient patient, OnCompleteListener<Void> onCompleteListener) {
        if (patient.getId() == null || patient.getId().isEmpty()) {
            // Generate unique ID if not provided
            String id = databaseReference.push().getKey();
            if (id != null) {
                patient.setId(id);
            } else {
                Log.e("PatientRepository", "Failed to generate unique ID for patient");
                return;
            }
        }

        databaseReference.child(patient.getId()).setValue(patient)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> Log.e("PatientRepository", "Failed to save patient", e));
    }

    // Upload patient photo to Firebase Storage
    public void uploadPatientPhoto(Uri photoUri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference photoRef = storageReference.child(UUID.randomUUID().toString());
        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot ->
                        photoRef.getDownloadUrl().addOnSuccessListener(onSuccessListener))
                .addOnFailureListener(onFailureListener);
    }

    // Fetch all patients from Firebase
    public LiveData<List<Patient>> getAllPatients() {
        MutableLiveData<List<Patient>> patientsLiveData = new MutableLiveData<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PatientRepository", "getAllPatients:onCancelled", error.toException());
            }
        });
        return patientsLiveData;
    }

    // Fetch a specific patient by ID from Firebase
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

    // Fetch timeline entries for a specific patient
    public LiveData<List<PhotoEntry>> getTimelineEntries(String patientId) {
        MutableLiveData<List<PhotoEntry>> timelineEntriesLiveData = new MutableLiveData<>();
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

    // Update patient information in Firebase
    public void updatePatient(Patient patient, OnCompleteListener<Void> onCompleteListener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", patient.getName());
        updates.put("nickname", patient.getNickname());
        updates.put("age", patient.getAge());
        updates.put("comment", patient.getComment());
        if (patient.getPhotoUrl() != null) {
            updates.put("photoUrl", patient.getPhotoUrl());
        }

        databaseReference.child(patient.getId()).updateChildren(updates)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> Log.e("PatientRepository", "Failed to update patient", e));
    }

    // Delete a patient from Firebase
    public void deletePatient(String patientId, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(patientId).removeValue()
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> Log.e("PatientRepository", "Failed to delete patient", e));
    }

    // Save PIN for a patient
    public void savePin(String patientId, String pin, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(patientId).child("pin").setValue(pin)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> Log.e("PatientRepository", "Failed to save PIN", e));
    }

    public LiveData<String> getPin(String patientId) {
        MutableLiveData<String> pinLiveData = new MutableLiveData<>();
        databaseReference.child(patientId).child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pin = snapshot.getValue(String.class);
                pinLiveData.setValue(pin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PatientRepository", "Failed to fetch PIN: " + error.getMessage());
            }
        });
        return pinLiveData;
    }

    // Interface for sync completion
    public interface OnSyncCompleteListener {
        void onSyncComplete();
    }
}