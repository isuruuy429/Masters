package com.isuruuy.tapadoc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PrescriptionsAdapter extends RecyclerView.Adapter<PrescriptionsAdapter.ViewHolderPrescriptions>{

    ArrayList<Prescriptions> prescriptions;
    Context context;

    public PrescriptionsAdapter(ArrayList<Prescriptions> prescriptions, Context context) {
        this.prescriptions = prescriptions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderPrescriptions onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PrescriptionsAdapter.ViewHolderPrescriptions(LayoutInflater.from(context).inflate(R.layout.custom_view4, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPrescriptions holder, int position) {
        holder.medicine.setText(prescriptions.get(position).getMedicine());
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    public class ViewHolderPrescriptions extends RecyclerView.ViewHolder{

        TextView medicine;

        public ViewHolderPrescriptions(@NonNull View itemView) {
            super(itemView);
            medicine = itemView.findViewById(R.id.past_prescriptions_textview);
        }
    }
}
