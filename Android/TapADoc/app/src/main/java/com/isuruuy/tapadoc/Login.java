package com.isuruuy.tapadoc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText username, password;
    String username_str, password_str, userID;
    FirebaseAuth firebaseAuth;
    Button login_patient, register_patient;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_login_pin);

        login_patient = findViewById(R.id.button_signin_patient);
        login_patient.setOnClickListener(this);

        register_patient = findViewById(R.id.button_register_patient);
        register_patient.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_signin_patient)
        {
            username_str = username.getText().toString().trim();
            password_str = password.getText().toString().trim();

            if(validate_inputs())
            {
                firebaseAuth.signInWithEmailAndPassword(username_str, password_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            System.out.println("calling***");
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firestore.collection("users").document(userID);
                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                                    if(documentSnapshot.exists()){
                                        boolean isDoctor = documentSnapshot.getBoolean("isDoctor");
                                        if(isDoctor)
                                        {
                                            Toast.makeText(Login.this, "Doctor logged in successfully.", Toast.LENGTH_SHORT ).show();
                                            startActivity(new Intent(getApplicationContext(), DoctorDashboard.class));
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(Login.this, "Patient Logged in successfully.", Toast.LENGTH_SHORT ).show();
                                            startActivity(new Intent(getApplicationContext(), PatientDashboard.class));
                                            finish();
                                        }
                                    }

                                }

                            });
                        }
                        else
                        {
                            Toast.makeText(Login.this, "Error Occurred! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else if(view.getId() == R.id.button_register_patient)
        {
            startActivity(new Intent(getApplicationContext(), RegisterPatient.class));
            finish();
        }
    }

    public boolean validate_inputs()
    {
        if(TextUtils.isEmpty(username_str))
        {
            username.setError("Username(email) is required");
            return false;
        }
        else if(TextUtils.isEmpty(password_str))
        {
            password.setError("Password is required");
            return false;
        }
        else
        {
            return true;
        }
    }
}
