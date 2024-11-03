package com.example.memoryconnect.local_database;


//imports
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.RoomDatabase;

import com.example.memoryconnect.model.Patient;

//contains code for the local database - manages the local database through dao
@Database(entities = {Patient.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

    //get dao
    public abstract LocaldatabaseDao localdatabaseDao();
}
