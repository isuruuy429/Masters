package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.type.DateTime;
import com.isuruuy.tapadoc.data.DBHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class RecordProgress extends AppCompatActivity {

    Button buttonProgressWeightAdd, buttonProgressWeightView;
    EditText editTextProgressWeight;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    LineDataSet lineDataSet;
    LineData lineData;
    LineChart lineChart;
    BottomNavigationView navigationView_record_progress;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_progress);

        try{
            this.getSupportActionBar().hide();
        }catch (NullPointerException e){}

        firebaseAuth = FirebaseAuth.getInstance();

        buttonProgressWeightAdd = findViewById(R.id.progress_submit_weight_button);
        buttonProgressWeightView = findViewById(R.id.view_progress_button);
        editTextProgressWeight = findViewById(R.id.progress_weight);
        lineDataSet = new LineDataSet(null, null);
        lineChart = findViewById(R.id.progresschart);
        lineDataSet.setLineWidth(4);

        ArrayList<ILineDataSet> datasets = new ArrayList<>();

        dbHelper = new DBHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        buttonProgressWeightAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight_str = editTextProgressWeight.getText().toString().trim();
                LocalDate localDate = LocalDate.now();
                String date_str = DateTimeFormatter.ofPattern("yyy/MM/dd").format(localDate);

                Boolean checkInsertData = dbHelper.insertUserData(date_str, weight_str);
                if(checkInsertData)
                {
                    Toast.makeText(RecordProgress.this, "Weight is recorded.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RecordProgress.this, "Error occurred when inserting data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonProgressWeightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = dbHelper.getData();
                if(cursor.getCount() == 0 ){
                    Toast.makeText(RecordProgress.this, "No entry exists.", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (cursor.moveToNext()){
                    buffer.append("date:" + cursor.getString(1)+ "\n");
                    buffer.append("weight:" + cursor.getString(2)+ "\n");
                    buffer.append("\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(RecordProgress.this);
                builder.setCancelable(true);
                builder.setTitle("Weight Progress");
                builder.setMessage(buffer.toString());
                builder.show();

                lineDataSet.setValues(getDataValues());
                lineDataSet.setLabel("Weight Progress");
                datasets.clear();
                datasets.add(lineDataSet);
                lineData = new LineData(datasets);
                lineChart.clear();
                lineChart.setData(lineData);
                lineChart.invalidate();
            }
        });

        navigationView_record_progress = findViewById(R.id.bottom_navigation_record_progress);
        navigationView_record_progress.setSelectedItemId(R.id.nav_profile);
        navigationView_record_progress.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        firebaseAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(), RecordProgress.class));
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
    }

    private ArrayList<Entry> getDataValues(){
        ArrayList<Entry> datavalues = new ArrayList<>();
        String []columns = {"date", "weight"};

        Cursor cursor = dbHelper.getData();
        for(int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            datavalues.add(new Entry(cursor.getFloat(0),cursor.getFloat(2)));
        }
        return datavalues;
    }
}