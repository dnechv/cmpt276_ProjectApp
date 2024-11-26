package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton, signUpRedirectButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        //check if the user already logged in
        if (mAuth.getCurrentUser() != null) {


            //if user logged in -> profile activity
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));

            finish();

            return;
        }




        // UI references
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginnow);
        signUpRedirectButton = findViewById(R.id.sign_up_button);

        // Login Button Logic
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Redirect to Sign-Up Page
        signUpRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                // Pass email and password to SignUpActivity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            return;
        }

        // Firebase login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                        finish();
                    } else {
                        // Login failed
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
