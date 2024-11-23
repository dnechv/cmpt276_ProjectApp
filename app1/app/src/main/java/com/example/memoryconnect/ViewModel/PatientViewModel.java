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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.function.Consumer;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PatientViewModel extends AndroidViewModel {

    private final PatientRepository patientRepository; // Repository
    private final LiveData<List<Patient>> allPatients;
    private final MutableLiveData<Boolean> isPatientSaved = new MutableLiveData<>();
    private final MutableLiveData<String> uploadError = new MutableLiveData<>();

    // Constructor
    public PatientViewModel(@NonNull Application application) {
        super(application);
        patientRepository = new PatientRepository(application);
        allPatients = fetchPatients(application); // Fetch patients dynamically
    }

    // Method to fetch patients based on connectivity
    private LiveData<List<Patient>> fetchPatients(Application application) {
        if (isOffline(application)) {
            Log.d("PatientViewModel", "Fetching patients from local database (Room)");
            return patientRepository.getAllPatientsFromLocalDatabase();
        } else {
            Log.d("PatientViewModel", "Fetching patients from Firebase");
            return patientRepository.getAllPatients();
        }
    }

    // Check if device is offline
    public boolean isOffline(Application application) {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Application.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnected();
    }

    // LiveData to observe save operation status
    public LiveData<Boolean> getIsPatientSaved() {
        return isPatientSaved;
    }

    // LiveData to observe error messages related to upload
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

    // Get all patients for observation in the UI
    public LiveData<List<Patient>> getAllPatients() {
        return allPatients;
    }

    // Get patient by ID
    public LiveData<Patient> getPatientById(String patientId) {
        boolean isOffline = isOffline(getApplication());
        return patientRepository.getPatientById(patientId, isOffline);
    }

    // Update patient
    // Update patient with preservation of photoUrl
    public LiveData<Boolean> updatePatient(Patient updatedPatient) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        // Fetch the existing patient to preserve photoUrl
        patientRepository.getPatientById(updatedPatient.getId(), false).observeForever(existingPatient -> {
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


    // Delete patient
    public LiveData<Boolean> deletePatient(String patientId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        patientRepository.deletePatient(patientId, task -> result.setValue(task.isSuccessful()));
        return result;
    }
}
