package com.example.memoryconnect.model;


//imports
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;



//defined data class for the photo entry
//uses foreginKey to link to the patient


//room entety for photo entry
@Entity(tableName = "photos",
        foreignKeys = @ForeignKey(entity = Patient.class,
                parentColumns = "id",
                childColumns = "patientId",
                onDelete = ForeignKey.CASCADE), //deletes photos whe the patient is deleted
        indices = {@Index("patientId")})

public class PhotoEntry {

    @NonNull // each photo has no null id with unique identifier
    @PrimaryKey // ensures every row has a unique identifier
    private String id;
    private String photoUrl;
    private String youtubeUrl;
    private String patientId; // foreign key that links to the patient
    private long timeWhenPhotoAdded;
    private String title;
    private String date;

  // photos
    public PhotoEntry(@NonNull String id, String title, String photoUrl, long timeWhenPhotoAdded) {
        this.id = id;
        this.title = title;
        this.photoUrl = photoUrl;
        this.patientId = patientId;
        this.timeWhenPhotoAdded = timeWhenPhotoAdded;
        this.youtubeUrl = null;
    }

    // yotube
    public PhotoEntry(@NonNull String id, String title, String youtubeUrl, String patientId, long timeWhenPhotoAdded, boolean isYouTube) {
        this.id = id;
        this.title = title;
        this.youtubeUrl = youtubeUrl;
        this.patientId = patientId;
        this.timeWhenPhotoAdded = timeWhenPhotoAdded;
        this.photoUrl = null;
    }


    //set patient id
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    //set time when photo added
    public void setTimeWhenPhotoAdded(long timeWhenPhotoAdded) {
        this.timeWhenPhotoAdded = timeWhenPhotoAdded;
    }


    //set photo url
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    //no arg constructor
    public PhotoEntry() {
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }


    public String getId() {
        return id;
    }

    // set photo id
    public void setId(String id) {
        this.id = id;
    }

    public long getTimeWhenPhotoAdded(){
        return timeWhenPhotoAdded;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPatientId() {
        return patientId;
    }

    //getTitle
    public String getTitle() {
        return title;
    }

    //setTitle
    public void setTitle(String title) {
        this.title = title;
    }

    //getDate
    public String getDate() {
        return date;
    }

    //setDate
    public void setDate(String date) {
        this.date = date;
    }

    public String toString() {
        return "PhotoEntry{" +
                "id='" + id + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", patientId='" + patientId + '\'' +
                ", timeWhenPhotoAdded=" + timeWhenPhotoAdded +
                '}';
    }
}
