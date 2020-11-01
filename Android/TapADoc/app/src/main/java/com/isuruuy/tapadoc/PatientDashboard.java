package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;

public class PatientDashboard extends AppCompatActivity {

    BottomNavigationView navigationView_dashboard;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID, height, weight, name;
    TextView greetText;
    String greetTime;
    Button viewDieticians, recordProgress;
    String bmi ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        try{
            this.getSupportActionBar().hide();
        }catch (NullPointerException e){}

        greetText = findViewById(R.id.textView_greet);

        recordProgress = findViewById(R.id.record_progress_patient_dashbaord_button);
        recordProgress.setOnClickListener(view -> {
            Intent i = new Intent(PatientDashboard.this, RecordProgress.class);
            startActivity(i);
            finish();
        });


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                height = documentSnapshot.getString("height");
                weight = documentSnapshot.getString("weight");
                bmi = documentSnapshot.getString("bmi");
                System.out.println("**** "+ bmi);
                name = documentSnapshot.getString("name");

                String greetUser = greetUser() + " "+ name;
                greetText.setText(greetUser);
                greetText.startAnimation(AnimationUtils.loadAnimation(PatientDashboard.this, android.R.anim.slide_in_left));

                if(bmi == null)
                {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast,
                            (ViewGroup) findViewById(R.id.custom_toast_container));

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
                else if(Double.parseDouble(bmi) > 24){
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_overweight,
                            (ViewGroup) findViewById(R.id.custom_toast_container_overweight));

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
                else
                {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_maintain,
                            (ViewGroup) findViewById(R.id.custom_toast_container_maintain));

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }

            }
        });

        navigationView_dashboard = findViewById(R.id.bottom_navigation_dashboard);
        navigationView_dashboard.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        firebaseAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(), PatientProfile.class));
                        finish();
                        return true;
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
                        finish();
                        return true;
                }
                return false;
            }
        });

        viewDieticians = findViewById(R.id.button_view_dieticians);
        viewDieticians.setOnClickListener(view -> {
            Intent intent = new Intent(PatientDashboard.this, AppointmentActivity.class);
            startActivity(intent);
        });

    }

    public String greetUser(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        System.out.println(timeOfDay);
        if(timeOfDay >= 0 && timeOfDay < 12){
            greetTime=  "Good Morning ";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greetTime=  "Good Afternoon ";
        }else if(timeOfDay >= 16 && timeOfDay <= 23){
            greetTime=  "Good Evening ";
        }
        return greetTime;
    }
}