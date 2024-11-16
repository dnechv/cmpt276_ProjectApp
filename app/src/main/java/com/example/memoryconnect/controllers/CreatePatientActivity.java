package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.memoryconnect.R;
import com.example.memoryconnect.model.Patient;
import com.example.memoryconnect.ViewModel.PatientViewModel;

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

        // Initialize ViewModel
        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        // Set up photo picker
        photoImageView.setOnClickListener(v -> pickPhoto());

        // Set up Save button
        saveButton.setOnClickListener(v -> savePatient());

        // Set up Cancel button
        cancelButton.setOnClickListener(v -> finish());

        // Observe ViewModel LiveData for save success or error handling
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

    // Save patient information
    private void savePatient() {
        // Generate a unique patient ID
        String patientId = UUID.randomUUID().toString();

        // Validate user inputs
        String name = nameEditText.getText().toString().trim();
        String nickname = nicknameEditText.getText().toString().trim();
        String ageText = ageEditText.getText().toString().trim();
        String comment = commentEditText.getText().toString().trim();

        if (name.isEmpty() || nickname.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Age must be a number.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if a photo was selected
        if (selectedPhotoUri != null) {
            // Upload the photo and get the URL
            patientViewModel.uploadPhoto(selectedPhotoUri,
                    uri -> {
                        // Photo uploaded successfully
                        Patient patient = new Patient(patientId, name, nickname, age, comment, uri.toString());
                        savePatientToDatabase(patient);
                    },
                    error -> Toast.makeText(this, "Failed to upload photo: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            );
        } else {
            // Save patient without photo URL
            Patient patient = new Patient(patientId, name, nickname, age, comment, null);
            savePatientToDatabase(patient);
        }
    }

    // Helper method to save patient and display confirmation
    private void savePatientToDatabase(Patient patient) {
        patientViewModel.savePatient(patient);
        Toast.makeText(this, "Patient saved successfully!", Toast.LENGTH_SHORT).show();
        finish(); // Close activity
    }
}
