package com.isuruuy.tapadoc;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class PatientProfile extends AppCompatActivity {

    BottomNavigationView navigationView_profile;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    EditText name, email,dob, occupation,height,weight,bmi, gender;
    Button save_data;
    String gender_str, occupation_str, height_str, weight_str, bmi_str;
    double bmi_double;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        try{
            this.getSupportActionBar().hide();
        }catch (NullPointerException e){}

        save_data = findViewById(R.id.profile_save_button);

        String userID;
        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        dob = findViewById(R.id.profile_dob);
        occupation = findViewById(R.id.profile_occupation);
        height = findViewById(R.id.profile_height);
        weight = findViewById(R.id.profile_weight);
        bmi = findViewById(R.id.profile_bmi);
        gender = findViewById(R.id.profile_gender);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                name.setText(documentSnapshot.getString("name"));
                email.setText(documentSnapshot.getString("email"));
                dob.setText(documentSnapshot.getString("dob"));

                occupation_str = documentSnapshot.getString("occupation");
                gender_str = documentSnapshot.getString("gender");
                height_str = documentSnapshot.getString("height");
                weight_str = documentSnapshot.getString("weight");
                bmi_str = documentSnapshot.getString("bmi");
                if(!TextUtils.isEmpty(occupation_str) || !TextUtils.isEmpty(gender_str) ||
                        !TextUtils.isEmpty(height_str) || !TextUtils.isEmpty(weight_str) ||
                        !TextUtils.isEmpty(bmi_str))
                {
                    occupation.setText(occupation_str);
                    gender.setText(gender_str);
                    height.setText(height_str);
                    weight.setText(weight_str);
                    bmi.setText(bmi_str);
                }

            }
        });

        navigationView_profile = findViewById(R.id.bottom_navigation_profile);
        navigationView_profile.setSelectedItemId(R.id.nav_profile);

        navigationView_profile.setOnNavigationItemSelectedListener(item -> {
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
        });

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                height_str = height.getText().toString().trim();
                weight_str = weight.getText().toString().trim();
                if(!TextUtils.isEmpty(weight_str) && !TextUtils.isEmpty(height_str)) {
                    bmi_double = calculateBmi(height_str, weight_str);
                    bmi.setText(String.valueOf(bmi_double));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        save_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                occupation_str = occupation.getText().toString().trim();
                Map<String,Object> user = new HashMap<>();
                user.put("gender", gender_str);
                user.put("occupation", occupation_str);
                user.put("height", height_str);
                user.put("weight", weight_str);
                user.put("bmi", Double.toString(bmi_double));

                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "onSuccess: Data successfully saved " + userID);
                        Toast.makeText(PatientProfile.this, "Successfully saved.", Toast.LENGTH_SHORT ).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
            }
        });
    }

    private double calculateBmi(String height, String weight)
    {
        double height_db = Double.parseDouble(height);
        double weight_db = Double.parseDouble(weight);
        double bmi=weight_db/((height_db/100.0)*(height_db/100.0));
        BigDecimal bd = new BigDecimal(bmi).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
