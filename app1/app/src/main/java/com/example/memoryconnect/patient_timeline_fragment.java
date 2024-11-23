package com.example.memoryconnect;

//view model -> manages the UI and data


//imports
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



//photo entry view model
import com.example.memoryconnect.ViewModel.PhotoEntryViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class patient_timeline_fragment extends Fragment {

    private static final String ARG_PATIENT_ID = "patient_id";
    private String patientId;
    private RecyclerView recyclerView; // recyclerView for timeline
    private TimelineAdapter adapter; // adapter for the timeline events
    private PhotoEntryViewModel photoEntryViewModel; // view model for photo entries




    //creates a new instance of the fragment with the patient id as an argument
    public static patient_timeline_fragment newInstance(String patientId) {

        patient_timeline_fragment fragment = new patient_timeline_fragment();

        Bundle args = new Bundle();

        args.putString(ARG_PATIENT_ID, patientId);

        fragment.setArguments(args);

        return fragment;
    }


    //creates view
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientId = getArguments().getString(ARG_PATIENT_ID);
        }
    }


    //creates view
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_timeline_fragment, container, false);

        //initialize the layout

        //find recycler view from the xml
        recyclerView = view.findViewById(R.id.timelineRecyclerViewPatientTimeLineFragment);



        //set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); //layout manager for the recycler view

        //initialize adapter with an empty list
        adapter = new TimelineAdapter(new ArrayList<>(), event -> {


            // TODO: Navigate to event details if we decide to work on it



        }); recyclerView.setAdapter(adapter);

        //initialize the view model
        photoEntryViewModel = new ViewModelProvider(this).get(PhotoEntryViewModel.class);

        //fetch the data for the specific patient
        photoEntryViewModel.initializePhotos(patientId);

        //observe data + update recycler view when it changes
        photoEntryViewModel.getAllPhotos().observe(getViewLifecycleOwner(), photoEntries -> {
            List<PhotoEntry> events = new ArrayList<>();
            for (com.example.memoryconnect.model.PhotoEntry photoEntry : photoEntries) {
                events.add(new PhotoEntry(
                        photoEntry.getId(),
                        photoEntry.getTitle(),
                        "https://youtu.be/dQw4w9WgXcQ", "patientId3", System.currentTimeMillis()));
            }

            //update the adapter with the new data
            adapter.setEvents(events);
        });

        return view;

    }

    //upload photo to database - testing
    private void uploadPhotoToDatabase() {


        //get firebase reference
        DatabaseReference photosRef = FirebaseDatabase.getInstance().getReference("photos");

        //template date
        String photoId = "photoId1";
        String patientId = "4c308aa2-c767-4181-af92-a0d5a3e6dd17";
        String photoUrl = "https://firebasestorage.googleapis.com/v0/b/memoryconnect-fa6e5.appspot.com/o/patientTimelinePhotos%2Ftest.jpg?alt=media";  // Public URL of the image
        long timeWhenPhotoAdded = System.currentTimeMillis();


        //photo entry - firebase
        com.example.memoryconnect.model.PhotoEntry photoEntry = new com.example.memoryconnect.model.PhotoEntry(photoId, patientId, photoUrl, timeWhenPhotoAdded);

        //adding to database
        photosRef.child(photoId).setValue(photoEntry)


                .addOnSuccessListener(aVoid -> {

                    Log.d("Firebase", "manual phone was added");
                })
                .addOnFailureListener(e -> {

                    Log.e("Firebase", "manual error adding photo", e);

                });
    }

}