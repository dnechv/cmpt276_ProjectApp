package com.example.memoryconnect.splash;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memoryconnect.R;
import com.example.memoryconnect.caregiver_main_screen;

public class SplashActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Play sound
        mediaPlayer = MediaPlayer.create(this, R.raw.splash);
        mediaPlayer.start();

        // Delay to navigate to MainActivity
        new Handler().postDelayed(() -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            Intent intent = new Intent(SplashActivity.this, caregiver_main_screen.class);
            startActivity(intent);
            finish();
        }, 2000); // delay for 3 seconds
    }
}
