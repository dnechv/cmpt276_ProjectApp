package com.example.memoryconnect.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.local_database.PhotoEntryDatabaseDAO;
import com.example.memoryconnect.model.PhotoEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoEntryRepository {

    private final PhotoEntryDatabaseDAO photoEntryDatabaseDAO;
    private final ExecutorService executorService;
    private final DatabaseReference databaseReference;

    public PhotoEntryRepository(Application application) {
        // Initialize Room database
        LocalDatabase db = LocalDatabase.getDatabase(application);
        photoEntryDatabaseDAO = db.photoEntryDatabaseDAO();

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("patientTimeline");

        // Create executor for background tasks
        executorService = Executors.newSingleThreadExecutor();
    }

    // Fetch all photos from Room
    public LiveData<List<PhotoEntry>> getAllPhotos() {
        return photoEntryDatabaseDAO.getAllPhotos();
    }

    // Fetch photos for a specific patient from Room
    public LiveData<List<PhotoEntry>> getAllPhotos(String patientId) {
        return photoEntryDatabaseDAO.getAllPhotosForPatient(patientId);
    }

    // Insert a photo into Room database
    public void insert(PhotoEntry photoEntry) {
        executorService.execute(() -> photoEntryDatabaseDAO.insert(photoEntry));
    }

    // Sync photos from Firebase for a specific patient
    public void syncPhotosFromFirebase(String patientId, OnDataSyncListener listener) {
        DatabaseReference timelineRef = databaseReference.child(patientId);

        timelineRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PhotoEntry> photoList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    PhotoEntry photo = data.getValue(PhotoEntry.class);
                    if (photo != null) {
                        photoList.add(photo);
                    }
                }
                listener.onDataSynced(photoList); // Callback to pass synced data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PhotoEntryRepository", "Error fetching timeline data", error.toException());
            }
        });
    }

    // Upload a photo to Firebase and save metadata in Firebase Database
    public void uploadTimelinePhoto(Uri photoUri, String patientId, OnUploadCompleteListener listener) {
        // Create a unique storage path
        StorageReference photoRef = FirebaseStorage.getInstance()
                .getReference("patientTimelinePhotos/" + patientId + "/" + UUID.randomUUID().toString());

        // Upload photo
        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            listener.onUploadComplete(uri);
                        })
                        .addOnFailureListener(e -> listener.onUploadFailed(e)))
                .addOnFailureListener(e -> listener.onUploadFailed(e));
    }

    // Interface for photo sync callbacks
    public interface OnDataSyncListener {
        void onDataSynced(List<PhotoEntry> photos);
    }

    // Interface for photo upload callbacks
    public interface OnUploadCompleteListener {
        void onUploadComplete(Uri downloadUri);

        void onUploadFailed(Exception e);
    }
}
