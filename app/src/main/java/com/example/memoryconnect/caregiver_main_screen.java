package com.example.memoryconnect;


//permissions go here
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.example.memoryconnect.ViewModel.PatientViewModel;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoryconnect.adaptor.PatientAdapter;
import com.example.memoryconnect.controllers.patient_screen_that_displays_tab_layout;


import java.util.ArrayList;


public class caregiver_main_screen extends AppCompatActivity {

    private PatientViewModel patientViewModel;
//overriding methods


    @Override
    //onCreate method -> creates the activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting the content view from caregiver_main_screen.xml
        setContentView(R.layout.caregiver_main_screen);

        // Initialize ViewModel
        PatientViewModel patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

//        //placeholder button logic
//
//        //finding the textview by id
//        TextView placeHolderButtonToGoToPatientScreen = findViewById(R.id.placeholderButton);
//
//        //setting on click listener for the placeholder button
//        placeHolderButtonToGoToPatientScreen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //creating the intent to start the new activity-> patient_screen_that_displays_tab_layout.class
//                Intent intent = new Intent(caregiver_main_screen.this, patient_screen_that_displays_tab_layout.class);
//               //starting the activity
//                startActivity(intent);
//            }
//        });

        //////////////////////////DB
        // Find the Add New Patient button
        Button addNewPatientButton = findViewById(R.id.add_new_patient);

        // Set an OnClickListener on the button
        addNewPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start CreatePatientActivity
                Intent intent = new Intent(caregiver_main_screen.this, patient_screen_that_displays_tab_layout.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.patientRecyclerView);
        PatientAdapter adapter = new PatientAdapter(new ArrayList<>(), patient -> {
            ////////////////////////////////////////////////
            // Handle click event - navigate to tab layout Activity and then to patient info fragment
            Intent intent = new Intent(caregiver_main_screen.this, patient_screen_that_displays_tab_layout.class);
            intent.putExtra("PATIENT_ID", patient.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe patient data from ViewModel and update RecyclerView
        patientViewModel.getAllPatients().observe(this, patients -> {
            if (patients != null) {
                adapter.setPatients(patients); // This method should call notifyDataSetChanged()
            } else {
                Log.d("MainActivity", "No patients found.");
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


///////////////////////////////////////DATABASE////////////


}

