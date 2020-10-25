package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewAvailableSlots extends AppCompatActivity {

    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    AvailableSlotsAdapter adapter;
    ArrayList<AvailableSlots> slots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String userid = i.getStringExtra("USERID");
        System.out.println("From past act:" +userid);
        setContentView(R.layout.activity_view_available_slots);

        recyclerView = findViewById(R.id.recycleview_slots);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        slots = new ArrayList<AvailableSlots>();

        firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("users").document(userid).collection("availableSlots");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        AvailableSlots doc = document.toObject(AvailableSlots.class);
                        slots.add(doc);
                    }
                    adapter = new AvailableSlotsAdapter(slots,ViewAvailableSlots.this);
                    recyclerView.setAdapter(adapter);
                }else{
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}