package com.example.memoryconnect.controllers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;
import com.example.memoryconnect.caregiver_main_screen;
import com.example.memoryconnect.local_database.LocalDatabase;
import com.example.memoryconnect.local_database.LocaldatabaseDao;
import com.example.memoryconnect.controllers.EnterPinActivity;

public class SplashActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show the splash screen layout
        setContentView(R.layout.activity_splash);

        // Play sound
        mediaPlayer = MediaPlayer.create(this, R.raw.splash);
        mediaPlayer.start();

        // Delay for 2 seconds to show splash screen before moving to PIN check
        new Handler().postDelayed(() -> {
            // Stop and release the media player after 2 seconds
            mediaPlayer.stop();
            mediaPlayer.release();

            // Now, after the splash screen duration, run the background task to check for PIN
            new CheckPinTask().execute();
        }, 2000); // Delay for 2 seconds (splash screen duration)
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

            // Navigate based on whether a PIN is set
            Intent intent;
            if (storedPin == null) {
                // If no PIN exists, go directly to Caregiver Main Screen
                intent = new Intent(SplashActivity.this, caregiver_main_screen.class);
            } else {
                // If PIN exists, go to Enter PIN activity
                intent = new Intent(SplashActivity.this, EnterPinActivity.class);
                intent.putExtra("STORED_PIN", storedPin);
            }

            startActivity(intent);
            finish();  // Close the splash activity after navigating
        }
    }
}

