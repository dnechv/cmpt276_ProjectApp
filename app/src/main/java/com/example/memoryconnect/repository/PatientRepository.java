package com.example.memoryconnect.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.local_database.LocaldatabaseDao;
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
import java.util.concurrent.ExecutorService;

public class PatientRepository {
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;

    // Local database DAO
    private final LocaldatabaseDao localdatabaseDao;

    public PatientRepository(Application application) {
        // Initialize Firebase Realtime Database reference for "patients" node
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("patients");

        // Initialize Firebase Storage reference for patient photos
        storageReference = FirebaseStorage.getInstance().getReference("patientPhotos");

        // Initialize the local database using Room
        LocalDatabase localDatabase = Room.databaseBuilder(application, LocalDatabase.class, "local_database").build();
        localdatabaseDao = localDatabase.localdatabaseDao();

        // Sync Room and Firebase databases
        syncLocalDatabase();
    }

    // Save patient to Firebase
    public void savePatient(Patient patient, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(patient.getId()).setValue(patient)
                .addOnCompleteListener(onCompleteListener);
    }

    public void uploadPatientPhoto(Uri photoUri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference photoRef = storageReference.child(UUID.randomUUID().toString());
        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot ->
                        photoRef.getDownloadUrl().addOnSuccessListener(onSuccessListener)
                                .addOnFailureListener(onFailureListener))
                .addOnFailureListener(onFailureListener);
    }

    // Sync local database with Firebase
    private void syncLocalDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Patient> patients = new ArrayList<>();
                for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                    Patient patient = patientSnapshot.getValue(Patient.class);
                    if (patient != null) {
                        patients.add(patient);
                    }
                }

                ExecutorService executorService = java.util.concurrent.Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    localdatabaseDao.deleteAll();
                    localdatabaseDao.insert(patients);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PatientRepository", "syncLocalDatabase:onCancelled", error.toException());
            }
        });
    }

    // Get all patients from the local database
    public LiveData<List<Patient>> getAllPatientsFromLocalDatabase() {
        return localdatabaseDao.getAllPatients();
    }

    // Get all patients from Firebase
    public LiveData<List<Patient>> getAllPatients() {
        MutableLiveData<List<Patient>> patientsLiveData = new MutableLiveData<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Patient> patients = new ArrayList<>();
                for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                    Patient patient = patientSnapshot.getValue(Patient.class);
                    if (patient != null) {
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

    // Get a specific patient by ID
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

    // Delete a patient by ID
    public void deletePatient(String patientId, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(patientId).removeValue().addOnCompleteListener(onCompleteListener);
    }

    // Delete a patient's photo by URL
    public void deletePhoto(String photoUrl, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);
        photoRef.delete().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }
}
