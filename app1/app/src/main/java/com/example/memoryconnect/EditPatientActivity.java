package com.example.memoryconnect;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.memoryconnect.ViewModel.PatientViewModel;
import com.example.memoryconnect.model.Patient;

public class EditPatientActivity extends AppCompatActivity {
    private EditText nameEditText, nicknameEditText, ageEditText, commentEditText;
    private Button saveButton;
    private String patientId;
    private PatientViewModel patientViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient);

        // Initialize ViewModel
        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        // Get patient ID from Intent
        patientId = getIntent().getStringExtra("PATIENT_ID");

        // Initialize UI elements
        nameEditText = findViewById(R.id.editPatientName);
        nicknameEditText = findViewById(R.id.editPatientNickname);
        ageEditText = findViewById(R.id.editPatientAge);
        commentEditText = findViewById(R.id.editPatientComment);
        saveButton = findViewById(R.id.savePatientButton);

        // Fetch patient details to pre-fill the form
        patientViewModel.getPatientById(patientId).observe(this, patient -> {
            if (patient != null) {
                nameEditText.setText(patient.getName());
                nicknameEditText.setText(patient.getNickname());
                ageEditText.setText(String.valueOf(patient.getAge()));
                commentEditText.setText(patient.getComment());
            }
        });

        // Handle Save button click
        saveButton.setOnClickListener(v -> {
            String updatedName = nameEditText.getText().toString();
            String updatedNickname = nicknameEditText.getText().toString();
            int updatedAge = Integer.parseInt(ageEditText.getText().toString());
            String updatedComment = commentEditText.getText().toString();

            // Create updated Patient object
            Patient updatedPatient = new Patient(patientId, updatedName, updatedNickname, updatedAge, updatedComment, null);

            // Update patient in Firebase
            patientViewModel.updatePatient(updatedPatient).observe(this, success -> {
                if (success) {
                    Toast.makeText(this, "Patient updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Navigate back to caregiver_main_screen
                } else {
                    Toast.makeText(this, "Failed to update patient.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
