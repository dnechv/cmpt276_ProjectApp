package com.example.memoryconnect.ViewModel;

// Provides data to the UI and handles background operations like Firebase sync

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.memoryconnect.model.PhotoEntry;
import com.example.memoryconnect.repository.PhotoEntryRepository;

import java.util.ArrayList;
import java.util.List;

public class PhotoEntryViewModel extends AndroidViewModel {

    // Variables for repository and LiveData
    private final PhotoEntryRepository photoEntryRepository;
    private final MutableLiveData<List<PhotoEntry>> firebasePhotos = new MutableLiveData<>();
    private LiveData<List<PhotoEntry>> allPhotos;
    private final MediatorLiveData<List<PhotoEntry>> combinedPhotos = new MediatorLiveData<>();

    // ViewModel constructor
    public PhotoEntryViewModel(@NonNull Application application) {
        super(application); // Superclass constructor
        photoEntryRepository = new PhotoEntryRepository(application); // Initialize repository
    }

    // Initialize photos for a specific patient (local + cloud)
    public void initializePhotos(String patientId) {
        allPhotos = photoEntryRepository.getAllPhotos(patientId); // Fetch from Room (local)

        // Sync with Firebase and update local Room database
        photoEntryRepository.syncPhotosFromFirebase(patientId, new PhotoEntryRepository.OnDataSyncListener() {
            @Override
            public void onDataSynced(List<PhotoEntry> photos) {
                firebasePhotos.postValue(photos);
                Log.d("FirebaseSync", "Photos synced from Firebase: " + photos.size());
            }
        });

        // Combine local Room data and Firebase data
        observeAndMergeData();
    }

    // Combines Room and Firebase photos, ensuring no duplicates
    private void observeAndMergeData() {
        combinedPhotos.addSource(allPhotos, localPhotos -> {
            mergeAndSetPhotos(localPhotos, firebasePhotos.getValue());
            Log.d("PhotoEntries", "Loaded from Room: " + (localPhotos != null ? localPhotos.size() : 0));
        });

        combinedPhotos.addSource(firebasePhotos, cloudPhotos -> {
            mergeAndSetPhotos(allPhotos.getValue(), cloudPhotos);
            Log.d("PhotoEntries", "Loaded from Firebase: " + (cloudPhotos != null ? cloudPhotos.size() : 0));
        });
    }

    // Returns the combined list of photos
    public LiveData<List<PhotoEntry>> getAllPhotos() {
        return combinedPhotos;
    }

    // Merges Room and Firebase photos, avoiding duplicates
    private void mergeAndSetPhotos(List<PhotoEntry> localPhotos, List<PhotoEntry> cloudPhotos) {
        List<PhotoEntry> mergedList = new ArrayList<>();
        if (localPhotos != null) {
            mergedList.addAll(localPhotos);
        }
        if (cloudPhotos != null) {
            for (PhotoEntry cloudPhoto : cloudPhotos) {
                if (!mergedList.contains(cloudPhoto)) {
                    mergedList.add(cloudPhoto);
                }
            }
        }
        combinedPhotos.setValue(mergedList);
        Log.d("PhotoEntries", "Merged Cloud & Local: " + mergedList.size());
    }

    // Sync with Room database - offline access
    private void saveToLocalDatabase(List<PhotoEntry> photos) {
        for (PhotoEntry photo : photos) {
            photoEntryRepository.insert(photo); // Saves photo to local database
            Log.d("LocalDatabase", "Saving photo to Room: " + photo.getId());
        }
    }
}