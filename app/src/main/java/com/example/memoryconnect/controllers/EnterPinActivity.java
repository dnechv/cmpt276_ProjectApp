package com.example.memoryconnect.controllers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.local_database.LocaldatabaseDao;

import java.util.concurrent.Executors;

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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.memoryconnect.R;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.local_database.LocaldatabaseDao;
import com.example.memoryconnect.local_database.PinEntry;

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
                            // Proceed to the next activity
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
