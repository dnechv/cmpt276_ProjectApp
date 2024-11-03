package com.example.memoryconnect;

//imports
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memoryconnect.ViewModel.PatientViewModel;


//fragment for displaying patient info for the caregiver
//info: name, age, date of birth, gender, relatives, condition, notes

//TODO: displaying interface for patient info -> Done
//TODO: database fetching for patient info -> Done
//TODO: Organize views
//TODO - delete functionality
//TODO - Edit info functionality


//patient info class extends fragment

public class patient_info_fragment extends Fragment {
    private static final String ARG_PATIENT_ID = "patient_id";
    private String patientId;

    private TextView nameTextView, nicknameTextView, ageTextView, commentTextView;
    private ImageView patientProfilePicture;

    public static patient_info_fragment newInstance(String patientId) {
        patient_info_fragment fragment = new patient_info_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_info_fragment, container, false);

        // Initialize UI elements
        nameTextView = view.findViewById(R.id.patientName);
        nicknameTextView = view.findViewById(R.id.patientNickname);
        ageTextView = view.findViewById(R.id.patientAge);
        commentTextView = view.findViewById(R.id.patientComment);
        patientProfilePicture = view.findViewById(R.id.patientProfilePicture);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        //delete button - TODO:delete functionality from the database

        //finding the delete button from xml
        Button deleteButton = view.findViewById(R.id.deleteButtonPatientInfoScreen);

        //delete button -> adding delete icon

        // getting the icon from xml
        Drawable deleteIcon = getResources().getDrawable(R.drawable.delete, null);
        //adjusting size
        deleteIcon.setBounds(0, 0, 48, 48);
        //setting icon on the button
        deleteButton.setCompoundDrawables(deleteIcon, null, null, null);
        //adding padding to the icon
        deleteButton.setCompoundDrawablePadding(5);


        //edit info button - TODO: edit functionality for the button

        //finding the button from xml
        Button editButton = view.findViewById(R.id.editInfoButtonPatientInfoScreen);

        //getting the icon from xml
        Drawable editIcon = getResources().getDrawable(R.drawable.edit, null);

        //adjusting the size
        editIcon.setBounds(0,0,48,48);
        //setting the icon on the button
        editButton.setCompoundDrawables(editIcon, null, null, null);
        //adding padding to the icon
        editButton.setCompoundDrawablePadding(5);



        // Retrieve the patient ID passed to this fragment
        if (getArguments() != null) {
            patientId = getArguments().getString(ARG_PATIENT_ID);
        }

        // Initialize PatientViewModel
        PatientViewModel patientViewModel = new ViewModelProvider(requireActivity()).get(PatientViewModel.class);

        // Observe patient data using patientId
        patientViewModel.getPatientById(patientId).observe(getViewLifecycleOwner(), patient -> {
            if (patient != null) {
                // Update UI elements with patient details
                nameTextView.setText(patient.getName());
                nicknameTextView.setText(patient.getNickname());
                ageTextView.setText(String.valueOf(patient.getAge()));
                commentTextView.setText(patient.getComment());

                // Load image using Glide
                Glide.with(this)
                        .load(patient.getPhotoUrl())
                        .placeholder(R.drawable.ic_default_photo) // Fallback image
                        .into(patientProfilePicture);
            }
        });
    }
}