package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddSlotsDoctorActivity extends AppCompatActivity {

    Button add;
    EditText dateSlot;
    DatePickerDialog picker;
    TimePicker timePicker;
    private String userID;
    EditText timetv;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_slots_doctor);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        add = findViewById(R.id.add_button_addslot_popup);
        dateSlot = findViewById(R.id.addslot_date);
        timePicker = findViewById(R.id.timepicker);
        timetv = findViewById(R.id.invisibleTimetext);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {

                int selectedHour = hourOfDay;
                int selectedMin = minute;
                timetv.setText(selectedHour+":"+selectedMin);
            }
        });

        dateSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calender = Calendar.getInstance();
                int day = calender.get(Calendar.DAY_OF_MONTH);
                int month = calender.get(Calendar.MONTH);
                int year = calender.get(Calendar.YEAR);

                picker = new DatePickerDialog(AddSlotsDoctorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateSlot.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);

                //It disables the future dates.
                picker.getDatePicker().setMinDate(System.currentTimeMillis());
                picker.show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date_str = dateSlot.getText().toString().trim();
                String time_str = timetv.getText().toString().trim();

                if(!date_str.isEmpty() && !time_str.isEmpty())
                {
                    userID = firebaseAuth.getCurrentUser().getUid();
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String timeStamp_str= String.valueOf(timestamp.getTime());
                    DocumentReference documentReference = firestore.collection("users").document(userID).collection("availableSlots").document(timeStamp_str);
                    Map<String,Object> slot = new HashMap<>();
                    slot.put("date", date_str);
                    slot.put("time", time_str);
                    slot.put("id", timeStamp_str);
                    slot.put("docid", userID);
                    documentReference.set(slot).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddSlotsDoctorActivity.this, "The time slot added successfully.", Toast.LENGTH_SHORT ).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddSlotsDoctorActivity.this, "Error occurred while adding the new slot.", Toast.LENGTH_SHORT ).show();
                        }
                    });
                }
            }
        });
    }

    public void addSlot(){

    }

}