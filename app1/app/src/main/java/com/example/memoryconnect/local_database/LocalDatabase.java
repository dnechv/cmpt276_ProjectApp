package com.example.memoryconnect.local_database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// The @Database annotation must annotate the LocalDatabase class
@Database(entities = {PinEntry.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

    // Singleton instance
    private static volatile LocalDatabase INSTANCE;

    // DAO access
    public abstract LocaldatabaseDao localdatabaseDao();

    // Get the database instance
    public static LocalDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    LocalDatabase.class, "memoryconnect_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}