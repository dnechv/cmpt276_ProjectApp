package com.example.memoryconnect;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.local_database.LocaldatabaseDao;
import com.example.memoryconnect.local_database.PinEntry;

import java.util.concurrent.Executors;

import com.example.memoryconnect.local_database.LocalDatabase;

public class pin_selection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_pin);

        // Initialize views
        EditText pinEntry = findViewById(R.id.pin_entry);
        Button confirmPin = findViewById(R.id.btn_confirm_pin);

        // Get DAO instance
        LocaldatabaseDao dao = LocalDatabase.getDatabase(this).localdatabaseDao();

        confirmPin.setOnClickListener(v -> {
            String pin = pinEntry.getText().toString();

            if (pin.length() == 4) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    if (dao.isPinExists(pin)) {
                        runOnUiThread(() -> {
                            Toast.makeText(pin_selection.this, "PIN already set", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        dao.insertPin(new PinEntry(pin));
                        runOnUiThread(() -> {
                            Toast.makeText(pin_selection.this, "PIN Set Successfully", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } else {
                Toast.makeText(this, "Please enter a 4-digit PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
