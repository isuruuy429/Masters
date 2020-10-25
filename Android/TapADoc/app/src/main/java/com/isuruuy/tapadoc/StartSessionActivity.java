package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import static android.Manifest.permission.CALL_PHONE;

public class StartSessionActivity extends AppCompatActivity {

    String patientID, patientMobile, doctorID, slotID;
    TextView patientName, bmi, weight;
    EditText prescription;
    Button completeSession, callUser, old_prescriptions, submitPrescription;
    DocumentReference documentReference;
    DocumentReference addMedicine, removeSession;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_session);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        doctorID = firebaseAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        patientID = intent.getStringExtra("PATIENTID");
        slotID = intent.getStringExtra("SLOTID");

        patientName = findViewById(R.id.textView_session_patient_name);
        bmi = findViewById(R.id.textView_session_bmi);
        weight = findViewById(R.id.textView_session_weight);
        prescription = findViewById(R.id.edittxt_medicine);
        completeSession = findViewById(R.id.button_complete_session);
        callUser = findViewById(R.id.button_call_patient);
        old_prescriptions = findViewById(R.id.button_see_old_prescriptions);
        submitPrescription = findViewById(R.id.button_submit_prescription);

        documentReference = firestore.collection("users").document(patientID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                patientName.setText(documentSnapshot.getString("name"));
                bmi.setText(documentSnapshot.getString("bmi"));
                weight.setText(documentSnapshot.getString("weight"));
                patientName.setText(documentSnapshot.getString("name"));
                patientMobile = documentSnapshot.getString("mobile");
            }
        });

        submitPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String timeStamp_str= String.valueOf(timestamp.getTime());
                addMedicine = firestore.collection("users").document(patientID).collection("medicine").document(timeStamp_str);

                Map<String,Object> pre_medicine = new HashMap<>();
                pre_medicine.put("medicine", prescription.getText().toString().trim());
                pre_medicine.put("patientid", patientID);
                addMedicine.set(pre_medicine).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StartSessionActivity.this, "Prescription added successfully.", Toast.LENGTH_SHORT ).show();
                        prescription.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StartSessionActivity.this, "Error occurred while adding prescription.", Toast.LENGTH_SHORT ).show();
                    }
                });
            }
        });

        old_prescriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartSessionActivity.this,ViewPastPrescriptionsActivity.class);
                intent.putExtra("PATIENTID", patientID);
                startActivity(intent);
            }
        });

        callUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", patientMobile, null));

                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{CALL_PHONE}, 1);
                }
            }
        });

        completeSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("users").document(doctorID).collection("bookedSlots").document(slotID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StartSessionActivity.this, "Session Completed! " , Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StartSessionActivity.this, "Error occurred. Session could not complete! " , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}


