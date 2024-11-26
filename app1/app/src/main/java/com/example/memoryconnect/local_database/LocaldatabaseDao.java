package com.example.memoryconnect.local_database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.memoryconnect.model.Patient;
import com.example.memoryconnect.model.PhotoEntry;

import java.util.List;

//DAO - Data Access Object
//only pin functionality left

@Dao
public interface LocaldatabaseDao {


        // Insert a PIN
        @Insert
        void insertPin(PinEntry pinEntry);

        // Get the stored PIN
        @Query("SELECT pin FROM pin_table LIMIT 1")
        String getPin();

        // Check existing PIN
        @Query("SELECT EXISTS(SELECT 1 FROM pin_table WHERE pin = :pin LIMIT 1)")
        boolean isPinExists(String pin);

        //get all PIN
        @Query("SELECT pin FROM pin_table")
        LiveData<List<String>> getAllPins();


}
