package com.example.memoryconnect.controllers;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UploadActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        // Get patient ID passed from the previous activity
        String patientId = getIntent().getStringExtra("PATIENT_ID");

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Run the Firebase Database Query
        fetchPatientData(patientId);
    }

    private void fetchPatientData(String patientId) {
        databaseReference.child("patients").child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String age = snapshot.child("age").getValue(String.class);
                    String comment = snapshot.child("comment").getValue(String.class);

                    // Log or display data
                    Log.d("FirebaseDebug", "Name: " + name + ", Age: " + age + ", Comment: " + comment);
                } else {
                    Log.e("FirebaseDebug", "Patient not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDebug", "Error: " + error.getMessage());
            }
        });
    }
}
//8a70605d-7cbd-4705-b8d0-51e9c0991a48