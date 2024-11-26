package com.example.memoryconnect.controllers;

// Necessary imports
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.memoryconnect.R;
import com.example.memoryconnect.ViewModel.PatientViewModel;
import com.example.memoryconnect.adaptor.fragment_adapter;
import com.example.memoryconnect.patient_info_fragment;
import com.example.memoryconnect.patient_timeline_fragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;

// Activity that displays a TabLayout for the caregiver
// Two tabs: Patient Info and Timeline


public class patient_screen_that_displays_tab_layout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_screen_that_displays_tab_layout);
        // Retrieve the patient ID from the intent

        String patientId = getIntent().getStringExtra("PATIENT_ID");

        // Initialize TabLayout and ViewPager2
        TabLayout tabLayout = findViewById(R.id.menu_top_tab);
        ViewPager2 viewPager2 = findViewById(R.id.viewpager_tab);

        //create list to display fragments -> contained within arraylist
        ArrayList<Fragment> fragments = new ArrayList<>();

        // Add fragments to the list, passing the patientId
        fragments.add(patient_info_fragment.newInstance(patientId));   // Fragment for Patient Info
        fragments.add(patient_timeline_fragment.newInstance(patientId)); // Fragment for Timeline

        //creating fragment adapter -> layer between fragment and activity
        fragment_adapter adapter = new fragment_adapter(this, fragments);

        // Set adapter for ViewPager2
        viewPager2.setAdapter(adapter);

        // Connect TabLayout and ViewPager2 using TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            // Set the title for each tab based on its position
            switch (position) {
                case 0:
                    tab.setText("Patient Info");
                    break;
                case 1:
                    tab.setText("Timeline");
                    break;
            }
        }).attach();  // Attach the TabLayoutMediator

    }
}