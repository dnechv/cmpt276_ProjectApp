package com.example.memoryconnect.model;

import androidx.annotation.NonNull;

// Defines a data class for timeline entries (photos or YouTube links)
public class PhotoEntry {
    @NonNull
    private String id;
    private String photoUrl;
    private String youtubeUrl;
    private String patientId;
    private long timestamp;
    private String title;

    // Default no-argument constructor (required for Firebase)
    public PhotoEntry() {}

    // Constructor for photo entries
    public PhotoEntry(@NonNull String id, String title, String photoUrl, long timestamp) {
        this.id = id;
        this.title = title;
        this.photoUrl = photoUrl;
        this.timestamp = timestamp;
        this.youtubeUrl = null;
    }

    // Constructor for YouTube entries
    public PhotoEntry(@NonNull String id, String title, String youtubeUrl, String patientId, long timestamp) {
        this.id = id;
        this.title = title;
        this.youtubeUrl = youtubeUrl;
        this.patientId = patientId;
        this.timestamp = timestamp;
        this.photoUrl = null;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "PhotoEntry{" +
                "id='" + id + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", youtubeUrl='" + youtubeUrl + '\'' +
                ", patientId='" + patientId + '\'' +
                ", timestamp=" + timestamp +
                ", title='" + title + '\'' +
                '}';
    }
}