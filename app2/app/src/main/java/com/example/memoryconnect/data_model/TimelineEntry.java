package com.example.memoryconnect.data_model;

//data model for timeline entry
public class TimelineEntry {
    private String title;
    private String photoUrl;
    private String youtubeLink;
    private String songName;
    private String photoDescription;

    //no arg constructor
    public TimelineEntry() {}

    //constructor -> data fields
    public TimelineEntry(String title, String photoUrl, String youtubeLink, String songName, String photoDescription) {
        this.title = title;
        this.photoUrl = photoUrl;
        this.youtubeLink = youtubeLink;
        this.songName = songName;
        this.photoDescription = photoDescription;
    }


    //methods


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

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
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
}