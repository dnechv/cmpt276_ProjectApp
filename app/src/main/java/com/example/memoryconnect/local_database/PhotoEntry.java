package com.example.memoryconnect.local_database;

public class PhotoEntry {

    //fields

    private String id;
    private String title;
    private String patientId;
    private String date;
    private String photoUrl;
    private String youtubeUrl;
    private long timeWhenPhotoAdded;

    //no arg
    public PhotoEntry(String id, String title, String url, String patientId3, long l) {}

    //constructor with arugments
    public PhotoEntry(String id, String title, String patientId, String date, String photoUrl, String youtubeUrl, long timeWhenPhotoAdded) {
        this.id = id;
        this.title = title;
        this.patientId = patientId;
        this.date = date;
        this.photoUrl = photoUrl;
        this.youtubeUrl = youtubeUrl;
        this.timeWhenPhotoAdded = timeWhenPhotoAdded;
    }

    // Methods (Getters and Setters)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public long getTimeWhenPhotoAdded() {
        return timeWhenPhotoAdded;
    }

    public void setTimeWhenPhotoAdded(long timeWhenPhotoAdded) {
        this.timeWhenPhotoAdded = timeWhenPhotoAdded;
    }
}