package com.example.memoryconnect.local_database;


//imports
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.memoryconnect.model.Patient;

import java.util.*;



//DAO - interface for database operations for patinet

@Dao
public interface PatientDAO {
    // Insert a list of patients into the Room database
    @Insert
    void insert(List<Patient> patients);

    // Delete all patients from the Room database
    @Query("DELETE FROM patients")
    void deleteAll();

    // Get all patients from the Room database
    @Query("SELECT * FROM patients")
    LiveData<List<Patient>> getAllPatients();

    // Get a patient by their ID
    @Query("SELECT * FROM patients WHERE id = :patientId")
    LiveData<Patient> getPatientById(String patientId);


}
