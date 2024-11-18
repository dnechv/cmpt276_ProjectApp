package com.example.memoryconnect.repository;

//Data base synchs with room -> offline support



//repository -> data source management + interaction

import android.app.Application;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

//import room
import androidx.room.Room;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.memoryconnect.model.PhotoEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.memoryconnect.model.Patient;


//import local database
import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.local_database.LocaldatabaseDao;




import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import for background thread
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PatientRepository {
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;



    //TODO: Firebase currently uses foreign key approach for photos


    //local database
    private LocaldatabaseDao localdatabaseDao;

    //executor service
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public PatientRepository(Application application) {

        //firebase initialization
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //get reference for patients
        databaseReference = database.getReference("patients");

        //and for photos
        storageReference = FirebaseStorage.getInstance().getReference("patientPhotos");


        //intialize the local database
        LocalDatabase localDatabase = Room.databaseBuilder(application, LocalDatabase.class, "local_database").build();
        localdatabaseDao = localDatabase.localdatabaseDao();//dao


        //sync room and local databases
        synchLocalDatabase();
    }


    // determines if device is offline
    public LiveData<List<Patient>> getAllPatients(boolean isOffline) {
        if (isOffline) {

            // use local databse
            return getAllPatientsFromLocalDatabase();
        } else {
            //use firebase
            return getAllPatients();
        }
    }

    /**
     * Saves patient information to Firebase Realtime Database.
     *
     * @param patient The Patient object to save.
     * @param onCompleteListener Listener to handle the completion of the save operation.
     */


    //TODO - ADD PATIENTS TO THE DATABASE MEDIA


    //saves a new patient
    public void savePatient(Patient patient, OnCompleteListener<Void> onCompleteListener) {


        //id
        String id = databaseReference.push().getKey();


        if (id != null) {
            patient.setId(id);
            databaseReference.child(id).setValue(patient)



                    .addOnCompleteListener(onCompleteListener)




                    .addOnFailureListener(e -> Log.e("PatientRepository", "Failed to save patient", e));



        } else {


        }
            Log.e("PatientRepository", "Failed to generate unique ID for patient");
        }

    /**
     * Uploads a patient's photo to Firebase Storage and retrieves the download URL.
     *
     * @param photoUri The Uri of the photo to upload.
     * @param onSuccessListener Listener to handle success and retrieve the photo URL.
     * @param onFailureListener Listener to handle any errors during the upload.
     */

    //upload patient photo to firebase storage
    public void uploadPatientPhoto(Uri photoUri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        // Create a unique reference for each photo using UUID
        StorageReference photoRef = storageReference.child(UUID.randomUUID().toString());

        // Upload the file to Firebase Storage
        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot ->
                        // Retrieve the download URL upon successful upload
                        photoRef.getDownloadUrl().addOnSuccessListener(onSuccessListener))
                .addOnFailureListener(onFailureListener);
    }


    //Local database functions:

    //sync local database with the firebase database
    private void synchLocalDatabase() {
        // Get all patients from Firebase Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Patient> patients = new ArrayList<>();//list of patients

                for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                    Patient patient = patientSnapshot.getValue(Patient.class);
                    if (patient != null) {

                        patients.add(patient);
                    }
                }


                Log.d("PatientRepository", "Number of patients fetched from Firebase: " + patients.size());

                //local database patient insersion
                ExecutorService executorService = java.util.concurrent.Executors.newSingleThreadExecutor();
                executorService.execute(() -> {


                    //clear
                    localdatabaseDao.deleteAll();

                    //insert
                    localdatabaseDao.insert(patients);


                    Log.d("PatientRepository", "Number of patients saved to Room: " + patients.size());
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PatientRepository", "synchLocalDatabase:onCancelled", error.toException());
            }
        });
    }




    // sync completion interface
    public interface OnSyncCompleteListener {


        void onSyncComplete();
    }


    // photos synch completion interface
    public interface OnDataSyncListener {
        void onDataSynced(List<PhotoEntry> photos);
    }

    //sync patients from firebase
    public void syncPatientsFromFirebase(OnSyncCompleteListener listener) {


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override


            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //list of patients
                List<Patient> patients = new ArrayList<>();

                //get all patients
                for (DataSnapshot patientSnapshot : snapshot.getChildren()) {

                    //get patient
                    Patient patient = patientSnapshot.getValue(Patient.class);

                    //add patient to the list if not null
                    if (patient != null) {


                        patients.add(patient);


                    }
                }

                Log.d("PatientRepository", "Fetched Patients from Firebase: " + patients.size());


                //execute on background thread
                executorService.execute(() -> {

                    //clear the local database
                    localdatabaseDao.deleteAll();

                    //insert patients in local database
                    localdatabaseDao.insert(patients);



                    Log.d("PatientRepository", "Patients saved to Room.");




                    //post the listener on the main thread - all patients were added


                    new Handler(Looper.getMainLooper()).post(() -> {


                        Log.d("PatientRepository", "Patient sync completed.");


                        listener.onSyncComplete();






                    });




                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


                Log.e("PatientRepository", "syncPatientsFromFirebase:onCancelled", error.toException());


            }
        });
    }

    public void insertPhotoEntry(PhotoEntry photoEntry) {
        executorService.execute(() -> {

            //insert photo entry using room
            localdatabaseDao.insertPhotoEntry(photoEntry);


            Log.d("RoomInsert", "Inserted Photo ID: " + photoEntry.getId());
        });
    }




    //get all patients from the local database
    public LiveData<List<Patient>> getAllPatientsFromLocalDatabase() {
        return localdatabaseDao.getAllPatients();
    }

    public LiveData<List<Patient>> getAllPatients() {

        //livedata for patients
        MutableLiveData<List<Patient>> patientsLiveData = new MutableLiveData<>();

        //get reference to the database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("patients");


        //listen to the quiery of the database attached to the listenver
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override


            //if data is changed
            public void onDataChange(DataSnapshot snapshot) {

                //list of patients
                List<Patient> patients = new ArrayList<>();


                Log.d("PatientRepository", "Snapshot exists: " + snapshot.exists());


                //get all patients
                for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                    Patient patient = patientSnapshot.getValue(Patient.class);
                    if (patient != null) {
                        Log.d("PatientRepository", "Loaded patient: " + patient.getName());
                        patients.add(patient);
                    }
                }

                //updates livedata with patients -> updates UI
                patientsLiveData.setValue(patients);
            }

            //if does not work
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PatientRepository", "loadPatients:onCancelled", error.toException());
            }
        });

        return patientsLiveData;
    }


    public LiveData<Patient> getPatientById(String patientId, boolean isOffline) {

        if (isOffline) {
            return localdatabaseDao.getPatientById(patientId);
        } else {

            MutableLiveData<Patient> patientLiveData = new MutableLiveData<>();

            //get patient reference
            databaseReference.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Convert the DataSnapshot to a Patient object
                    Patient patient = snapshot.getValue(Patient.class);
                    patientLiveData.setValue(patient); // Update LiveData with the retrieved patient
                }

                //if does not work
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("PatientRepository", "getPatientById:onCancelled", error.toException());
                }
            });

            return patientLiveData;
        }

    }



}
