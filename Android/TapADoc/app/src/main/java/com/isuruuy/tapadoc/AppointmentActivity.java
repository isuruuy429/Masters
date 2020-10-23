package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class AppointmentActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<String> items;
    ProgressDialog mProgressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        items = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("users").whereEqualTo("isDoctor", true);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d("TAG", document.getId() + " => " + document.getData().get("name"));
                        System.out.println("Class type:"+task.getResult().getClass());
                        items.add(document.getData().get("name").toString());
                    }
                    System.out.println("printing array list");
                    for(int i=0; i < items.size(); i++){
                        System.out.println( items.get(i) );
                    }
                }else{
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(getApplicationContext());
//            mProgressDialog.setMessage("Loading");
//            mProgressDialog.setIndeterminate(true);
//        }
//
//        mProgressDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        //items = new ArrayList<>();
//        items.add("First");
//        items.add("Second");
//        items.add("Third");
//        items.add("fourth");

//        recyclerView = findViewById(R.id.recyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new Adapter(this, items);
//        recyclerView.setAdapter(adapter);

//        firestore = FirebaseFirestore.getInstance();
//        Query query = firestore.collection("users").whereEqualTo("isDoctor", true);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for(QueryDocumentSnapshot document : task.getResult()){
//                        Log.d("TAG", document.getId() + " => " + document.getData().get("name"));
//                        System.out.println("Class type:"+task.getResult().getClass());
//                        items.add(document.getData().get("name").toString());
//                    }
//                    System.out.println("printing array list");
//                    for(int i=0; i < items.size(); i++){
//                        System.out.println( items.get(i) );
//                    }
//                }else{
//                    Log.d("TAG", "Error getting documents: ", task.getException());
//                }
//            }
//        });
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, items);
        recyclerView.setAdapter(adapter);

//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
    }
}