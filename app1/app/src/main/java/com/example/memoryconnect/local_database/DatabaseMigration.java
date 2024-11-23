package com.example.memoryconnect.local_database;

//defines database migration
//ie adding new nodes and etc


//imports

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigration {


    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //create new table
            database.execSQL("CREATE TABLE 'PhotoEntry' ('id' TEXT NOT NULL, 'patientId' TEXT NOT NULL, 'photoUrl' TEXT, 'timeWhenPhotoAdded' INTEGER NOT NULL, PRIMARY KEY('id'))");
        }
    };

}
