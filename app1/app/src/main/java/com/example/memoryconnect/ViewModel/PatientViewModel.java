package com.example.memoryconnect.ViewModel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.memoryconnect.model.Patient;
import com.example.memoryconnect.repository.PatientRepository;

import java.util.List;
import java.util.function.Consumer;

public class PatientViewModel extends AndroidViewModel {

    private final PatientRepository patientRepository; // Repository
    private final MutableLiveData<Boolean> isPatientSaved = new MutableLiveData<>();
    private final MutableLiveData<String> uploadError = new MutableLiveData<>();

    // Constructor
    public PatientViewModel(@NonNull Application application) {
        super(application);

        // Initialize the repository
        patientRepository = new PatientRepository(application);
    }

    // LiveData to observe the save operation status
    public LiveData<Boolean> getIsPatientSaved() {
        return isPatientSaved;
    }

    // LiveData to observe error messages related to uploads
    public LiveData<String> getUploadError() {
        return uploadError;
    }

    // Save a patient in Firebase Database
    public void savePatient(Patient patient) {
        patientRepository.savePatient(patient, task -> {
            if (task.isSuccessful()) {
                isPatientSaved.setValue(true);
            } else {
                isPatientSaved.setValue(false);
            }
        });
    }

    // Upload a patient's photo to Firebase Storage
    public void uploadPhoto(Uri photoUri, Consumer<Uri> onSuccess) {
        patientRepository.uploadPatientPhoto(photoUri, uri -> onSuccess.accept(uri),
                error -> uploadError.setValue("Failed to upload photo: " + error.getMessage()));
    }

    // Get all patients from Firebase
    public LiveData<List<Patient>> getAllPatients() {
        return patientRepository.getAllPatients(); // Always fetch directly from Firebase
    }

    // Get a specific patient by ID from Firebase
    public LiveData<Patient> getPatientById(String patientId) {
        return patientRepository.getPatientById(patientId); // Fetch directly from Firebase
    }

    // Update a patient while preserving the photo URL
    public LiveData<Boolean> updatePatient(Patient updatedPatient) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        // Fetch the existing patient to preserve photoUrl
        patientRepository.getPatientById(updatedPatient.getId()).observeForever(existingPatient -> {
            if (existingPatient != null) {
                // Preserve the photoUrl if it exists
                if (existingPatient.getPhotoUrl() != null && updatedPatient.getPhotoUrl() == null) {
                    updatedPatient.setPhotoUrl(existingPatient.getPhotoUrl());
                }

                // Perform the update
                patientRepository.updatePatient(updatedPatient, task -> result.setValue(task.isSuccessful()));
            } else {
                Log.e("PatientViewModel", "Patient not found, cannot update.");
                result.setValue(false); // Fail if the patient doesn't exist
            }
        });

        return result;
    }

    // Delete a patient
    public LiveData<Boolean> deletePatient(String patientId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        patientRepository.deletePatient(patientId, task -> result.setValue(task.isSuccessful()));
        return result;
    }

    // Save PIN for a patient
    public LiveData<Boolean> savePatientPin(String patientId, String pin) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        patientRepository.savePin(patientId, pin, task -> {
            if (task.isSuccessful()) {
                result.setValue(true);
            } else {
                Log.e("PatientViewModel", "Failed to save PIN for patient: " + patientId);
                result.setValue(false);
            }
        });
        return result;
    }

    // Get PIN for a patient
    public LiveData<String> getPatientPin(String patientId) {
        return patientRepository.getPin(patientId);
    }
}