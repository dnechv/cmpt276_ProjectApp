package com.example.memoryconnect.local_database;

//defines database migration
//ie adding new nodes and etc


//imports

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigration {

/*
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //create new table
            database.execSQL("CREATE TABLE 'PhotoEntry' ('id' TEXT NOT NULL, 'patientId' TEXT NOT NULL, 'photoUrl' TEXT, 'timeWhenPhotoAdded' INTEGER NOT NULL, PRIMARY KEY('id'))");
        }
    };
*/
    public static final Migration MIGRATION_2_4 = new Migration(2, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create the PhotoEntry table
            database.execSQL("CREATE TABLE 'PhotoEntry' ('id' TEXT NOT NULL, 'patientId' TEXT NOT NULL, 'photoUrl' TEXT, 'timeWhenPhotoAdded' INTEGER NOT NULL, PRIMARY KEY('id'))");

            // Create the pin_table
            database.execSQL("CREATE TABLE 'pin_table' (" +
                    "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'pin' TEXT NOT NULL)");
        }
    };
}
    /*
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //create new table
            database.execSQL("CREATE TABLE 'pin_table' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'pin' TEXT NOT NULL)");
        }
    };
}
*/