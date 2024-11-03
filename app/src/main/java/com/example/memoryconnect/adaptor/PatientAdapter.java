package com.example.memoryconnect.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoryconnect.R;
import com.example.memoryconnect.model.Patient;

import java.util.List;
import com.bumptech.glide.Glide;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private List<Patient> patients;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Patient patient);
    }

    public PatientAdapter(List<Patient> patients, OnItemClickListener listener) {
        this.patients = patients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patients.get(position);
        holder.bind(patient, listener);
    }

    @Override
    public int getItemCount() {
        return patients != null ? patients.size() : 0;
    }

    // ViewHolder class for individual patient items
    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        private ImageView patientPhoto;
        private TextView patientName;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            patientPhoto = itemView.findViewById(R.id.patientPhoto);
            patientName = itemView.findViewById(R.id.patientName);
        }

        public void bind(Patient patient, OnItemClickListener listener) {
            patientName.setText(patient.getName());
            Glide.with(patientPhoto.getContext())
                    .load(patient.getPhotoUrl())
                    .circleCrop()
                    .into(patientPhoto);
            itemView.setOnClickListener(v -> listener.onItemClick(patient));
        }
    }

    // Method to update the list of patients and notify the adapter
    public void setPatients(List<Patient> newPatients) {
        this.patients = newPatients;       // Update the adapter's patient list
        notifyDataSetChanged();            // Notify RecyclerView that data has changed
    }
}
