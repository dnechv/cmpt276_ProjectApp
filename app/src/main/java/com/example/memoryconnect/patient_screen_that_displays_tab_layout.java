package com.example.memoryconnect;

// Necessary imports
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

// Activity that displays a TabLayout for the caregiver
// Two tabs: Patient Info and Timeline


public class patient_screen_that_displays_tab_layout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_screen_that_displays_tab_layout);

        // Initialize TabLayout and ViewPager2
        TabLayout tabLayout = findViewById(R.id.menu_top_tab);
        ViewPager2 viewPager2 = findViewById(R.id.viewpager_tab);

        //create list to display fragments -> contained within arraylist
        ArrayList<Fragment> fragments = new ArrayList<>();

        //adding fragments to the list
        fragments.add(new patient_info_fragment());   // Fragment for Patient Info
        fragments.add(new patient_timeline_fragment());      // Fragment for Timeline


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