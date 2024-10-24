package com.example.memoryconnect;


//permissions go here
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//main screen - start of the app
//displays the patients list -> pulls data from the database


//the screen shows patients list to caregiver
//TODO - populate list as entries in data bases added
//TODO - caregiver clicks on name -> it takes him to that patient screen
//TODO - PIN Logic remote
//TODO - permissions check
//TODO - Database
//TODO - Database items showing and adding in recycler view


//imports will go here
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class caregiver_main_screen extends AppCompatActivity {


//overriding methods


    @Override
    //onCreate method -> creates the activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting the content view from caregiver_main_screen.xml
        setContentView(R.layout.caregiver_main_screen);

        //placeholder button logic

        //finding the textview by id
        TextView placeHolderButtonToGoToPatientScreen = findViewById(R.id.placeholderButton);

        //setting on click listener for the placeholder button
        placeHolderButtonToGoToPatientScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating the intent to start the new activity-> patient_screen_that_displays_tab_layout.class
                Intent intent = new Intent(caregiver_main_screen.this, patient_screen_that_displays_tab_layout.class);
               //starting the activity
                startActivity(intent);
            }
        });

    }

    //oncreate options menu -> action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // method to handle action bar clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //click on settings
        if (id == R.id.action_settings) {
            //TODO: if needed - possibly patient managment for the caregiver
            return true;
        }


        return super.onOptionsItemSelected(item);
    }




}

