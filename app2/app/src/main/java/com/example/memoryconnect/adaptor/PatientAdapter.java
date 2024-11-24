package com.example.memoryconnect.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memoryconnect.R;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private List<String> patientNames;
    private List<String> patientIds;
    private OnPatientClickListener onPatientClickListener;

    public interface OnPatientClickListener {
        void onPatientClick(String patientName, String patientId);
    }

    public PatientAdapter(List<String> patientNames, List<String> patientIds, OnPatientClickListener onPatientClickListener) {
        this.patientNames = patientNames;
        this.patientIds = patientIds;
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
        String patientName = patientNames.get(position);
        String patientId = patientIds.get(position);
        holder.bind(patientName, patientId, onPatientClickListener);
    }

    @Override
    public int getItemCount() {
        return patientNames.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        private TextView patientNameText;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            patientNameText = itemView.findViewById(R.id.patientIdTextView);
        }

        public void bind(String patientName, String patientId, OnPatientClickListener onPatientClickListener) {
            patientNameText.setText(patientName);
            itemView.setOnClickListener(v -> onPatientClickListener.onPatientClick(patientName, patientId));
        }
    }
}
