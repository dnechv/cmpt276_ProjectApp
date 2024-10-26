package com.example.memoryconnect;

//imports
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memoryconnect.ViewModel.PatientViewModel;


//fragment for displaying patient info for the caregiver
//info: name, age, date of birth, gender, relatives, condition, notes

//TODO: displaying interface for patient info
//TODO: database fetching for patient info


//patient info class extends fragment
public class patient_info_fragment extends Fragment {



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflates the fragment layout from the xml
        return inflater.inflate(R.layout.patient_info_fragment, container, false);

    }
}
