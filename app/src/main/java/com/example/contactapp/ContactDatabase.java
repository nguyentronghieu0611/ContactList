package com.example.contactapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDatabase extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Contact.db";

    public ContactDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + "tblContact" + " (" +
                "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "phonenumber" + " TEXT NOT NULL, " +
                "name" + " TEXT NOT NULL, " +
                "email" + " TEXT, " +
                "image" + " BLOB )");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertContact(Contact contact){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phonenumber", contact.phonenummber);
        values.put("name", contact.name);
        values.put("email", contact.email);
        values.put("image", contact.image);
        return sqLiteDatabase.insert("tblContact", null, values);
    }

    public Cursor updateContact(Contact contact){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phonenumber", contact.phonenummber);
        values.put("name", contact.name);
        values.put("email", contact.email);
        values.put("image", contact.image);
        return sqLiteDatabase.rawQuery("update tblContact set phonenumber = ?,name = ?, email = ?, image = ? where id = ?", new String[]{contact.phonenummber,contact.name,contact.email,contact.image.toString()});
    }
}
