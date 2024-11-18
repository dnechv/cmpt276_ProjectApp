package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.memoryconnect.R;
import com.example.memoryconnect.ViewModel.PatientViewModel;
import com.example.memoryconnect.model.Patient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class CreatePatientActivity extends AppCompatActivity {
    private PatientViewModel patientViewModel;
    private Uri selectedPhotoUri;

    private ImageView photoImageView;
    private EditText nameEditText;
    private EditText nicknameEditText;
    private EditText ageEditText;
    private EditText commentEditText;
    private Button saveButton;
    private Button cancelButton;
    private Button takePhotoButton;

    public static final int CAMERA_ACTION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_patient);

        // Initialize UI components
        photoImageView = findViewById(R.id.photoImageView);
        nameEditText = findViewById(R.id.nameEditText);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        commentEditText = findViewById(R.id.commentEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        takePhotoButton = findViewById(R.id.takephotoButton);

        // Initialize ViewModel
        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        // Set up photo picker
        photoImageView.setOnClickListener(v -> pickPhoto());

        // Set up Save button
        saveButton.setOnClickListener(v -> savePatient());

        // Set up Cancel button
        cancelButton.setOnClickListener(v -> finish());

        // Set up Take Photo button
        takePhotoButton.setOnClickListener(v -> takePicture());

        // Observe LiveData from ViewModel
        observeViewModel();
    }

    // Launch photo picker
    private final ActivityResultLauncher<Intent> photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedPhotoUri = result.getData().getData();
                    photoImageView.setImageURI(selectedPhotoUri);
                }
            }
    );

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        photoPickerLauncher.launch(intent);
    }

    // Observe LiveData from ViewModel
    private void observeViewModel() {
        patientViewModel.getIsPatientSaved().observe(this, isSaved -> {
            if (isSaved != null && isSaved) {
                Toast.makeText(this, "Patient saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else if (isSaved != null) {
                Toast.makeText(this, "Failed to save patient.", Toast.LENGTH_SHORT).show();
            }
        });

        patientViewModel.getUploadError().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("CreatePatientActivity", errorMessage);
            }
        });
    }

    // Save patient method
    private void savePatient() {
        // Get input
        String name = nameEditText.getText().toString().trim();
        String nickname = nicknameEditText.getText().toString().trim();
        String ageText = ageEditText.getText().toString().trim();
        String comment = commentEditText.getText().toString().trim();

        // Check for valid input
        if (name.isEmpty() || nickname.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique patient ID
        String patientId = UUID.randomUUID().toString();

        // Check if photo is selected
        if (selectedPhotoUri != null) {
            patientViewModel.uploadPhoto(selectedPhotoUri,
                    uri -> {
                        // On photo upload success, create a Patient object and save it
                        Patient patient = new Patient(patientId, name, nickname, age, comment, uri.toString());
                        patientViewModel.savePatient(patient);
                    },
                    error -> {
                        // On photo upload failure
                        Toast.makeText(this, "Photo upload failed.", Toast.LENGTH_SHORT).show();
                        Log.e("CreatePatientActivity", "Photo upload error: " + error.getMessage());
                    }
            );
        } else {
            // No photo selected, create a Patient object without a photo URL
            Patient patient = new Patient(patientId, name, nickname, age, comment, null);
            patientViewModel.savePatient(patient);
        }
    }

    // Launch camera for taking a picture
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_ACTION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_ACTION_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap finalPhoto = (Bitmap) extras.get("data");
            photoImageView.setImageBitmap(finalPhoto);

            // Save the captured photo to a file and set it as the selected photo URI
            Uri photoUri = saveBitmapToFile(finalPhoto);
            if (photoUri != null) {
                selectedPhotoUri = photoUri;
                photoImageView.setImageURI(photoUri);
            }
        }
    }

    // Save bitmap to a file and return its URI
    private Uri saveBitmapToFile(Bitmap bitmap) {
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photoFile = new File(storageDir, "photo_" + UUID.randomUUID().toString() + ".jpg");

            FileOutputStream fos = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            return Uri.fromFile(photoFile);
        } catch (IOException e) {
            Log.e("CreatePatientActivity", "Error saving photo", e);
            return null;
        }
    }
}
