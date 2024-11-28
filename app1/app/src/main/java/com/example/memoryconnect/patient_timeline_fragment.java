package com.example.memoryconnect;

// View model -> manages the UI and data

// Imports
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.memoryconnect.adaptor.TimelineAdapter;
import com.example.memoryconnect.local_database.PhotoEntry;

// Photo entry view model
import com.example.memoryconnect.ViewModel.PhotoEntryViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class patient_timeline_fragment extends Fragment {

    private static final String ARG_PATIENT_ID = "patient_id";
    private String patientId;
    private RecyclerView recyclerView; // RecyclerView for timeline
    private TimelineAdapter adapter; // Adapter for the timeline events
    private PhotoEntryViewModel photoEntryViewModel; // ViewModel for photo entries
    private DatabaseReference databaseReference; // Firebase reference

    // Creates a new instance of the fragment with the patient id as an argument
    public static patient_timeline_fragment newInstance(String patientId) {
        patient_timeline_fragment fragment = new patient_timeline_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    // Initializes the fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientId = getArguments().getString(ARG_PATIENT_ID);
        }

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // Creates and returns the fragment's view
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_timeline_fragment, container, false);

        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.timelineRecyclerViewPatientTimeLineFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Layout manager for the RecyclerView

        // Initialize adapter with an empty list
        adapter = new TimelineAdapter(new ArrayList<>(), event -> {
            // TODO: Navigate to event details if we decide to work on it
        });
        recyclerView.setAdapter(adapter);

        // Initialize the ViewModel
        photoEntryViewModel = new ViewModelProvider(this).get(PhotoEntryViewModel.class);

        // Fetch the data for the specific patient
        fetchTimelineEntriesFromFirebase(patientId);

        return view;
    }

    //fet data for timeline from fireabse and update the adapter
    private void fetchTimelineEntriesFromFirebase(String patientId) {



        //create a reference to the timeline entries
        DatabaseReference timelineRef = databaseReference

                //organize structure of the database for fetching
                //patients have timeline entries node with data
                .child("patients")
                .child(patientId)
                .child("timelineEntries");


        //fetch data from firebase
        timelineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                //create a list of photo entries
                List<PhotoEntry> events = new ArrayList<>();


                //loop through the data
                for (DataSnapshot entrySnapshot : snapshot.getChildren()) {



                    //get firebase data
                    String id = entrySnapshot.child("id").getValue(String.class);
                    String title = entrySnapshot.child("title").getValue(String.class);
                    String photoUrl = entrySnapshot.child("photoUrl").getValue(String.class);
                    String youtubeLink = entrySnapshot.child("youtubeLink").getValue(String.class);
                    Long timestamp = entrySnapshot.child("timestamp").getValue(Long.class);
                    String songName = entrySnapshot.child("songName").getValue(String.class);
                    String photoDescription = entrySnapshot.child("photoDescription").getValue(String.class);

                    //check for either link to photo or youtube
                    if ((photoUrl != null && !photoUrl.isEmpty()) || (youtubeLink != null && !youtubeLink.isEmpty())) {

                        //new photo entry object
                        events.add(new PhotoEntry(
                                id != null ? id : entrySnapshot.getKey(),
                                title != null ? title : "Timeline Event",
                                patientId,
                                null,
                                photoUrl,
                                youtubeLink,
                                timestamp != null ? timestamp : System.currentTimeMillis(),  // <-- add the comma here
                                songName != null ? songName : "No song name",  // <-- Now it's correct
                                photoDescription != null ? photoDescription : "No description available"
                        ));


                    } else {




                        Log.e("firebase", "error here: " + entrySnapshot.getKey());
                    }
                }

                //update adpater
                adapter.setEvents(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("firebase", " error: " + error.getMessage());
            }
        });
    }
}