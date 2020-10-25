package com.isuruuy.tapadoc;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    ArrayList<Doctor> profiles;
    Context context;

    Adapter(Context c, ArrayList<Doctor> doctor){
        context = c;
        profiles = doctor;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_view, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.name.setText(profiles.get(position).getName());
        viewHolder.speciality.setText(profiles.get(position).getSpecialities());
        viewHolder.userid = profiles.get(position).getId();
        Picasso.get().load(profiles.get(position).getProfilePicture()).into(viewHolder.profilepic);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,speciality;
        ImageView profilepic;
        Button bookNow;
        String userid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.doctor_name_cardview);
            speciality = itemView.findViewById(R.id.doctor_speciality_cardview);
            profilepic = itemView.findViewById(R.id.doctor_profilepic_cardview);
            bookNow = itemView.findViewById(R.id.button_booknow);

            bookNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("INSIDE BUtton clock "+ userid);

                    Intent intent = new Intent(view.getContext(), ViewAvailableSlots.class);
                    intent.putExtra("USERID", userid);
                    view.getContext().startActivity(intent);
                }
            });

        }
    }

}
