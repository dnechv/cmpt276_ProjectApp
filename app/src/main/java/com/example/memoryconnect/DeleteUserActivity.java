package com.example.memoryconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteUserActivity extends AppCompatActivity {
    private Button yesButton, noButton;
    private DatabaseReference userRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        // Initialize buttons
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        // Get the user ID from the Intent
        userId = getIntent().getStringExtra("user_id");
        userRef = FirebaseDatabase.getInstance().getReference("patients").child(userId);

        // Handle the "Yes" button click (deletes user data)
        yesButton.setOnClickListener(v -> {
            userRef.removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Show success message
                            Toast.makeText(DeleteUserActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();

                            // Navigate back to caregiver_main_screen
                            Intent intent = new Intent(DeleteUserActivity.this, caregiver_main_screen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            finish(); // Close DeleteUserActivity
                        } else {
                            // Show failure message
                            Toast.makeText(DeleteUserActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Handle the "No" button click (cancel deletion)
        noButton.setOnClickListener(v -> finish()); // Close activity without any action
    }
}
