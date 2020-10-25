package com.isuruuy.tapadoc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AvailableSlotsAdapter extends RecyclerView.Adapter<AvailableSlotsAdapter.ViewHolderSlots>{

    ArrayList<AvailableSlots> slots;
    Context context;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    public AvailableSlotsAdapter(ArrayList<AvailableSlots> slots, Context context) {
        this.slots = slots;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderSlots onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AvailableSlotsAdapter.ViewHolderSlots(LayoutInflater.from(context).inflate(R.layout.custom_view2, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSlots holder, int position) {
        holder.date.setText(slots.get(position).getDate());
        holder.time.setText(slots.get(position).getTime());
        holder.id = slots.get(position).getId();
        holder.docid = slots.get(position).getDocid();
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }


    public class ViewHolderSlots extends RecyclerView.ViewHolder{

        TextView date,time;
        String id;
        String docid;

        public ViewHolderSlots(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_carview);
            time = itemView.findViewById(R.id.time_cardview);

            firestore = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(itemView.getContext());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Do you want to book this slot?");
                    builder.setIcon(R.drawable.ic_baseline_book_online_24);
                    builder.setPositiveButton("OK", (dialogInterface, i) -> {
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        String timeStamp_str= String.valueOf(timestamp.getTime());

                        String date_str = date.getText().toString().trim();
                        String time_str = time.getText().toString();
                        String patientId = firebaseAuth.getCurrentUser().getUid();

                        System.out.println("SLOT ID is: " + id);
                        System.out.println("DOCID is : "+ docid);

                        DocumentReference documentReference = firestore.collection("users").document(docid).collection("bookedSlots").document(timeStamp_str);
                        Map<String,Object> new_book_slot = new HashMap<>();
                        new_book_slot.put("date", date_str);
                        new_book_slot.put("time", time_str);
                        new_book_slot.put("slotid", timeStamp_str);
                        new_book_slot.put("docid", docid);
                        new_book_slot.put("patientid", patientId);

                        documentReference.set(new_book_slot).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(view.getContext(), "Appointment booking is completed.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(), "Error occurred in Appointment booking.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        firestore.collection("users").document(docid).collection("availableSlots").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAGDELETE", "DocumentSnapshot successfully deleted!");
                            }
                        });
                    });
                    builder.setNegativeButton("NO", (dialogInterface, i) -> System.out.println("Clicked NO"));
                    builder.show();
                }
            });
        }
    }
}
