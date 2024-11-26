package com.example.memoryconnect;


//youtube web player


//imports
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class youtube_view extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.youtube_player);



        WebView webView = findViewById(R.id.youtube_webview);


        String videoId = getIntent().getStringExtra("VIDEO_ID");

        //load url
        String videoUrl = "https://www.youtube.com/embed/" + videoId + "?autoplay=1";

        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(videoUrl);
    }
}

