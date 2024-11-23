package com.example.memoryconnect.repository;

// Repository -> Data source management + interaction

// Imports
import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.memoryconnect.local_database.PhotoEntryDatabaseDAO;
import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.model.Patient;
import com.example.memoryconnect.model.PhotoEntry;
import com.example.memoryconnect.repository.PatientRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoEntryRepository {

    //variables to work with database
    private final PhotoEntryDatabaseDAO photoEntryDatabaseDAO;
    private final LiveData<List<PhotoEntry>> allPhotos;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final DatabaseReference databaseReference;
    private final PatientRepository patientRepository;



    public PhotoEntryRepository(Application application) {

        //database instance - db
        LocalDatabase db = LocalDatabase.getDatabase(application);

        //initalize dao for photos
        photoEntryDatabaseDAO = db.photoEntryDatabaseDAO();

        //get all photos using dao
        allPhotos = photoEntryDatabaseDAO.getAllPhotos();

        //initialize data reference to firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("photos");

        //repository for patients
        patientRepository = new PatientRepository(application);
    }

    //upload photo to timeline for the patient
    public void uploadTimelinePhoto(Uri photoUri, String patientId, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {


        //initializing firebase storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        //generating a unique id for the photo
        StorageReference photoRef = storageReference.child("patientTimelinePhotos/" + patientId + "/" + UUID.randomUUID().toString());

        //uploading the photo to the firebase storage
        photoRef.putFile(photoUri)


                .addOnSuccessListener(taskSnapshot -> {


                    // retrive photo url if successful
                    photoRef.getDownloadUrl().addOnSuccessListener(uri -> {




                        Log.d("PhotoEntryRepository", "Photo uploaded successfully. Download URL: " + uri.toString());



                        onSuccessListener.onSuccess(uri);



                    }).addOnFailureListener(e -> {

                        Log.e("PhotoEntryRepository", "Failed to get download URL: " + e.getMessage());


                        onFailureListener.onFailure(e);  // Return failure to the listener
                    });
                })




                //if not successful
                .addOnFailureListener(e -> {


                    Log.e("PhotoEntryRepository", "Failed to upload photo: " + e.getMessage());



                    onFailureListener.onFailure(e);
                });
    }

    // all photos from local database
    public LiveData<List<PhotoEntry>> getAllPhotos() {
        return allPhotos;
    }

    //get photos by patient id
    public LiveData<List<PhotoEntry>> getAllPhotos(String patientId) {


        Log.d("RoomQuery", "Querying Room for patientId: " + patientId);


        //get all photos for patient
        return photoEntryDatabaseDAO.getAllPhotosForPatient(patientId);
    }

    //insert photo locally
    public void insert(PhotoEntry photoEntry) {
        executorService.execute(() -> {


            //check if patient exists
            int patientExists = photoEntryDatabaseDAO.isPatientIdExists(photoEntry.getPatientId());

            if (patientExists>0) {

                Log.d("RoomInsert", "Inserting Photo ID: " + photoEntry.getId());

                photoEntryDatabaseDAO.insert(photoEntry);
            } else {
                Log.e("RoomInsert", "Failed to insert Photo ID: " + photoEntry.getId() + " - Patient ID not found in Room.");
            }
        });
    }

    //update photo locally
    public void update(PhotoEntry photoEntry) {
        executorService.execute(() -> photoEntryDatabaseDAO.update(photoEntry));
    }

    //delete locally
    public void delete(PhotoEntry photoEntry) {
        executorService.execute(() -> photoEntryDatabaseDAO.delete(photoEntry));
    }

    //delete using patient id
    public void deletePhotosByPatient(String patientId) {
        executorService.execute(() -> photoEntryDatabaseDAO.deletePhotosByPatient(patientId));
    }


    // Sync photos from Firebase
    public void syncPhotosFromFirebase(String patientId, OnDataSyncListener listener) {
        patientRepository.syncPatientsFromFirebase(() -> { // Sync patients first

            Log.d("FirebaseSync", "Patients synced. Now syncing photos...");

            //get photos via patient id from firebase
            databaseReference.orderByChild("patientId").equalTo(patientId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {



                            Log.d("FirebaseSync", "Total photos found in Firebase snapshot: " + snapshot.getChildrenCount());

                            //lists for photos
                            List<PhotoEntry> photoList = new ArrayList<>();
                            List<PhotoEntry> photosToQueue = new ArrayList<>();

                            //go over all photos
                            for (DataSnapshot data : snapshot.getChildren()) {

                                //get photo entry
                                PhotoEntry photo = data.getValue(PhotoEntry.class);


                                if (photo != null) {
                                    Log.d("FirebaseSync", "Loaded Photo Entry - ID: " + photo.getId() + ", Patient ID: " + photo.getPatientId());

                                    //check for patient existence locally
                                    checkPatientExists(photo.getPatientId(), exists -> {
                                        if (exists) {


                                            Log.d("FirebaseSync", "Patient found for Photo ID: " + photo.getId());


                                            //add photo to list
                                            photoList.add(photo);
                                        } else {


                                            Log.e("FirebaseSync", "Patient not found for Photo ID: " + photo.getId() + " - Queuing photo for later.");

                                            //add photo to queue
                                            photosToQueue.add(photo);
                                        }

                                        //finalize if checks are done
                                        if (photoList.size() + photosToQueue.size() == snapshot.getChildrenCount()) {


                                            Log.d("FirebaseSync", "All photos processed. Syncing complete.");
                                            listener.onDataSynced(photoList);

                                            //TODO- insert photos -> fails because photos are being inserted faster than patients
                                            for (PhotoEntry syncedPhoto : photoList) {
                                                Log.d("RoomInsert", "Attempting to insert Photo ID: " + syncedPhoto.getId());

                                                //insert photo
                                                insert(syncedPhoto);
                                            }

                                            //process queued photos
                                            processQueuedPhotos(photosToQueue);
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FirebaseSync", "Error syncing photos", error.toException());
                        }
                    });
        });
    }

    private void processQueuedPhotos(List<PhotoEntry> queuedPhotos) {
        if (queuedPhotos.isEmpty()) {
            return;
        }

        // process each photo  in a loop
        for (PhotoEntry photo : queuedPhotos) {
            checkPatientExists(photo.getPatientId(), exists -> {
                if (exists) {


                    Log.d("FirebaseSync", "Patient found for queued photo. Inserting Photo ID: " + photo.getId());


                    insert(photo);
                } else {


                    Log.e("FirebaseSync", "Patient not found for queued photo. Skipping.");


                }
            });
        }
    }

    //check if the patient exists in the database
    private void checkPatientExists(String patientId, OnPatientExistCallback callback) {
        // get id using repository


        patientRepository.getPatientById(patientId, true).observeForever(foundPatient -> {


            callback.onResult(foundPatient != null);
        });
    }


    //check if patient exists interface
    private interface OnPatientExistCallback {
        void onResult(boolean exists);
    }

    //interface for data sync listener
    public interface OnDataSyncListener {
        void onDataSynced(List<PhotoEntry> photos);
    }
}