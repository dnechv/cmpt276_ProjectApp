package com.example.memoryconnect.ViewModel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.memoryconnect.model.Patient;
import com.example.memoryconnect.repository.PatientRepository;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.function.Consumer;

public class PatientViewModel extends AndroidViewModel {
    private final PatientRepository patientRepository;
    private final LiveData<List<Patient>> allPatients;
    private final MutableLiveData<Boolean> isPatientSaved = new MutableLiveData<>();
    private final MutableLiveData<String> uploadError = new MutableLiveData<>();

    //constructor
    public PatientViewModel(Application application) {
        super(application);
        patientRepository = new PatientRepository(application);
        // Get all patients from the repository
        allPatients = patientRepository.getAllPatients();
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
            // Pass the URI back to the calling code for further processing
            onSuccess.accept(uri);
        }, error -> {
            // Set an error message to be observed
            uploadError.setValue("Failed to upload photo: " + error.getMessage());
        });
    }

    // Method to get the list of all patients for observation in the UI
    public LiveData<List<Patient>> getAllPatients() {
        LiveData<List<Patient>> patients = patientRepository.getAllPatients();
        patients.observeForever(patientList -> {
            Log.d("PatientViewModel", "Patients loaded: " + patientList.size());
        });
        return patients;
    }

    // Additional method to get details for a specific patient by ID
    public LiveData<Patient> getPatientById(String patientId) {
        return patientRepository.getPatientById(patientId);  // Implement this in repository as needed
    }

    








}
