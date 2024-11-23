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
//defines the methods that access the database - methods defined by DAO so only specify name

@Dao
public interface LocaldatabaseDao {

        //insert photo
        @Query("SELECT * FROM photos WHERE patientId = :patientId")
        LiveData<List<PhotoEntry>> getAllPhotosForPatient(String patientId);

        //insert patient
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(List<Patient> patients);


        //insert patient
        @Query("DELETE FROM patients")
        void deleteAll();


        //get all patients
        @Query("SELECT * from patients ORDER BY name ASC")
        LiveData<List<Patient>> getAllPatients();


        //get patient by id
        @Query("SELECT * FROM patients WHERE id = :patientId LIMIT 1")
        LiveData<Patient> getPatientById(String patientId);

        //insert photo
        @Insert
        void insertPhotoEntry(PhotoEntry photoEntry);

        //isPatientExists -> uses int so anything bigger than 1 is true
        @Query("SELECT EXISTS(SELECT 1 FROM patients WHERE id = :patientId)")
        int isPatientIdExists(String patientId);


        //get all ids
        @Query("SELECT id FROM patients")
        List<String> getAllPatientIds();

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
