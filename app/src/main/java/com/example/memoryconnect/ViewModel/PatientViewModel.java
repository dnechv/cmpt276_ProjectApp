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


//imports for connectivity
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PatientViewModel extends AndroidViewModel {

    //variables for the repository, LiveData, and MutableLiveData
    private final PatientRepository patientRepository;
    private final LiveData<List<Patient>> allPatients;
    private final MutableLiveData<Boolean> isPatientSaved = new MutableLiveData<>();
    private final MutableLiveData<String> uploadError = new MutableLiveData<>();

    //constructor
    public PatientViewModel(Application application) {
        super(application);

        //initialize the repository
        patientRepository = new PatientRepository(application);

        //fetch data dynamically based on connectivity
        allPatients = fetchPatients(application);



    }


    //method to fetch patients based on connectivity
    private LiveData<List<Patient>> fetchPatients(Application application) {
        if (isOffline(application)) {
            Log.d("PatientViewModel", "Fetching patients from local database (Room)");
            return patientRepository.getAllPatientsFromLocalDatabase();
        } else {
            Log.d("PatientViewModel", "Fetching patients from Firebase");
            return patientRepository.getAllPatients();
        }
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
            //success message if uploaded
            onSuccess.accept(uri);
        }, error -> {
            //error message if not uploaded
            uploadError.setValue("Failed to upload photo: " + error.getMessage());
        });
    }



    //get the list of all patients for observation in the UI
    public LiveData<List<Patient>> getAllPatients() {
        return allPatients;
    }



    // get patient by id
    public LiveData<Patient> getPatientById(String patientId) {

        //check if offline
        boolean isOffline = isOffline(getApplication());

        //return patient by id
        return patientRepository.getPatientById(patientId, isOffline);
    }

    //method to check network -> connectivity Manger
    public boolean isOffline(Application application) {

        //connectivity manager
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Application.CONNECTIVITY_SERVICE);

        //get active network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //return true if no network is connected
        return activeNetwork == null || !activeNetwork.isConnected();
    }

    








}
