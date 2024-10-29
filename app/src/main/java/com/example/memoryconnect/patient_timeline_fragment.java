package com.example.memoryconnect;


//imports
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


//this screen will be a patient timeline
//data will be populated based on each patient chosen by the caregiver

//TODO: Populate the timeline data for each patient
//TODO: Scrollable list of events for each patient


//patient timeline fragment -> extends fragment class
public class patient_timeline_fragment  extends Fragment{
    private static final String ARG_PATIENT_ID = "patient_id"; // Define ARG_PATIENT_ID here
    private String patientId;

    public static patient_timeline_fragment newInstance(String patientId) {
        patient_timeline_fragment fragment = new patient_timeline_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflates the fragment layout from the patient_timeline_fragment.xml
        return inflater.inflate(R.layout.patient_timeline_fragment, container, false);  // Ensure this matches your layout file name
    }

}
