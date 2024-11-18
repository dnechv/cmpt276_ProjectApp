package com.example.memoryconnect;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class pin_selection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.select_pin);


        //find inputs
        EditText pinEntry = findViewById(R.id.pin_entry);
        Button confirmPin = findViewById(R.id.btn_confirm_pin);

        confirmPin.setOnClickListener(v -> {
            String pin = pinEntry.getText().toString();

            if (pin.length() == 4) {



                Toast.makeText(this, "PIN Set Successfully", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(this, "Please enter a 4-digit PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }
}