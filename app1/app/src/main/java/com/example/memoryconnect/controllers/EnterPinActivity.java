package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.memoryconnect.caregiver_main_screen;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;
import com.example.memoryconnect.local_database.LocaldatabaseDao;
import com.example.memoryconnect.local_database.LocalDatabase;

import java.util.concurrent.Executors;

public class EnterPinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_pin);

        // Initialize views
        EditText pinEntry = findViewById(R.id.pin_entry);
        Button confirmPinButton = findViewById(R.id.btn_confirm_pin);

        // Get DAO instance
        LocaldatabaseDao dao = LocalDatabase.getDatabase(this).localdatabaseDao();

        confirmPinButton.setOnClickListener(v -> {
            String enteredPin = pinEntry.getText().toString();

            if (enteredPin.length() == 4) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    String storedPin = dao.getPin();  // Assuming you have a method to retrieve the PIN
                    if (enteredPin.equals(storedPin)) {
                        runOnUiThread(() -> {
                            Toast.makeText(EnterPinActivity.this, "PIN matched!", Toast.LENGTH_SHORT).show();

                            // Create an intent to start the caregiver_main_screen
                            Intent intent = new Intent(EnterPinActivity.this, caregiver_main_screen.class);
                            startActivity(intent);

                            // Optionally, finish this activity so the user cannot go back
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(EnterPinActivity.this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } else {
                Toast.makeText(EnterPinActivity.this, "Please enter a valid 4-digit PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

