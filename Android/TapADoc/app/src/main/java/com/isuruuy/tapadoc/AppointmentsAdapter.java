package com.isuruuy.tapadoc;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolderSlots>{

    ArrayList<Appointments> appointments;
    Context context;

    public AppointmentsAdapter(ArrayList<Appointments> appointments, Context context) {
        this.appointments = appointments;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderSlots onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppointmentsAdapter.ViewHolderSlots(LayoutInflater.from(context).inflate(R.layout.custom_view3, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSlots holder, int position) {
        holder.date.setText(appointments.get(position).getDate());
        holder.time.setText(appointments.get(position).getTime());
        holder.patientId = appointments.get(position).getPatientid();
        holder.slotId = appointments.get(position).getSlotid();
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public class ViewHolderSlots extends RecyclerView.ViewHolder{

        TextView date,time;
        Button startSession;
        String patientId, slotId;

        public ViewHolderSlots(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.datebooked_carview);
            time = itemView.findViewById(R.id.timebooked_cardview);
            startSession = itemView.findViewById(R.id.start_session_button);

            startSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), StartSessionActivity.class);
                    intent.putExtra("PATIENTID", patientId);
                    intent.putExtra("SLOTID", slotId);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
