package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewPastPrescriptionsActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    PrescriptionsAdapter adapter;
    ArrayList<Prescriptions> prescriptions;
    String patientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_past_prescriptions);

        try{
            this.getSupportActionBar().hide();
        }catch (NullPointerException e){}

        Intent intent = getIntent();
        patientID = intent.getStringExtra("PATIENTID");

        recyclerView = findViewById(R.id.past_prescription_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        prescriptions = new ArrayList<Prescriptions>();

        firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("users").document(patientID).collection("medicine");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Prescriptions pre = document.toObject(Prescriptions.class);
                        prescriptions.add(pre);
                    }
                    adapter = new PrescriptionsAdapter(prescriptions,ViewPastPrescriptionsActivity.this);
                    recyclerView.setAdapter(adapter);
                }else{
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}