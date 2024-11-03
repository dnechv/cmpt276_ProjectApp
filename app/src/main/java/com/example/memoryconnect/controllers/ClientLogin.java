package com.example.memoryconnect.controllers;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.Observer;
import com.example.memoryconnect.R;
import com.example.memoryconnect.repository.PatientRepository;
import com.example.memoryconnect.model.Patient;

public class ClientLogin extends AppCompatActivity {

    private Button loginButton;
    private Button signupButton;
    private TextView sign_up_text;
    private EditText password;
    private EditText email;

    // Hardcoded for now for testing TODO: implement actual login at login page *for now if database is edited,patient ID have to be updated manually*
    private static final String PATIENT_ID = "9ae502de-0b61-4227-8481-e5cc35d70a52";
    private PatientRepository patientRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_login);

        loginButton = findViewById(R.id.loginnow);
        signupButton = findViewById(R.id.sign_up_button);
        sign_up_text = findViewById(R.id.sign_up_text);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);

        patientRepository = new PatientRepository();

        // Set up button listeners
        loginButton.setOnClickListener(v -> loginStep());
        signupButton.setOnClickListener(v -> signupStep());
    }

    private void loginStep() {
        // Retrieve input from the EditText fields
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        //TODO:validation
    /*
    if (emailInput.isEmpty() || passwordInput.isEmpty()) {
        Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
        return;
    }
    */

        // TODO: login implementation
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();

        patientRepository.getPatientById(PATIENT_ID).observe(this, new Observer<Patient>() {
            @Override
            public void onChanged(Patient patient) {
                if (patient != null) {
                    loginActivity(PATIENT_ID);
                } else {
                    Toast.makeText(ClientLogin.this, "Patient not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signupStep() {
        // TODO: signup implementation
        Toast.makeText(this, "Navigating to Sign Up...", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, SignupActivity.class);
        // startActivity(intent);
    }

    private void loginActivity(String patientId) {
        Intent intent = new Intent(ClientLogin.this, patient_screen_that_displays_tab_layout.class);
        intent.putExtra("PATIENT_ID", patientId);
        startActivity(intent);
    }
}

