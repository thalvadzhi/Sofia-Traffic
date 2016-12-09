package com.bearenterprises.sofiatraffic.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.bearenterprises.sofiatraffic.MainActivity;

import java.util.ArrayList;

/**
 * Created by thalv on 01-Jul-16.
 */
public class DbManipulator {

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbManipulator(Context context){
        dbHelper = new DbHelper(context);
        try {
            db = dbHelper.getWritableDatabase();
        }catch (SQLiteException e){
            ((MainActivity)context).makeSnackbar("Информацията за спирките все още се обновява");
        }
    }


    public void closeDb(){
        db.close();
    }

    /**
     * Inserts values into DB and returns the primary key value of the new row
     * @param values key value pairs of the type : column name - value
     * @return the primary key of the inserted row
     */
    public long insert(ContentValues values){
        //TODO catch exception when unique constraint is violated
        long primaryKey = -1;
        try {
            primaryKey = db.insert(DbHelper.FeedEntry.TABLE_NAME, null, values);
        }catch(SQLiteConstraintException e){
            Log.d("Constraint Violation", "A unique constraint was violated", e);
        }
        return primaryKey;
    }


    /**
     * Inserts all of the values in the db
      * @param values ArrayList of values to be inserted
     */
    public void insert(ArrayList<ContentValues> values){
        try {
            db.beginTransaction();
            for (ContentValues value : values) {
                insert(value);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    /**
     * Reads the selected lines from db and returns a cursor
     * @param selection the columns for the where clause
     * @param selectionArgs the values for the where clause
     * @param projection the columns for the select statement
     * @param sortOrder the sorting order
     * @return Cursor to browse the queried lines
     */
    public Cursor read(String selection, String[] selectionArgs, String[] projection, String sortOrder){
        return db.query(DbHelper.FeedEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    /**
     * Overload that queries for all of the lines and all of the columns
     * @return Cursor to browse the queried lines
     */
    public Cursor read(){
        return db.query(DbHelper.FeedEntry.TABLE_NAME, null, null, null, null, null, null);
    }

    public Cursor readRawQuery(String query, String[] args){
        return db.rawQuery(query, args);
    }

    public void dropDatabase(){
        db.execSQL(DbHelper.FeedEntry.SQL_DROP_DB);
    }

    public void deleteAll(){
        db.execSQL(DbHelper.FeedEntry.SQL_DELETE_ALL);
    }
}
