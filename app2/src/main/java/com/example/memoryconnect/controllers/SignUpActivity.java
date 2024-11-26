package com.example.memoryconnect.controllers;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText fullNameField, emailField, passwordField, confirmPasswordField;
    private Button signUpButton;
    private TextView loginRedirect;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // UI references
        fullNameField = findViewById(R.id.full_name);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        signUpButton = findViewById(R.id.sign_up_button);

        // Retrieve email and password passed from LoginActivity
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");

        // Pre-fill fields
        if (email != null) {
            emailField.setText(email);
        }
        if (password != null) {
            passwordField.setText(password);
        }

        // Set up the Sign-Up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
    }

    private void signUpUser() {
        String fullName = fullNameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Validate user input
        if (TextUtils.isEmpty(fullName)) {
            fullNameField.setError("Full Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters long");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords do not match");
            return;
        }

        // Create a new user with Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign-up successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();

                        // Redirect to ProfileActivity
                        startActivity(new Intent(SignUpActivity.this, ProfileActivity.class));
                        finish();
                    } else {
                        // Sign-up failed
                        Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
