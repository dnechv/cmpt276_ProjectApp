package com.example.memoryconnect.controllers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.memoryconnect.R;
import com.example.memoryconnect.ViewModel.PatientViewModel;

public class PatientDetailActivity extends AppCompatActivity {
    private PatientViewModel patientViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_info_fragment);

        // Get the patient ID passed from the intent
        String patientId = getIntent().getStringExtra("PATIENT_ID");

        // Initialize the ViewModel
        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        // Observe patient details and update the UI
        patientViewModel.getPatientById(patientId).observe(this, patient -> {
            if (patient != null) {
                // Update UI with patient details here (e.g., set TextViews with name, age, etc.)
            }
        });
    }
}
