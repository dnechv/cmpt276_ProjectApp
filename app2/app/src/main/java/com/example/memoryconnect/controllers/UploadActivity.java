package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private Uri photoUri;

    private EditText youtubeLinkInput;
    private ImageView photoPreview;
    private Button uploadPhotoButton, saveButton;

    private static final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // UI Components
        youtubeLinkInput = findViewById(R.id.youtube_link_input);
        photoPreview = findViewById(R.id.photoPreview);
        uploadPhotoButton = findViewById(R.id.upload_photo_button);
        saveButton = findViewById(R.id.save_button);

        // Get patient ID passed from the previous activity
        String patientId = getIntent().getStringExtra("PATIENT_ID");

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Upload Photo Button
        uploadPhotoButton.setOnClickListener(v -> openGallery());

        // Save Button Logic
        saveButton.setOnClickListener(v -> {
            if (photoUri != null) {
                uploadPhoto(patientId);
            } else if (!TextUtils.isEmpty(youtubeLinkInput.getText().toString().trim())) {
                saveYouTubeLink(patientId);
            } else {
                Toast.makeText(this, "Please add a photo or YouTube link", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch patient data for debugging or preloading purposes (optional)
        fetchPatientData(patientId);
    }

    // Open the Gallery to Select a Photo
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            photoUri = data.getData();
            photoPreview.setImageURI(photoUri); // Display the selected photo
            photoPreview.setVisibility(View.VISIBLE);
        }
    }

    // Upload Photo to Firebase Storage
    private void uploadPhoto(String patientId) {
        if (photoUri == null) return;

        //upload code ->  specify the path to save the image
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("patientTimelinePhotos/" + patientId);

        storageRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String photoUrl = uri.toString();
                    saveToFirebase(patientId, photoUrl, null); // Save photo URL to database
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Photo upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Save YouTube Link to Firebase
    private void saveYouTubeLink(String patientId) {
        String youtubeLink = youtubeLinkInput.getText().toString().trim();
        if (TextUtils.isEmpty(youtubeLink) || !isValidYouTubeUrl(youtubeLink)) {
            youtubeLinkInput.setError("Please enter a valid YouTube link");
            return;
        }
        saveToFirebase(patientId, null, youtubeLink);
    }

    private boolean isValidYouTubeUrl(String url) {
        return url.startsWith("https://www.youtube.com/") || url.startsWith("https://youtu.be/");
    }

    // Save Timeline Entry to Firebase Database
    private void saveToFirebase(String patientId, @Nullable String photoUrl, @Nullable String youtubeLink) {
        DatabaseReference timelineRef = databaseReference.child("patients").child(patientId).child("timelineEntries");

        String entryId = timelineRef.push().getKey(); // Generate unique entry ID
        Map<String, Object> entry = new HashMap<>();
        entry.put("photoUrl", photoUrl);
        entry.put("youtubeLink", youtubeLink);
        entry.put("timestamp", System.currentTimeMillis());

        timelineRef.child(entryId).setValue(entry)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Timeline entry added successfully!", Toast.LENGTH_SHORT).show();
                    clearInputs(); // Clear inputs after saving
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add entry: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Clear Inputs After Saving
    private void clearInputs() {
        photoUri = null;
        photoPreview.setImageURI(null);
        photoPreview.setVisibility(View.GONE);
        youtubeLinkInput.setText("");
    }

    private void fetchPatientData(String patientId) {
        databaseReference.child("patients").child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    Long age = snapshot.child("age").getValue(Long.class);
                    String ageString = (age != null) ? String.valueOf(age) : "Unknown Age";
                    String comment = snapshot.child("comment").getValue(String.class);

                    Log.d("FirebaseDebug", "Name: " + name + ", Age: " + ageString + ", Comment: " + comment);
                } else {
                    Log.e("FirebaseDebug", "Patient not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDebug", "Error: " + error.getMessage());
            }
        });
    }
}