package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DoctorDashboard extends AppCompatActivity {

    BottomNavigationView navigationView_doctor_dashboard;
    FirebaseAuth firebaseAuth;
    Button addSlot, viewAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        try{
            this.getSupportActionBar().hide();
        }catch (NullPointerException e){}

        firebaseAuth = FirebaseAuth.getInstance();

        addSlot = findViewById(R.id.button_doc_addslot);
        viewAppointments = findViewById(R.id.doctor_viewappointments_btn);


        addSlot.setOnClickListener(view -> {
            Intent intent = new Intent(DoctorDashboard.this, AddSlotsDoctorActivity.class);
            startActivity(intent);
            finish();
        });

        viewAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorDashboard.this, ViewAppointments.class);
                startActivity(i);
                finish();
            }
        });

        navigationView_doctor_dashboard = findViewById(R.id.bottom_navigation_doctor_dashboard);
        navigationView_doctor_dashboard.setSelectedItemId(R.id.nav_home);
        navigationView_doctor_dashboard.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) item -> {
            switch (item.getItemId()) {
                case R.id.nav_logout:
                    firebaseAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                    return true;
                case R.id.nav_profile:
                    startActivity(new Intent(getApplicationContext(), DoctorProfile.class));
                    finish();
                    return true;
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), DoctorDashboard.class));
                    finish();
                    return true;
            }
            return false;
        });

    }

}