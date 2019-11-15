package com.fap.bnotion.findaplace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "fap.db";
    public static final String TABLE_NAME = "fap_table";
    private static final String CREATE_TABLE = "create table " + TABLE_NAME +" (id INTEGER PRIMARY KEY AUTOINCREMENT, image_url TEXT," +
            " profile_url TEXT, title TEXT, category TEXT, location TEXT, price TEXT, number_of_toilets TEXT, number_of_baths TEXT, state TEXT, user_id TEXT, " +
            "username TEXT, mobile_number TEXT, status TEXT, email TEXT, description TEXT, timestamp TEXT)";
    static final int DB_VERSION = 1;
    public static final String COL_1 = "id";
    public static final String COL_2 = "image_url";
    public static final String COL_3 = "profile_url";
    public static final String COL_4 = "title";
    public static final String COL_5 = "category";
    public static final String COL_6 = "location";
    public static final String COL_7 = "price";
    public static final String COL_8 = "number_of_toilets";
    public static final String COL_9 = "number_of_baths";
    public static final String COL_10 = "state";
    public static final String COL_11 = "user_id";
    public static final String COL_12 = "username";
    public static final String COL_13 = "mobile_number";
    public static final String COL_14 = "status";
    public static final String COL_15 = "email";
    public static final String COL_16 = "description";
    public static final String COL_17 = "timestamp";
    public static final String COL_18 = "image_urls";
    public DatabaseHelper( Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void insertData(String image_url, String profile_url, String title, String category, String location, String price, String number_of_toilets, String number_of_baths, String state, String user_id, String username, String mobile_number, String status, String email, ArrayList<String> image_urls, String description, String timestamp){
SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, image_url);
        contentValues.put(COL_3, profile_url);
        contentValues.put(COL_4, title);
        contentValues.put(COL_5, category);
        contentValues.put(COL_6, location);
        contentValues.put(COL_7, price);
        contentValues.put(COL_8, number_of_toilets);
        contentValues.put(COL_9, number_of_baths);
        contentValues.put(COL_10, state);
        contentValues.put(COL_11, user_id);
        contentValues.put(COL_12, username);
        contentValues.put(COL_13, mobile_number);
        contentValues.put(COL_14, status);
        contentValues.put(COL_15, email);
        contentValues.put(COL_16, description);
        contentValues.put(COL_17, timestamp);
        contentValues.put(COL_18, String.valueOf(image_urls));
        db.insert(TABLE_NAME, null,contentValues);


    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }
    public boolean checkAlreadyExist(String desc)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + TABLE_NAME + " where " + COL_16 + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{desc});
        if (cursor.getCount() > 0)
        {
            return false;
        }
        else
            return true;
    }
    public Integer deleteData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
       return db.delete(TABLE_NAME, COL_1 + "=?", new String[]{id});
    }
    public int getId(String desc){
        int id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id  FROM  fap_table WHERE description = '"+desc+"'" , null);
        if (cursor != null) {
            if (cursor.moveToFirst())
                id =  cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return id;
    }
}
