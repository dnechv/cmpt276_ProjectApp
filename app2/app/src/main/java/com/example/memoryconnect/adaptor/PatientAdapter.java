package com.example.memoryconnect.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoryconnect.R;

import java.util.List;
import java.util.Map;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private List<String> patientList; // List of patient IDs
    private Map<String, String> patientIdToNameMap; // Map for patient names
    private OnPatientClickListener onPatientClickListener;

    public interface OnPatientClickListener {
        void onPatientClick(String patientId);
    }

    public PatientAdapter(List<String> patientList, Map<String, String> patientIdToNameMap, OnPatientClickListener onPatientClickListener) {
        this.patientList = patientList;
        this.patientIdToNameMap = patientIdToNameMap;
        this.onPatientClickListener = onPatientClickListener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        String patientId = patientList.get(position);
        String patientName = patientIdToNameMap.getOrDefault(patientId, patientId);
        holder.bind(patientId, patientName, onPatientClickListener);
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {

        private TextView patientIdText;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);

            //display name
            patientIdText = itemView.findViewById(R.id.patientIdTextView);
        }

        public void bind(String patientId, String patientName, OnPatientClickListener onPatientClickListener) {
            patientIdText.setText(patientName);
            itemView.setOnClickListener(v -> onPatientClickListener.onPatientClick(patientId));
        }
    }
}