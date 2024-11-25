package com.example.memoryconnect.repository;

// Repository -> Data source management + interaction

// Imports
import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.memoryconnect.model.PhotoEntry;
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

public class PhotoEntryRepository {

    private final DatabaseReference databaseReference; // Firebase Database reference
    private final PatientRepository patientRepository; // Reference to PatientRepository

    public PhotoEntryRepository(Application application) {
        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference("photos");
        patientRepository = new PatientRepository(application); // Initialize patient repository
    }

    /**
     * Upload photo to the timeline for a patient.
     *
     * @param photoUri The URI of the photo.
     * @param patientId The ID of the patient.
     * @param onSuccessListener Callback for success.
     * @param onFailureListener Callback for failure.
     */
    public void uploadTimelinePhoto(Uri photoUri, String patientId, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        // Initialize Firebase storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoRef = storageReference.child("patientTimelinePhotos/" + patientId + "/" + UUID.randomUUID().toString());

        // Upload photo to Firebase Storage
        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Log.d("PhotoEntryRepository", "Photo uploaded successfully. Download URL: " + uri.toString());
                            onSuccessListener.onSuccess(uri);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("PhotoEntryRepository", "Failed to get download URL: " + e.getMessage());
                            onFailureListener.onFailure(e);
                        }))
                .addOnFailureListener(e -> {
                    Log.e("PhotoEntryRepository", "Failed to upload photo: " + e.getMessage());
                    onFailureListener.onFailure(e);
                });
    }

    /**
     * Fetch all photos for a specific patient from Firebase.
     *
     * @param patientId The ID of the patient.
     * @return LiveData<List<PhotoEntry>> of photos for the patient.
     */
    public LiveData<List<PhotoEntry>> getPhotosForPatient(String patientId) {
        MutableLiveData<List<PhotoEntry>> photosLiveData = new MutableLiveData<>();

        databaseReference.orderByChild("patientId").equalTo(patientId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<PhotoEntry> photos = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            PhotoEntry photoEntry = data.getValue(PhotoEntry.class);
                            if (photoEntry != null) {
                                photos.add(photoEntry);
                                Log.d("PhotoEntryRepository", "Loaded photo: " + photoEntry.getId());
                            }
                        }
                        photosLiveData.setValue(photos);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PhotoEntryRepository", "Error fetching photos: " + error.getMessage());
                    }
                });

        return photosLiveData;
    }

    /**
     * Sync photos from Firebase for a patient.
     *
     * @param patientId The ID of the patient.
     * @param listener Callback for data sync completion.
     */
    public void syncPhotosFromFirebase(String patientId, OnDataSyncListener listener) {
        databaseReference.orderByChild("patientId").equalTo(patientId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<PhotoEntry> photoList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            PhotoEntry photo = data.getValue(PhotoEntry.class);
                            if (photo != null) {
                                photoList.add(photo);
                                Log.d("FirebaseSync", "Synced photo: " + photo.getId());
                            }
                        }
                        listener.onDataSynced(photoList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseSync", "Error syncing photos: " + error.getMessage());
                    }
                });
    }

    // Interface for data sync listener
    public interface OnDataSyncListener {
        void onDataSynced(List<PhotoEntry> photos);
    }
}