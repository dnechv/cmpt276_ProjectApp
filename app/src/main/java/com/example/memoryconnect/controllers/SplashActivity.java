package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.caregiver_main_screen;
import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.local_database.LocaldatabaseDao;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Run database check in background
        new CheckPinTask().execute();
    }

    // AsyncTask to query PIN from the database
    private class CheckPinTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // Get the DAO instance and query the PIN from the database
            LocaldatabaseDao dao = LocalDatabase.getDatabase(SplashActivity.this).localdatabaseDao();
            return dao.getPin();  // This returns the stored PIN or null if not found
        }

        @Override
        protected void onPostExecute(String storedPin) {
            super.onPostExecute(storedPin);

            // Check if the PIN exists or not
            if (storedPin == null) {
                // If no PIN exists, go directly to Caregiver Main Screen
                Intent intent = new Intent(SplashActivity.this, caregiver_main_screen.class);
                startActivity(intent);
            } else {
                // If PIN exists, go to Enter PIN activity
                Intent intent = new Intent(SplashActivity.this, EnterPinActivity.class);
                intent.putExtra("STORED_PIN", storedPin);
                startActivity(intent);
            }

            finish();  // Finish SplashActivity so the user can't go back to it
        }
    }
}
