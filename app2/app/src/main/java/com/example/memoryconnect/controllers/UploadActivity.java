package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadActivity extends AppCompatActivity {

    private TextView patientDetailsTextView;
    private Button addToTimelineButton, deletePatientButton, viewTimelineButton, removePatientButton;
    private String patientId, patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Get patient details passed from the previous activity
        patientId = getIntent().getStringExtra("PATIENT_ID");
        patientName = getIntent().getStringExtra("PATIENT_NAME");

        // Initialize UI components
        patientDetailsTextView = findViewById(R.id.patientDetailsTextView);
        addToTimelineButton = findViewById(R.id.addPhotosButton);
        deletePatientButton = findViewById(R.id.deletePatientButton);
        viewTimelineButton = findViewById(R.id.viewTimelineButton);
        removePatientButton = findViewById(R.id.removePatientButton);

        // Display patient details
        if (patientName != null) {
            patientDetailsTextView.setText("Name: " + patientName);
        } else {
            patientDetailsTextView.setText("Patient details not found.");
        }

        // Add to Timeline button logic
        addToTimelineButton.setOnClickListener(v -> {
            Intent intent = new Intent(UploadActivity.this, AddToTimelineActivity.class);
            intent.putExtra("PATIENT_ID", patientId);
            startActivity(intent);
        });

        // View Timeline button logic
        viewTimelineButton.setOnClickListener(v -> {
            Intent intent = new Intent(UploadActivity.this, PatientTimelineActivity.class);
            intent.putExtra("PATIENT_ID", patientId);
            startActivity(intent);
        });

        // Delete Patient Timeline button logic
        deletePatientButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Timeline")
                    .setMessage("Are you sure you want to delete the patient's timeline?")
                    .setPositiveButton("Yes", (dialog, which) -> deletePatientTimeline(patientId))
                    .setNegativeButton("No", null)
                    .show();
        });

        // Remove Patient button logic
        removePatientButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Remove Patient")
                    .setMessage("Are you sure you want to remove the patient from the screen?")
                    .setPositiveButton("Yes", (dialog, which) -> removePatientFromScreenAndNavigate())
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    // Method to delete patient timeline data from Firebase
    private void deletePatientTimeline(String patientId) {
        // Reference to Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("patientTimeline")
                .child(patientId);

        // Remove the patient's timeline data
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UploadActivity.this, "Patient timeline deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UploadActivity.this, "Failed to delete patient timeline", Toast.LENGTH_SHORT).show();
                if (task.getException() != null) {
                    Log.e("UploadActivity", "Deletion error: " + task.getException().getMessage());
                }
            }
        });
    }

    // Method to remove patient details from the screen and navigate back to ProfileActivity
    private void removePatientFromScreenAndNavigate() {
        // Navigate back to ProfileActivity and pass the patient ID to remove
        Intent intent = new Intent(UploadActivity.this, ProfileActivity.class);
        intent.putExtra("REMOVED_PATIENT_ID", patientId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the current activity so it is removed from the back stack
    }
}