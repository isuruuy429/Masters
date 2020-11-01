package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewAppointments extends AppCompatActivity {

    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    AppointmentsAdapter adapter;
    ArrayList<Appointments> appointments;
    FirebaseAuth auth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments);

        try{
            this.getSupportActionBar().hide();
        }catch (NullPointerException e){}

        recyclerView = findViewById(R.id.view_booked_appointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appointments = new ArrayList<Appointments>();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();

        Query query = firestore.collection("users").document(userId).collection("bookedSlots");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Appointments doc = document.toObject(Appointments.class);
                        appointments.add(doc);
                    }
                    adapter = new AppointmentsAdapter(appointments, ViewAppointments.this);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

    }
}