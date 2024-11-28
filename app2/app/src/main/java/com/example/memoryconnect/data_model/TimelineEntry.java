package com.example.memoryconnect.data_model;


//this is to hold timeline entry

public class TimelineEntry {
    private String title;
    private String photoUrl;
    private String youtubeLink;

    public TimelineEntry(String title, String photoUrl, String youtubeLink) {
        this.title = title;
        this.photoUrl = photoUrl;
        this.youtubeLink = youtubeLink;
    }

    public String getTitle() {
        return title;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }
}