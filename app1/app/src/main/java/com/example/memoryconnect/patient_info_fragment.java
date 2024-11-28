package com.example.memoryconnect;

// Imports
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.memoryconnect.ViewModel.PatientViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class patient_info_fragment extends Fragment {
    private static final String ARG_PATIENT_ID = "patient_id";
    private String patientId;


    //text views
    private TextView nameTextView, nicknameTextView, ageTextView, commentTextView, patientIDTextView;
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
        patientIDTextView = view.findViewById(R.id.patientID);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                patientIDTextView.setText(patientId);

                // Load profile picture using Glide
                Glide.with(this)
                        .load(patient.getPhotoUrl())
                        .placeholder(R.drawable.ic_default_photo)
                        .into(patientProfilePicture);
            }
        });

        // Delete button functionality
        Button deleteButton = view.findViewById(R.id.deleteButtonPatientInfoScreen);
        setupDeleteButton(deleteButton, patientViewModel);

        // Edit button functionality
        Button editButton = view.findViewById(R.id.editInfoButtonPatientInfoScreen);
        setupEditButton(editButton);
    }

    private void setupEditButton(Button editButton) {
        // Add edit icon to the button
        Drawable editIcon = getResources().getDrawable(R.drawable.edit, null);
        editIcon.setBounds(0, 0, 48, 48);
        editButton.setCompoundDrawables(editIcon, null, null, null);
        editButton.setCompoundDrawablePadding(5);

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditPatientActivity.class);
            intent.putExtra("PATIENT_ID", patientId); // Pass patient ID to edit screen
            startActivity(intent);
            requireActivity().finish(); // Close current activity and return after editing
        });
    }

    private void setupDeleteButton(Button deleteButton, PatientViewModel patientViewModel) {
        // Add delete icon to the button
        Drawable deleteIcon = getResources().getDrawable(R.drawable.delete, null);
        deleteIcon.setBounds(0, 0, 48, 48);
        deleteButton.setCompoundDrawables(deleteIcon, null, null, null);
        deleteButton.setCompoundDrawablePadding(5);

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Patient")
                    .setMessage("Are you sure you want to delete this patient?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        patientViewModel.deletePatient(patientId).observe(getViewLifecycleOwner(), success -> {
                            if (success) {
                                Toast.makeText(requireContext(), "Patient deleted successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(requireContext(), caregiver_main_screen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(requireContext(), "Failed to delete patient.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}