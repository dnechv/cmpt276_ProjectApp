package com.example.memoryconnect.repository;


import android.net.Uri;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.memoryconnect.model.Patient;
import java.util.UUID;

public class PatientRepository {
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;

    public PatientRepository() {
        // Initialize Firebase Realtime Database reference for "patients" node
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("patients");

        // Initialize Firebase Storage reference for patient photos
        storageReference = FirebaseStorage.getInstance().getReference("patientPhotos");
    }

    /**
     * Saves patient information to Firebase Realtime Database.
     *
     * @param patient The Patient object to save.
     * @param onCompleteListener Listener to handle the completion of the save operation.
     */
    public void savePatient(Patient patient, OnCompleteListener<Void> onCompleteListener) {
        // Save the patient using their unique ID as the key
        databaseReference.child(patient.getId()).setValue(patient)
                .addOnCompleteListener(onCompleteListener);
    }

    /**
     * Uploads a patient's photo to Firebase Storage and retrieves the download URL.
     *
     * @param photoUri The Uri of the photo to upload.
     * @param onSuccessListener Listener to handle success and retrieve the photo URL.
     * @param onFailureListener Listener to handle any errors during the upload.
     */
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
}
