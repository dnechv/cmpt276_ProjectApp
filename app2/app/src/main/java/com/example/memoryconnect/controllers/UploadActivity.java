package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;

public class UploadActivity extends AppCompatActivity {

    private TextView patientDetailsTextView;
    private Button addToTimelineButton;
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
        addToTimelineButton = findViewById(R.id.addPhotosButton); // Button ID remains the same for compatibility

        // Display patient details
        if (patientName != null) {
            patientDetailsTextView.setText("Name: " + patientName);
        } else {
            patientDetailsTextView.setText("Patient details not found.");
        }

        // Add to Timeline button logic
        addToTimelineButton.setOnClickListener(v -> {
            Intent intent = new Intent(UploadActivity.this, AddToTimelineActivity.class); // Updated activity
            intent.putExtra("PATIENT_ID", patientId);
            startActivity(intent);
        });
    }
}
