package com.example.memoryconnect.controllers;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoryconnect.R;
import com.example.memoryconnect.adaptor.PatientTimelineAdapter;
import com.example.memoryconnect.data_model.TimelineEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


//this defines the patient timeline so family member can view it

public class PatientTimeLine extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PatientTimelineAdapter timelineAdapter;
    private List<TimelineEntry> timelineEntries;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_timeline);


        //initialize recycler view
        recyclerView = findViewById(R.id.timelineRecyclerViewPatientTimeLineFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backButton = findViewById(R.id.backButton); // Reference to the ImageView
        backButton.setOnClickListener(v -> finish()); // Close the activity and go back



        //initialize timeline entries
        timelineEntries = new ArrayList<>();
        timelineAdapter = new PatientTimelineAdapter(timelineEntries);


        //set adapter
        recyclerView.setAdapter(timelineAdapter);


        //get patient id via intent
        String patientId = getIntent().getStringExtra("PATIENT_ID");


        //fetch patient timeline if patient id exists
        if (patientId != null) {

            //fetch patient timeline
            fetchPatientTimeline(patientId);
        } else {


            //show error message
            Toast.makeText(this, "no patient id", Toast.LENGTH_SHORT).show();
        }
    }


    //fetch patient timeline function
    private void fetchPatientTimeline(String patientId) {

        //get database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("patients")

                //get patient id
                .child(patientId)

                //get timeline entries which is a child node
                .child("timelineEntries");


        //add value event listener to database reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timelineEntries.clear();



                //iterate through timeline entries
                for (DataSnapshot timelineSnapshot : snapshot.getChildren()) {

                    //get firebase data


                    //get photo from firebase
                    String photoUrl = timelineSnapshot.child("photoUrl").getValue(String.class);

                    //get title
                    String title = timelineSnapshot.child("title").getValue(String.class);

                    //get youtube link
                    String youtubeLink = timelineSnapshot.child("youtubeLink").getValue(String.class);

                    //get event name
                    String photoDescription = timelineSnapshot.child("photoDescription").getValue(String.class);


                    //get song name
                    String songName = timelineSnapshot.child("songName").getValue(String.class);




                    //add timeline entry if they exists
                    if (photoUrl != null || youtubeLink != null) {



                        TimelineEntry entry = new TimelineEntry(title, photoUrl, youtubeLink, songName, photoDescription);
                        timelineEntries.add(entry);
                    }
                }

                timelineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PatientTimeLine.this, "Failed to load timeline.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}