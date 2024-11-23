package com.example.memoryconnect.local_database;


//patient class


//imports
import static com.example.memoryconnect.local_database.DatabaseMigration.MIGRATION_2_3;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.RoomDatabase;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.memoryconnect.model.Patient;
import com.example.memoryconnect.model.PhotoEntry;

//contains code for the local database - manages the local database through dao
//gets dao for patients and photos
@Database(entities = {Patient.class, PhotoEntry.class}, version = 3, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {


    // migration class
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //create new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `PhotoEntry` (" +
                    "`id` TEXT NOT NULL, " +
                    "`photoUrl` TEXT, " +
                    "`patientId` TEXT, " +
                    "`timeWhenPhotoAdded` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`id`))");
        }
    };

    //ensures only single instance of database is created
    private static volatile LocalDatabase INSTANCE;

    //get dao for patients
    public abstract LocaldatabaseDao localdatabaseDao();


    //get dao for photos
    public abstract PhotoEntryDatabaseDAO photoEntryDatabaseDAO();


    //  getter -> only once instance of databse is created


    public static LocalDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {


            synchronized (LocalDatabase.class) {



                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),



                                    LocalDatabase.class, "memoryconnect_database")

                            .addMigrations(MIGRATION_2_3) //migration database_2) //migration database
                            .fallbackToDestructiveMigration() //if no migration, recreate database
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}
