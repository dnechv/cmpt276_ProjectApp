package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoryconnect.R;
import com.example.memoryconnect.adaptor.PatientAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView welcomeText, emailText;
    private Button logoutButton, addMemberButton;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String userId;

    private PatientAdapter adapter;
    private List<String> patientNames;
    private List<String> patientIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            String email = currentUser.getEmail();

            // Initialize UI elements
            welcomeText = findViewById(R.id.welcome_text);
            emailText = findViewById(R.id.email_text);
            logoutButton = findViewById(R.id.logout_button);
            addMemberButton = findViewById(R.id.add_member_button);
            recyclerView = findViewById(R.id.recycler_view);

            welcomeText.setText("Welcome!");
            emailText.setText("Email: " + email);

            // Initialize RecyclerView
            patientNames = new ArrayList<>();
            patientIds = new ArrayList<>();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new PatientAdapter(patientNames, patientIds, (patientName, patientId) -> {
                Intent intent = new Intent(ProfileActivity.this, UploadActivity.class);
                intent.putExtra("PATIENT_NAME", patientName);
                intent.putExtra("PATIENT_ID", patientId);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);

            fetchLinkedPatients();

            addMemberButton.setOnClickListener(v -> showAddPatientDialog());

            logoutButton.setOnClickListener(v -> {
                mAuth.signOut();
                Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchLinkedPatients() {
        databaseReference.child("Users").child(userId).child("linkedPatients")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        patientNames.clear();
                        patientIds.clear();
                        for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                            String patientId = patientSnapshot.getKey();
                            String patientName = patientSnapshot.getValue(String.class);
                            if (patientId != null && patientName != null) {
                                patientIds.add(patientId);
                                patientNames.add(patientName);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, "Failed to fetch patients.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddPatientDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Patient");

        // Use EditText instead of TextView to allow user input
        final EditText input = new EditText(this);
        input.setHint("Enter Patient ID");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String patientId = input.getText().toString().trim();
            if (!patientId.isEmpty()) {
                addPatient(patientId);
            } else {
                Toast.makeText(this, "Patient ID cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void addPatient(String patientId) {
        databaseReference.child("patients").child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String patientName = snapshot.child("name").getValue(String.class);
                    databaseReference.child("Users").child(userId).child("linkedPatients").child(patientId)
                            .setValue(patientName)
                            .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Patient added successfully.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to add patient.", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(ProfileActivity.this, "Patient ID not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error adding patient.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
