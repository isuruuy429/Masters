package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterPatient extends AppCompatActivity implements View.OnClickListener {

    DatePickerDialog picker;
    EditText dateOfBirth, patientName, email, pin, reconfirmPin;
    Button register;
    String dateOfBirth_str, patientName_str, email_str, pin_str, reconfirmPin_str,gender_str;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;
    String [] genderArray;
    Spinner spinner_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);

        spinner_gender = findViewById(R.id.register_gender);
        populateGenderValues();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        dateOfBirth = findViewById(R.id.editTextDate_register_dob);
        dateOfBirth.setInputType(InputType.TYPE_NULL);
        dateOfBirth.setOnClickListener(this);

        patientName = findViewById(R.id.editText_register_name);
        email = findViewById(R.id.editTextText_register_email);
        pin = findViewById(R.id.editText_register_pin);
        reconfirmPin = findViewById(R.id.editText_register_reconfirmpin);

        register = findViewById(R.id.button_register_user);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.editTextDate_register_dob)
        {
            final Calendar calender = Calendar.getInstance();
            int day = calender.get(Calendar.DAY_OF_MONTH);
            int month = calender.get(Calendar.MONTH);
            int year = calender.get(Calendar.YEAR);

            picker = new DatePickerDialog(RegisterPatient.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    dateOfBirth.setText(day + "/" + (month + 1) + "/" + year);
                }
            }, year, month, day);

            //It disables the future dates.
            picker.getDatePicker().setMaxDate(System.currentTimeMillis());
            picker.show();
        }

        if (view.getId() == R.id.button_register_user)
        {
            dateOfBirth_str = dateOfBirth.getText().toString();
            patientName_str = patientName.getText().toString();
            email_str = email.getText().toString();
            pin_str = pin.getText().toString();
            reconfirmPin_str = reconfirmPin.getText().toString();
            gender_str = spinner_gender.getSelectedItem().toString().trim();

            if(validate_user_registration_inputs())
            {
                firebaseAuth.createUserWithEmailAndPassword(email_str,pin_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firestore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("name", patientName_str);
                            user.put("email", email_str);
                            user.put("dob", dateOfBirth_str);
                            user.put("gender", gender_str);
                            user.put("isDoctor",false);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "onSuccess: user profile created for " + userID);
                                    Toast.makeText(RegisterPatient.this, "User registration is successful.", Toast.LENGTH_SHORT ).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error adding document", e);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(RegisterPatient.this, "Error Occurred! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public boolean validate_user_registration_inputs()
    {
        if(patientName_str.trim().length() == 0 )
        {
            patientName.requestFocus();
            patientName.setError("Enter patient name");
            return false;
        }
        else if(dateOfBirth_str.trim().length() == 0 )
        {
            dateOfBirth.requestFocus();
            dateOfBirth.setError("Enter Date of Birth");
            return false;
        }
        else if(email_str.trim().length() == 0 )
        {
            email.requestFocus();
            email.setError("Enter a valid Email Address");
            return false;
        }
         else if(pin_str.trim().length() != 6 || reconfirmPin_str.trim().length() != 6 )
         {
             pin.requestFocus();
             pin.setError("Enter PIN of 6 digits.");
             pin.setText("");
             reconfirmPin.setText("");
             return false;
         }
         else if(!pin_str.equals(reconfirmPin_str))
        {
            reconfirmPin.requestFocus();
            reconfirmPin.setError("Check PIN values");
            pin.setText("");
            reconfirmPin.setText("");
            return false;
        }
        else
        {
            return true;
        }

    }

    private void populateGenderValues() {
        genderArray = new String[]{"Male", "Female"};
        ArrayAdapter<String> gender = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderArray);
        gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(gender);
    }

}