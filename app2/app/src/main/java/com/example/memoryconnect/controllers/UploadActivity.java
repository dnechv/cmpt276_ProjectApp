package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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


    //photo
    private Uri photoUri;


    //youtube link, song name
    private EditText youtubeLinkInput, songNameInput,photoDescriptionInput;
    private ImageView photoPreview;
    private Button uploadPhotoButton, saveButton, viewTimelineButton;



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
        viewTimelineButton = findViewById(R.id.viewTimelineButton);
        songNameInput = findViewById(R.id.song_name_input);
        photoDescriptionInput = findViewById(R.id.photo_description_input);


        // Get patient ID passed from the previous activity
        String patientId = getIntent().getStringExtra("PATIENT_ID");

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();


        // textWatcher for YouTube Link - > load the youtube thumbnail
        youtubeLinkInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String youtubeUrl = s.toString().trim();
                if (isValidYouTubeUrl(youtubeUrl)) {
                    loadYouTubeThumbnail(youtubeUrl); // Load the YouTube thumbnail
                } else {
                    photoPreview.setVisibility(View.GONE); // Hide preview for invalid links
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


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

        // View Timeline Button Logic
        viewTimelineButton.setOnClickListener(v -> {

            //get patient id
            String patientIdForTimeLine = getIntent().getStringExtra("PATIENT_ID");


            //pass id and go to view timeline
            if (patientId != null) {
                Intent intent = new Intent(UploadActivity.this, PatientTimeLine.class);
                intent.putExtra("PATIENT_ID", patientIdForTimeLine);
                startActivity(intent);
            } else {
                Toast.makeText(UploadActivity.this, "No Patient ID found", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch patient data for debugging or preloading purposes (optional)
        fetchPatientData(patientId);
    }


    //this method extracts the youtube video id from the youtube url
    private void loadYouTubeThumbnail(String youtubeUrl) {
        String videoId = extractYouTubeVideoId(youtubeUrl);

        if (videoId != null) {
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";

            //loads image with youtube thumbnail url
            Glide.with(this)
                    .load(thumbnailUrl)
                    .into(photoPreview);


            //show the cover
            photoPreview.setVisibility(View.VISIBLE);
        } else {

            //link broken -> no cover
            photoPreview.setVisibility(View.GONE);
        }
    }

    // Extract YouTube Video ID from YouTube URL
    private String extractYouTubeVideoId(String youtubeUrl) {

        //set video id
        String videoId = null;

        //check if the url is a youtube video url
        if (youtubeUrl.contains("youtube.com/watch?v=")) {

            //extract the video id
            videoId = youtubeUrl.split("v=")[1];


            //get the first occurance of & and remove everything after that
            int ampersandPosition = videoId.indexOf("&");

            //if -1 means character not there so
            if (ampersandPosition != -1) {

                //just get the video id
                videoId = videoId.substring(0, ampersandPosition);
            }

            //if the url contains this
        } else if (youtubeUrl.contains("youtu.be/")) {

            //extract the video id
            videoId = youtubeUrl.substring(youtubeUrl.lastIndexOf("/") + 1);
        }

        //return the video id
        return videoId;
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


        //get unique entry id
        String entryId = timelineRef.push().getKey();


        //get song name from the input field
        String songName = songNameInput.getText().toString().trim();

        //get photo description from the input field
        String photoDescription = photoDescriptionInput.getText().toString().trim();

        //create a map to hold the entry -> allows to display names of patient with combination of user id
        Map<String, Object> entry = new HashMap<>();

        entry.put("photoUrl", photoUrl);
        entry.put("youtubeLink", youtubeLink);
        entry.put("songName", songName);
        entry.put("timestamp", System.currentTimeMillis());
        entry.put("photoDescription", photoDescription);


        timelineRef.child(entryId).setValue(entry)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Timeline entry added successfully!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add entry: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }



    // clears inputs after saving
    private void clearInputs() {
        photoUri = null;
        photoPreview.setImageURI(null);
        photoPreview.setVisibility(View.GONE);
        youtubeLinkInput.setText("");
        ((EditText) findViewById(R.id.song_name_input)).setText("");
        ((EditText) findViewById(R.id.photo_description_input)).setText("");

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