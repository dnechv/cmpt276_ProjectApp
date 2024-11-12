package com.example.memoryconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditUserActivity extends AppCompatActivity {
    private EditText editName, editNickname, editAge, editComment;
    private Button saveButton;
    private DatabaseReference userRef;
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

        // Get the user ID from the Intent
        userId = getIntent().getStringExtra("user_id");
        if (userId == null) {
            Toast.makeText(this, "Error: User ID not provided", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if userId is missing
            return;
        }

        // Initialize userRef to point to the specific user's data in Firebase
        userRef = FirebaseDatabase.getInstance().getReference("patients").child(userId);

        // Load existing data from Firebase
        loadUserData();

        // Save updated data to Firebase
        saveButton.setOnClickListener(v -> {
            String name = editName.getText().toString();
            String nickname = editNickname.getText().toString();
            int age = Integer.parseInt(editAge.getText().toString());
            String comment = editComment.getText().toString();

            userRef.child("name").setValue(name);
            userRef.child("nickname").setValue(nickname);
            userRef.child("age").setValue(age);
            userRef.child("comment").setValue(comment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditUserActivity.this, "Info updated successfully", Toast.LENGTH_SHORT).show();

                            // Navigate back to caregiver_main_screen
                            Intent intent = new Intent(EditUserActivity.this, caregiver_main_screen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish(); // Close EditUserActivity
                        } else {
                            Toast.makeText(EditUserActivity.this, "Failed to update info", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void loadUserData() {
        // Retrieve data from Firebase and display it in EditText fields
        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Get values from Firebase
                String name = snapshot.child("name").getValue(String.class);
                String nickname = snapshot.child("nickname").getValue(String.class);
                Long age = snapshot.child("age").getValue(Long.class); // Use Long for Firebase integers
                String comment = snapshot.child("comment").getValue(String.class);

                // Populate EditText fields with the retrieved data
                editName.setText(name);
                editNickname.setText(nickname);
                editAge.setText(age != null ? String.valueOf(age) : "");
                editComment.setText(comment);
            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
        });
    }
}
