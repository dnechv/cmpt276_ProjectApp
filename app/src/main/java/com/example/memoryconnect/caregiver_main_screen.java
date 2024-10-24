package com.example.memoryconnect;


//permissions go here
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//main screen - start of the app

//the screen shows patients list to caregiver
//TODO - populate list as entries in data bases added
//TODO - PIN Logic remote
//TODO - permissions check
//TODO - Database items showing and adding in recycler view


//imports will go here
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class caregiver_main_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.caregiver_main_screen);

        // Set up the toolbar

    }

    // Inflate the menu (the one you defined in XML)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Handle action bar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle the "settings" action
        if (id == R.id.action_settings) {
            // Perform your settings action here
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}