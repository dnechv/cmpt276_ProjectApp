package com.example.memoryconnect.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.memoryconnect.model.PhotoEntry;
import com.example.memoryconnect.repository.PhotoEntryRepository;

import java.util.List;

public class PhotoEntryViewModel extends AndroidViewModel {

    private final PhotoEntryRepository photoEntryRepository;
    private final MutableLiveData<List<PhotoEntry>> firebasePhotos = new MutableLiveData<>();
    private final LiveData<List<PhotoEntry>> allPhotos;
    private final MediatorLiveData<List<PhotoEntry>> combinedPhotos = new MediatorLiveData<>();

    public PhotoEntryViewModel(@NonNull Application application) {
        super(application);
        photoEntryRepository = new PhotoEntryRepository(application);
        allPhotos = photoEntryRepository.getAllPhotos();

        combinedPhotos.addSource(firebasePhotos, combinedPhotos::setValue);
        combinedPhotos.addSource(allPhotos, combinedPhotos::setValue);
    }

    public void initializePhotos(String patientId) {
        photoEntryRepository.syncPhotosFromFirebase(patientId, photos -> {
            firebasePhotos.postValue(photos);
        });
    }

    public LiveData<List<PhotoEntry>> getAllPhotos() {
        return combinedPhotos;
    }

    public void saveToLocalDatabase(List<PhotoEntry> photos) {
        for (PhotoEntry photo : photos) {
            photoEntryRepository.insert(photo);
        }
    }
}
