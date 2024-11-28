package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView welcomeText, emailText;
    private Button logoutButton, addMemberButton;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String userId;
    private PatientAdapter adapter;
    private List<String> linkedPatients;


    //map to store patient id and name
    private Map<String, String> patientIdToNameMap; // Maps Patient ID to Patient Name

    //4804a8a7-f06e-4e2c-9574-b1b6ba273953
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth and Database


        //hash map for name and patient id
        patientIdToNameMap = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            String email = currentUser.getEmail();

            // UI references
            welcomeText = findViewById(R.id.welcome_text);
            emailText = findViewById(R.id.email_text);
            logoutButton = findViewById(R.id.logout_button);
            addMemberButton = findViewById(R.id.add_member_button);
            recyclerView = findViewById(R.id.recycler_view);

            welcomeText.setText("Welcome!");
            emailText.setText("Email: " + email);

            // Initialize RecyclerView
            linkedPatients = new ArrayList<>();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new PatientAdapter(linkedPatients, patientIdToNameMap, patientId -> {
                // Open UploadActivity when a patient is clicked
                Intent intent = new Intent(ProfileActivity.this, UploadActivity.class);
                intent.putExtra("PATIENT_ID", patientId);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);

            // Fetch linked patients
            fetchLinkedPatients();

            // Add Member button logic
            addMemberButton.setOnClickListener(v -> showAddPatientDialog());

            // Logout button logic


            logoutButton.setOnClickListener(v -> {
                mAuth.signOut();
                Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            });
        } else {

            // Redirect to login if no user is logged in
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void fetchLinkedPatients() {
        databaseReference.child("Users").child(userId).child("linkedPatients")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        linkedPatients.clear();
                        patientIdToNameMap.clear();

                        for (DataSnapshot patientSnapshot : snapshot.getChildren()) {
                            String patientId = patientSnapshot.getKey();

                            // Fetch the patient name from the "patients" node
                            databaseReference.child("patients").child(patientId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot patientDataSnapshot) {
                                            String patientName = patientDataSnapshot.child("name").getValue(String.class);

                                            // If name is null, fallback to the ID for display
                                            if (patientName == null) {
                                                patientName = patientId;
                                            }

                                            // Update the map and list
                                            patientIdToNameMap.put(patientId, patientName);
                                            linkedPatients.add(patientId);

                                            // Notify adapter about data changes
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("FetchLinkedPatients", "Failed to fetch patient details: " + error.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, "Failed to load patients.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddPatientDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Patient");

        // Set up input field
        final EditText input = new EditText(this);
        input.setHint("Enter Patient ID");
        builder.setView(input);

        // Set up buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            String patientId = input.getText().toString().trim();
            if (!patientId.isEmpty()) {
                addPatient(patientId);
            } else {
                Toast.makeText(ProfileActivity.this, "Patient ID cannot be empty", Toast.LENGTH_SHORT).show();
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


                    String name = snapshot.child("name").getValue(String.class);
                    if (name == null) name = "Unknown";

                    Long age = snapshot.child("age").getValue(Long.class);


                    String ageString = (age != null) ? String.valueOf(age) : "Unknown Age";

                    String nickname = snapshot.child("nickname").getValue(String.class);
                    if (nickname == null) nickname = "No Nickname";

                    String comment = snapshot.child("comment").getValue(String.class);
                    if (comment == null) comment = "No Comment";

                    String photoUrl = snapshot.child("photoUrl").getValue(String.class);
                    if (photoUrl == null) photoUrl = "No Photo URL";


                    Log.d("ProfileActivity", "Patient Data: Name: " + name + ", Age: " + ageString +
                            ", Nickname: " + nickname + ", Comment: " + comment +
                            ", Photo URL: " + photoUrl);

                    // Add the patient to the user's linkedPatients
                    databaseReference.child("Users").child(userId).child("linkedPatients").child(patientId)
                            .setValue(true)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ProfileActivity.this, "Patient added successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ProfileActivity.this, "Failed to add patient.", Toast.LENGTH_SHORT).show();
                                Log.e("FirebaseError", "Failed to write linkedPatients: " + e.getMessage());
                            });
                } else {


                    Log.e("ProfileActivity", "Patient ID does not exist.");
                    Toast.makeText(ProfileActivity.this, "Patient ID does not exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


                Log.e("ProfileActivity", "Error retrieving pat" +
                        "Patient data: " + error.getMessage());
                Toast.makeText(ProfileActivity.this, "Error checking patient ID.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
