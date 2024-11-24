package com.example.memoryconnect.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "photos") // Mark this class as a Room Entity
public class PhotoEntry {

    @PrimaryKey
    @NonNull
    private String id; // Primary Key

    private String title;
    private String photoUrl;
    private String youtubeUrl;
    private String patientId;
    private long timeWhenPhotoAdded;

    // Default constructor for Firebase
    public PhotoEntry() {
        // Required empty constructor
    }

    // Constructor
    public PhotoEntry(@NonNull String id, String title, String photoUrl, String youtubeUrl, String patientId, long timeWhenPhotoAdded) {
        this.id = id;
        this.title = title;
        this.photoUrl = photoUrl;
        this.youtubeUrl = youtubeUrl;
        this.patientId = patientId;
        this.timeWhenPhotoAdded = timeWhenPhotoAdded;
    }

    // Getters and Setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public long getTimeWhenPhotoAdded() {
        return timeWhenPhotoAdded;
    }

    public void setTimeWhenPhotoAdded(long timeWhenPhotoAdded) {
        this.timeWhenPhotoAdded = timeWhenPhotoAdded;
    }
}
