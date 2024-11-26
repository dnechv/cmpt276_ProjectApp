package com.example.memoryconnect;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoryconnect.adaptor.TimelineAdapter;
import com.example.memoryconnect.model.PhotoEntry;
import com.example.memoryconnect.ViewModel.PhotoEntryViewModel;

import java.util.ArrayList;

public class patient_timeline_fragment extends Fragment {

    private static final String ARG_PATIENT_ID = "PATIENT_ID";
    private RecyclerView recyclerView;
    private TimelineAdapter adapter;
    private PhotoEntryViewModel photoEntryViewModel;
    private String patientId;

    // Static method to create a new instance of the fragment and pass the patientId
    public static patient_timeline_fragment newInstance(String patientId) {
        patient_timeline_fragment fragment = new patient_timeline_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientId = getArguments().getString(ARG_PATIENT_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_timeline_fragment, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.timelineRecyclerViewPatientTimeLineFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter with an empty list
        adapter = new TimelineAdapter(new ArrayList<>(), event -> {
            // Handle event click here, e.g., opening YouTube or showing details
            Log.d("TimelineAdapter", "Clicked event: " + event.getTitle());
        });
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel and sync photos
        photoEntryViewModel = new ViewModelProvider(this).get(PhotoEntryViewModel.class);
        photoEntryViewModel.initializePhotos(patientId);

        // Observe data from ViewModel and update RecyclerView
        photoEntryViewModel.getAllPhotos().observe(getViewLifecycleOwner(), photoEntries -> {
            if (photoEntries != null && !photoEntries.isEmpty()) {
                Log.d("patient_timeline_fragment", "Photos fetched: " + photoEntries.size());
                adapter.setEvents(photoEntries);
            } else {
                Log.d("patient_timeline_fragment", "No photos found for patient.");
            }
        });

        return view;
    }
}
