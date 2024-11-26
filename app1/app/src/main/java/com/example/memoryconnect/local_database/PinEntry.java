package com.example.memoryconnect.local_database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pin_table")
public class PinEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String pin;

    // Constructor
    public PinEntry(String pin) {
        this.pin = pin;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
