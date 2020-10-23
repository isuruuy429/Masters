package com.isuruuy.tapadoc.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Patient.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String db_create = "create table WeightProgress(\n" +
                "id INTEGER PRIMARY KEY autoincrement, \n" +
                "date TEXT, \n" +
                "weight REAL \n" +
                ")";
        sqLiteDatabase.execSQL(db_create);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Patient");
    }

    public Boolean insertUserData(String date, String weight){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        contentValues.put("weight", weight);
        long result = DB.insert("WeightProgress", null, contentValues);

        if (result == -1 ){
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from WeightProgress", null);
        return cursor;
    }
}
