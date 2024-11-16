package com.example.memoryconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.memoryconnect.ViewModel.PatientViewModel;

public class DeleteUserActivity extends AppCompatActivity {

    private Button yesButton, noButton;
    private PatientViewModel patientViewModel;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        // Initialize views
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        // Initialize ViewModel
        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        // Get the user ID from the Intent
        userId = getIntent().getStringExtra("user_id");
        if (userId == null) {
            Toast.makeText(this, "Error: User ID not provided", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if userId is missing
            return;
        }

        // Handle "Yes" button click (delete user data and photo)
        yesButton.setOnClickListener(v -> {
            // Use ViewModel to delete patient and their photo
            patientViewModel.deletePatientAndPhoto(userId, new PatientViewModel.OnDeleteCompleteListener() {
                @Override
                public void onSuccess() {
                    // Show success message
                    Toast.makeText(DeleteUserActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();

                    // Navigate back to caregiver_main_screen
                    Intent intent = new Intent(DeleteUserActivity.this, caregiver_main_screen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    finish(); // Close DeleteUserActivity
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Show failure message
                    Toast.makeText(DeleteUserActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Handle "No" button click (cancel deletion)
        noButton.setOnClickListener(v -> finish()); // Close activity without any action
    }
}
