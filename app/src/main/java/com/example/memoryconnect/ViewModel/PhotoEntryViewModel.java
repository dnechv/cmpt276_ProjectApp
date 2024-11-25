package com.example.memoryconnect.ViewModel;

// Provides data to the UI and handles background operations like Firebase sync

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.memoryconnect.model.PhotoEntry;
import com.example.memoryconnect.repository.PhotoEntryRepository;

import java.util.List;

public class PhotoEntryViewModel extends AndroidViewModel {

    // Repository for Firebase operations
    private final PhotoEntryRepository photoEntryRepository;
    private final MutableLiveData<List<PhotoEntry>> firebasePhotos = new MutableLiveData<>();

    // Constructor
    public PhotoEntryViewModel(@NonNull Application application) {
        super(application);
        photoEntryRepository = new PhotoEntryRepository(application);
    }

    /**
     * Initialize photos for a specific patient, fetching from Firebase.
     *
     * @param patientId The ID of the patient to fetch photos for.
     */
    public void initializePhotos(String patientId) {
        photoEntryRepository.syncPhotosFromFirebase(patientId, photos -> {
            firebasePhotos.postValue(photos);
            Log.d("FirebaseSync", "Photos synced from Firebase: " + photos.size());
        });
    }

    /**
     * Returns the list of photos fetched from Firebase.
     *
     * @return LiveData<List<PhotoEntry>> of photos.
     */
    public LiveData<List<PhotoEntry>> getAllPhotos() {
        return firebasePhotos;
    }
}