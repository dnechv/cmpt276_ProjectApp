package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView welcomeText, emailText;
    private Button logoutButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // UI references
        welcomeText = findViewById(R.id.welcome_text);
        emailText = findViewById(R.id.email_text);
        logoutButton = findViewById(R.id.logout_button);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Display user information
            String email = currentUser.getEmail();
            welcomeText.setText("Welcome!");
            emailText.setText("Email: " + email);
        } else {
            // Redirect to login if no user is logged in
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }

        // Logout Button logic
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
