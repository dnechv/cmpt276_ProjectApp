package com.example.memoryconnect.local_database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.memoryconnect.model.Patient;

import java.util.List;

//DAO - Data Access Object
//defines the methods that access the database - methods defined by DAO so only specify name

@Dao
public interface LocaldatabaseDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(List<Patient> patients);

        @Query("DELETE FROM patients")
        void deleteAll();

        @Query("SELECT * from patients ORDER BY name ASC")
        LiveData<List<Patient>> getAllPatients();
}
