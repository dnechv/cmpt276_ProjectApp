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
import com.example.memoryconnect.model.Patient;
import com.example.memoryconnect.ViewModel.PatientViewModel;
import com.google.firebase.auth.FirebaseAuth;

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

    private Button takephotoButton;


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
        takephotoButton = findViewById(R.id.takephotoButton);

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

        // set up take picture button
        takephotoButton.setOnClickListener(v -> takepic());
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

    //save patient method
    private void savePatient() {


        //get input
        String name = nameEditText.getText().toString();
        String nickname = nicknameEditText.getText().toString();
        String ageText = ageEditText.getText().toString();
        String comment = commentEditText.getText().toString();


        //check for valid input
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

        //generate a unique patient ID
        String patientId = UUID.randomUUID().toString();

        //check if photo is selected
        if (selectedPhotoUri != null) {
            patientViewModel.uploadPhoto(selectedPhotoUri, uri -> {


                //create a patient object



                Patient patient = new Patient(patientId, name, nickname, age, comment, uri.toString());
                patientViewModel.savePatient(patient);
            });
        } else {



            //without url for photo
            Patient patient = new Patient(patientId, name, nickname, age, comment, null);
            patientViewModel.savePatient(patient);
        }
    }
    public void takepic(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_ACTION_CODE);
    }
    public static final int CAMERA_ACTION_CODE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CAMERA_ACTION_CODE && resultCode == RESULT_OK && data != null){
           Bundle bundle = data.getExtras();
           Bitmap finalphoto = (Bitmap) bundle.get("data");
           photoImageView.setImageBitmap(finalphoto);

           Uri photoUri = saveBitmapToFile(finalphoto);
           if (photoUri != null) {
               selectedPhotoUri = photoUri;
               photoImageView.setImageURI(photoUri);

           }
        }
    }

    private Uri saveBitmapToFile(Bitmap bitmap) {
        try {
            // Create a temporary file
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photoFile = new File(storageDir, "photo_" + UUID.randomUUID().toString() + ".jpg");

            // Write the bitmap to the file
            FileOutputStream fos = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            // Return URI from the file
            return Uri.fromFile(photoFile);
        } catch (IOException e) {
            Log.e("CreatePatientActivity", "Error saving photo", e);
            return null;
        }
    }

}