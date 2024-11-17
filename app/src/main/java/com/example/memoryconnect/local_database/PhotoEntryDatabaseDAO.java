package com.example.memoryconnect.local_database;


//Imports

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Update;

import com.example.memoryconnect.model.PhotoEntry;


//DAO - interface for database operations
//contains read, update, delete insert operations


import java.util.*;
@Dao
public interface PhotoEntryDatabaseDAO {
    @Insert
    void insert(PhotoEntry photoEntry);
    @Update
    void update(PhotoEntry photoEntry);
    @Delete
    void delete(PhotoEntry photoEntry);


    //get all patient photos
    @Query("SELECT * FROM photos WHERE patientId = :patientId")
    LiveData<List<PhotoEntry>> getAllPhotosForPatient(String patientId);

    //get all photos
    @Query("SELECT * FROM photos")
    LiveData<List<PhotoEntry>> getAllPhotos();

    //delete all photos
    @Query("DELETE FROM photos WHERE patientId = :patientId")
    void deletePhotosByPatient(String patientId);

    @Query("SELECT EXISTS(SELECT 1 FROM patients WHERE id = :patientId)")
    int isPatientIdExists(String patientId);





}


