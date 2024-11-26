package com.example.memoryconnect;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MemoryConnect extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //cahce size - 50 mb
        database.setPersistenceCacheSizeBytes(50 * 1024 * 1024);

        // offline firebase
        database.setPersistenceEnabled(true);

        // synch data
        database.getReference().keepSynced(true);


    }
}