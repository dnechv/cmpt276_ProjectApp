package com.example.memoryconnect;


//view model - > manages the UI and data


//TODO - Delete Patients by the caregiver



//Release #1

//TODO - Database - Firebase - Done
//TODO - Local - Database -> sync with Firebase -> Done
//TODO - populate list as entries in data bases added -> Done
//TODO - caregiver clicks on name -> it takes him to that patient screen -> Done
//TODO - Database items showing and adding in recycler view -> Done


//Release #2
//TODO - permissions check
//TODO - Showing databse entries from local databse when the device is online
//TODO - remove / keep 3 dots on top
//TODO - PIN Logic for entry


//permissions go here
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.example.memoryconnect.ViewModel.PatientViewModel;

//caregiver_main_screen - start of the app
//displays the patients list -> pulls data from the database



//imports will go here
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoryconnect.adaptor.PatientAdapter;
import com.example.memoryconnect.controllers.CreatePatientActivity;
import com.example.memoryconnect.controllers.patient_screen_that_displays_tab_layout;
import com.example.memoryconnect.controllers.AddPinActivity;


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
       patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);



        //////////////////////////DB
        // Find the Add New Patient button
        Button addNewPatientButton = findViewById(R.id.add_new_patient);

        //adding plus sign to the button
        addNewPatientButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plus, 0, 0, 0);

        // Set an OnClickListener on the button
        addNewPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start CreatePatientActivity
                Intent intent = new Intent(caregiver_main_screen.this, CreatePatientActivity.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView and Adapter -displaying the patients

        // Find the RecyclerView from the xml -> assign to recyclerView variable
        RecyclerView recyclerView = findViewById(R.id.patientRecyclerView);

        //setting layout view for the recycler manager -> position linearly
         recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //setting up the adapter
        PatientAdapter adapter = new PatientAdapter(new ArrayList<>(), patient -> {
            ////////////////////////////////////////////////
            // Handle click event - navigate to tab layout Activity and then to patient info fragment
            Intent intent = new Intent(caregiver_main_screen.this, patient_screen_that_displays_tab_layout.class);

            intent.putExtra("PATIENT_ID", patient.getId()); // passing the patient id to the next activity -> displays patients

            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // observes data and updates the UI
        patientViewModel.getAllPatients().observe(this, patients -> {
            if (patients != null) {
                adapter.setPatients(patients);
            } else {
                Log.d("MainActivity", "No patients found.");
            }
        });


    }

    //oncreate options menu -> action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        Log.d("menu action bar", "menu action bar");
        return true;
    }

    // method to handle action bar clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Debug logging
        Log.d("MenuClick", "Clicked item ID: " + id);

            Log.d("MenuClick", "Navigating to AddPinActivity");
            Intent intent = new Intent(this, AddPinActivity.class);
            startActivity(intent);
            return true;



    }




///////////////////////////////////////DATABASE////////////


}

