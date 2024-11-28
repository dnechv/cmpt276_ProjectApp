package com.example.memoryconnect.local_database;

public class PhotoEntry {

    // Fields
    private String id;
    private String title;
    private String patientId;
    private String date;
    private String photoUrl;
    private String youtubeUrl;
    private long timeWhenPhotoAdded;
    private String songName;
    private String photoDescription;

    // No-arg constructor
    public PhotoEntry() {}

    // All-arg constructor
    public PhotoEntry(String id, String title, String patientId, String date, String photoUrl, String youtubeUrl, long timeWhenPhotoAdded, String songName, String photoDescription) {
        this.id = id;
        this.title = title;
        this.patientId = patientId;
        this.date = date;
        this.photoUrl = photoUrl;
        this.youtubeUrl = youtubeUrl;
        this.timeWhenPhotoAdded = timeWhenPhotoAdded;
        this.songName = songName;  // Set song name for YouTube entries
        this.photoDescription = photoDescription;  // Set photo description for photo entries
    }

    // Photo entry constructor (for photo events only)
    public PhotoEntry(String id, String title, String photoUrl, long timeWhenPhotoAdded, String date, String photoDescription) {
        this.id = id;
        this.title = title;
        this.photoUrl = photoUrl;
        this.timeWhenPhotoAdded = timeWhenPhotoAdded;
        this.date = date;
        this.patientId = null;
        this.youtubeUrl = null;
        this.songName = null;  // No song name for photo events
        this.photoDescription = photoDescription;  // Set the photo description
    }

    // YouTube constructor (for YouTube events only)
    public PhotoEntry(String id, String title, String youtubeUrl, String patientId, long timeWhenPhotoAdded, String songName) {
        this.id = id;
        this.title = title;
        this.youtubeUrl = youtubeUrl;
        this.patientId = patientId;
        this.timeWhenPhotoAdded = timeWhenPhotoAdded;
        this.photoUrl = null;  // No photo URL for YouTube events
        this.date = null;
        this.songName = songName;  // Set the song name
        this.photoDescription = null;  // No photo description for YouTube events
    }


    //methods
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

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getPhotoDescription() {
        return photoDescription;
    }

    public void setPhotoDescription(String photoDescription) {
        this.photoDescription = photoDescription;
    }

    @Override
    public String toString() {
        return "PhotoEntry{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", patientId='" + patientId + '\'' +
                ", date='" + date + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", youtubeUrl='" + youtubeUrl + '\'' +
                ", timeWhenPhotoAdded=" + timeWhenPhotoAdded +
                ", songName='" + songName + '\'' +
                ", photoDescription='" + photoDescription + '\'' +
                '}';
    }
}