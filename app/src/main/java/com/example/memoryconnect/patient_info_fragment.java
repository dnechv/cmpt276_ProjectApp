package com.example.memoryconnect;
import com.example.memoryconnect.controllers.ClientLogin;
import com.example.memoryconnect.ViewModel.EditInfo;

//imports
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.memoryconnect.ViewModel.PatientViewModel;


//fragment for displaying patient info for the caregiver
//info: name, age, date of birth, gender, relatives, condition, notes

//TODO: displaying interface for patient info
//TODO: database fetching for patient info


//patient info class extends fragment

public class patient_info_fragment extends Fragment {

    private Button editInfoButton;
    private Button logoutButton;
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
        logoutButton = view.findViewById(R.id.logoutButton);
        editInfoButton = view.findViewById(R.id.editInfoButton);

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

                // Load image using Glide
                Glide.with(this)
                        .load(patient.getPhotoUrl())
                        .placeholder(R.drawable.ic_default_photo) // Fallback image
                        .into(patientProfilePicture);
            }
        });
        logoutButton.setOnClickListener(v -> logoutStep());
        editInfoButton.setOnClickListener(v -> editInfoStep());
    }
// Steps logout and edit
private void logoutStep() {
    Toast.makeText(requireContext(), "Logging out...", Toast.LENGTH_SHORT).show();
    Intent intent = new Intent(requireContext(), ClientLogin.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    requireActivity().finish();
}
    private void editInfoStep() {
        Intent intent = new Intent(requireContext(), EditInfo.class);
        startActivity(intent);
    }

}