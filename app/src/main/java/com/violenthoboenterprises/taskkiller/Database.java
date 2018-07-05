package com.violenthoboenterprises.taskkiller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

    public static final String DBNAME = "Notes.db";
    public static final String TABLE = "notes_table";
    public static final String COL1 = "ID";
    public static final String COL2= "NOTE";

    public Database(Context context) {
        super(context, DBNAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE + " (ID INTEGER PRIMARY KEY, NOTE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public boolean insertData(int id, String note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(COL1, id);
        content.put(COL2, note);
        long result = db.insert(TABLE, null, content);
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE, null);
        return result;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE + " where " + COL1 + " == " + id, null);
        return result;
    }

    public boolean updateData(String id, String note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(COL1, id);
        content.put(COL2, note);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateAfterDelete(String id, String note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(COL1, id);
        content.put(COL2, note);
        db.update(TABLE, content, "NOTE = ?", new String[] {note});
        return true;
    }

    public Integer deleteData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE, "ID = ?", new String[] {id});
    }

}
