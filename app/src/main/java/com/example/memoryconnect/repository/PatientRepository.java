package com.example.memoryconnect.repository;

import android.app.Application;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.local_database.LocaldatabaseDao;
import com.example.memoryconnect.model.Patient;
import com.example.memoryconnect.model.PhotoEntry;
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
import java.util.concurrent.Executors;

public class PatientRepository {
    private final DatabaseReference databaseReference; // Firebase database reference
    private final StorageReference storageReference;  // Firebase storage reference
    private final LocaldatabaseDao localdatabaseDao;  // DAO for Room database
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Background thread executor

    public PatientRepository(Application application) {
        // Firebase initialization
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("patients");
        storageReference = FirebaseStorage.getInstance().getReference("patientPhotos");

        // Room database initialization
        LocalDatabase localDatabase = Room.databaseBuilder(application, LocalDatabase.class, "local_database").build();
        localdatabaseDao = localDatabase.localdatabaseDao();

        // Sync Room and Firebase databases
        synchLocalDatabase();
    }

    /**
     * Retrieves all patients depending on the offline/online status.
     */
    public LiveData<List<Patient>> getAllPatients(boolean isOffline) {
        if (isOffline) {
            return getAllPatientsFromLocalDatabase();
        } else {
            return getAllPatients();
        }
    }

    /**
     * Saves a new patient to Firebase Realtime Database.
     */
    public void savePatient(Patient patient, OnCompleteListener<Void> onCompleteListener) {
        String id = databaseReference.push().getKey();
        if (id != null) {
            patient.setId(id);
            databaseReference.child(id).setValue(patient)
                    .addOnCompleteListener(onCompleteListener)
                    .addOnFailureListener(e -> Log.e("PatientRepository", "Failed to save patient", e));
        } else {
            Log.e("PatientRepository", "Failed to generate unique ID for patient");
        }
    }

    /**
     * Uploads a patient's photo to Firebase Storage.
     */
    public void uploadPatientPhoto(Uri photoUri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference photoRef = storageReference.child(UUID.randomUUID().toString());
        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl().addOnSuccessListener(onSuccessListener))
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Deletes a patient from Firebase Realtime Database.
     */
    public void deletePatient(String patientId, OnCompleteListener<Void> onCompleteListener) {
        databaseReference.child(patientId)
                .removeValue()
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> Log.e("PatientRepository", "Failed to delete patient", e));
    }

    /**
     * Deletes a photo from Firebase Storage.
     */
    public void deletePhoto(String photoUrl, OnCompleteListener<Void> onCompleteListener) {
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);
        photoRef.delete()
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> Log.e("PatientRepository", "Failed to delete photo", e));
    }

    /**
     * Syncs local Room database with Firebase Realtime Database.
     */
    private void synchLocalDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Patient> patients = new ArrayList<>();
                for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                    Patient patient = patientSnapshot.getValue(Patient.class);
                    if (patient != null) {
                        patients.add(patient);
                    }
                }
                Log.d("PatientRepository", "Fetched patients from Firebase: " + patients.size());
                executorService.execute(() -> {
                    localdatabaseDao.deleteAll();
                    localdatabaseDao.insert(patients);
                    Log.d("PatientRepository", "Patients saved to Room: " + patients.size());
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PatientRepository", "synchLocalDatabase:onCancelled", error.toException());
            }
        });
    }

    /**
     * Syncs patients from Firebase to the local Room database.
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
                executorService.execute(() -> {
                    localdatabaseDao.deleteAll();
                    localdatabaseDao.insert(patients);
                    new Handler(Looper.getMainLooper()).post(listener::onSyncComplete);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PatientRepository", "syncPatientsFromFirebase:onCancelled", error.toException());
            }
        });
    }

    /**
     * Retrieves all patients from the Room database.
     */
    public LiveData<List<Patient>> getAllPatientsFromLocalDatabase() {
        return localdatabaseDao.getAllPatients();
    }

    /**
     * Retrieves all patients from Firebase.
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
     * Retrieves a patient by ID, depending on offline/online status.
     */
    public LiveData<Patient> getPatientById(String patientId, boolean isOffline) {
        if (isOffline) {
            return localdatabaseDao.getPatientById(patientId);
        } else {
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
    }

    /**
     * Sync completion interface.
     */
    public interface OnSyncCompleteListener {
        void onSyncComplete();
    }
}
