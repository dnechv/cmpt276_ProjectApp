package com.example.memoryconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.memoryconnect.ViewModel.PatientViewModel;
import com.example.memoryconnect.model.Patient;

public class EditUserActivity extends AppCompatActivity {
    private EditText editName, editNickname, editAge, editComment;
    private Button saveButton;
    private PatientViewModel patientViewModel;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Initialize views
        editName = findViewById(R.id.editName);
        editNickname = findViewById(R.id.editNickname);
        editAge = findViewById(R.id.editAge);
        editComment = findViewById(R.id.editComment);
        saveButton = findViewById(R.id.saveButton);

        // Initialize ViewModel
        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        // Get the user ID from the Intent
        userId = getIntent().getStringExtra("user_id");
        if (userId == null) {
            Toast.makeText(this, "Error: User ID not provided", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if userId is missing
            return;
        }

        // Fetch and observe patient data from ViewModel
        patientViewModel.getPatientById(userId).observe(this, patient -> {
            if (patient != null) {
                // Populate UI with patient data
                editName.setText(patient.getName());
                editNickname.setText(patient.getNickname());
                editAge.setText(String.valueOf(patient.getAge()));
                editComment.setText(patient.getComment());

                // Save button click listener
                saveButton.setOnClickListener(v -> {
                    // Get updated values from the fields
                    String name = editName.getText().toString();
                    String nickname = editNickname.getText().toString();
                    int age = Integer.parseInt(editAge.getText().toString());
                    String comment = editComment.getText().toString();

                    // Create updated Patient object, retaining the existing photo URL
                    Patient updatedPatient = new Patient(
                            userId,
                            name,
                            nickname,
                            age,
                            comment,
                            patient.getPhotoUrl() // Keep the existing photo URL
                    );

                    // Save patient data using ViewModel
                    patientViewModel.savePatient(updatedPatient);

                    // Provide feedback and navigate back to the main screen
                    Toast.makeText(EditUserActivity.this, "Patient updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditUserActivity.this, caregiver_main_screen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close activity after saving
                });
            } else {
                Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show();
                finish(); // Close activity if patient is not found
            }
        });
    }
}
