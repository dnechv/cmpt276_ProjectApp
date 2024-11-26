package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;
import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.local_database.LocaldatabaseDao;
import com.example.memoryconnect.local_database.PinEntry;

import java.util.concurrent.Executors;

public class AddPinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout first
        setContentView(R.layout.select_pin);

        // Get DAO instance
        LocaldatabaseDao dao = LocalDatabase.getDatabase(this).localdatabaseDao();

        // Initialize views
        EditText pinEntry = findViewById(R.id.pin_entry);
        Button confirmPinButton = findViewById(R.id.btn_confirm_pin);


        // Check if PIN is already set
        Executors.newSingleThreadExecutor().execute(() -> {
            if (dao.getPin() != null) {
                // If PIN is set, navigate to the EnterPinActivity
                runOnUiThread(() -> {
                    Intent intent = new Intent(AddPinActivity.this, EnterPinActivity.class);
                    startActivity(intent);
                    finish(); // Close the AddPinActivity so it's not in the back stack
                });
            } else {
                // If no PIN is set, show the PIN setup screen
                runOnUiThread(() -> {
                    confirmPinButton.setOnClickListener(v -> {
                        String pin = pinEntry.getText().toString();

                        if (pin.length() == 4) {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                if (dao.isPinExists(pin)) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(AddPinActivity.this, "PIN already set", Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    dao.insertPin(new PinEntry(pin));
                                    runOnUiThread(() -> {
                                        Toast.makeText(AddPinActivity.this, "PIN Set Successfully", Toast.LENGTH_SHORT).show();
                                        // Navigate to EnterPinActivity after setting the PIN
                                        Intent intent = new Intent(AddPinActivity.this, EnterPinActivity.class);
                                        startActivity(intent);
                                        finish(); // Close the AddPinActivity
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(this, "Please enter a 4-digit PIN", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Set up the button click listener for checking the PIN
                    /*
                    checkPinButton.setOnClickListener(v -> {
                        // Start EnterPinActivity when Check PIN is clicked
                        Intent intent = new Intent(AddPinActivity.this, EnterPinActivity.class);
                        startActivity(intent);
                        finish(); // Close AddPinActivity if PIN is already set
                    });
                    */

                });
            }
        });
    }
}
