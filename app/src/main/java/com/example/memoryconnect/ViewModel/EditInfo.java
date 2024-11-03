package com.example.memoryconnect.ViewModel;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.memoryconnect.R;

public class EditInfo extends AppCompatActivity {

    private EditText nameEditText;
    private EditText nicknameEditText;
    private EditText ageEditText;
    private EditText commentEditText;
    private Button cancel;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_patient_info);

        nameEditText = findViewById(R.id.nameEditText);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        commentEditText = findViewById(R.id.commentEditText);
        cancel = findViewById(R.id.cancelButton);
        save= findViewById(R.id.saveButton);

        // Set up button listeners
        save.setOnClickListener(v -> saveStep());
        cancel.setOnClickListener(v -> cancelStep());
    }

    private void saveStep() {
        // Placeholder as haven't finish it TODO: make save works
        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();

    }
    private void cancelStep() {
        finish();
    }

}
