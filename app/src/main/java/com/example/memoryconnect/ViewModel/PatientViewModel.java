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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class PatientViewModel extends AndroidViewModel {
    private final PatientRepository patientRepository;
    private final LiveData<List<Patient>> patients;
    private final MutableLiveData<Boolean> isPatientSaved = new MutableLiveData<>();
    private final MutableLiveData<String> uploadError = new MutableLiveData<>();

    // Constructor
    public PatientViewModel(@NonNull Application application) {
        super(application);
        patientRepository = new PatientRepository(application);
        patients = patientRepository.getAllPatients();
    }

    // Retrieve all patients
    public LiveData<List<Patient>> getAllPatients() {
        return patients;
    }

    // Delete patient and their photo
    public void deletePatientAndPhoto(String patientId, OnDeleteCompleteListener listener) {
        patientRepository.getPatientById(patientId).observeForever(patient -> {
            if (patient != null) {
                String photoUrl = patient.getPhotoUrl();
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    // Delete photo first, then delete patient
                    patientRepository.deletePhoto(photoUrl, aVoid -> {
                        // Photo deleted successfully, now delete patient data
                        patientRepository.deletePatient(patientId, task -> {
                            if (task.isSuccessful()) {
                                listener.onSuccess();
                            } else {
                                listener.onFailure("Failed to delete patient data.");
                            }
                        });
                    }, error -> listener.onFailure("Failed to delete photo: " + error.getMessage()));
                } else {
                    // No photo, just delete patient
                    patientRepository.deletePatient(patientId, task -> {
                        if (task.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            listener.onFailure("Failed to delete patient data.");
                        }
                    });
                }
            } else {
                listener.onFailure("Patient not found.");
            }
        });
    }

    // Callback Interface for Deletion
    public interface OnDeleteCompleteListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // Retrieve a single patient by ID
    public LiveData<Patient> getPatientById(String patientId) {
        return patientRepository.getPatientById(patientId);
    }

    // Save patient data
    public void savePatient(Patient patient) {
        patientRepository.savePatient(patient, task -> {
            if (task.isSuccessful()) {
                isPatientSaved.postValue(true);
                Log.d("PatientViewModel", "Patient updated successfully.");
            } else {
                isPatientSaved.postValue(false);
                Log.e("PatientViewModel", "Failed to update patient.");
            }
        });
    }

    // Upload photo for a patient
    public void uploadPhoto(Uri photoUri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        patientRepository.uploadPatientPhoto(photoUri, onSuccessListener, onFailureListener);
    }


    // Observe save status
    public LiveData<Boolean> getIsPatientSaved() {
        return isPatientSaved;
    }

    // Observe upload error
    public LiveData<String> getUploadError() {
        return uploadError;
    }

    // Callback Interface for Photo Upload
    public interface OnPhotoUploadListener {
        void onSuccess(String photoUrl);
        void onFailure(String errorMessage);
    }
}
