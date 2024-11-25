package com.example.memoryconnect.ViewModel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.memoryconnect.model.Patient;
import com.example.memoryconnect.repository.PatientRepository;

import java.util.List;
import java.util.function.Consumer;

public class PatientViewModel extends AndroidViewModel {

    // Variables for the repository and LiveData
    private final PatientRepository patientRepository;
    private final MutableLiveData<Boolean> isPatientSaved = new MutableLiveData<>();
    private final MutableLiveData<String> uploadError = new MutableLiveData<>();

    // Constructor
    public PatientViewModel(Application application) {
        super(application);

        // Initialize the repository
        patientRepository = new PatientRepository(application);
    }

    // LiveData to observe the save operation status
    public LiveData<Boolean> getIsPatientSaved() {
        return isPatientSaved;
    }

    // LiveData to observe any error messages related to the upload
    public LiveData<String> getUploadError() {
        return uploadError;
    }

    /**
     * Save a patient in Firebase Database.
     *
     * @param patient The Patient object to save.
     */
    public void savePatient(Patient patient) {
        patientRepository.savePatient(patient, task -> {
            if (task.isSuccessful()) {
                isPatientSaved.setValue(true);
            } else {
                isPatientSaved.setValue(false);
            }
        });
    }

    /**
     * Upload a patient's photo to Firebase Storage.
     *
     * @param photoUri The Uri of the photo to upload.
     * @param onSuccess Callback to receive the download URL of the uploaded photo.
     */
    public void uploadPhoto(Uri photoUri, Consumer<Uri> onSuccess) {
        patientRepository.uploadPatientPhoto(photoUri, uri -> {
            // Success callback
            onSuccess.accept(uri);
        }, error -> {
            // Error callback
            uploadError.setValue("Failed to upload photo: " + error.getMessage());
        });
    }

    /**
     * Get the list of all patients from Firebase.
     *
     * @return LiveData<List<Patient>> for observing in the UI.
     */
    public LiveData<List<Patient>> getAllPatients() {
        return patientRepository.getAllPatients(); // Fetch directly from Firebase
    }

    /**
     * Get a specific patient by ID from Firebase.
     *
     * @param patientId The ID of the patient to fetch.
     * @return LiveData<Patient> of the requested patient.
     */
    public LiveData<Patient> getPatientById(String patientId) {
        return patientRepository.getPatientById(patientId); // Fetch directly from Firebase
    }
}