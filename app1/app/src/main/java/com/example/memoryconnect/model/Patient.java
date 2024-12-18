package com.example.memoryconnect.model;

import androidx.annotation.NonNull;

import java.util.Map;

public class Patient {
    @NonNull
    private String id;
    private String name;
    private String nickname;
    private int age;
    private String comment;
    private String photoUrl;

    // New field for timeline entries
    private Map<String, PhotoEntry> timelineEntries;

    // Default constructor for Firebase
    public Patient() {
    }

    // Constructor with all fields
    public Patient(@NonNull String id, String name, String nickname, int age, String comment, String photoUrl) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.age = age;
        this.comment = comment;
        this.photoUrl = photoUrl;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Map<String, PhotoEntry> getTimelineEntries() {
        return timelineEntries;
    }

    public void setTimelineEntries(Map<String, PhotoEntry> timelineEntries) {
        this.timelineEntries = timelineEntries;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", age=" + age +
                ", comment='" + comment + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", timelineEntries=" + timelineEntries +
                '}';
    }
}