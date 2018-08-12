package com.violenthoboenterprises.taskkiller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

public class Database extends SQLiteOpenHelper {

    public static final String DBNAME = "Notes.db";
    //Main Table
    public static final String TABLE = "notes_table";
    public static final String COL1 = "ID";
    public static final String COL2 = "NOTE";
    public static final String COL3 = "CHECKLIST";
    public static final String COL4 = "TIMESTAMP";
    public static final String COL5 = "TASK";
    public static final String COL6 = "DUE";
    public static final String COL7 = "KILLED";
    //TODO probably don't need broadcast. Just need to set alarm to ID instead.
    public static final String COL8 = "BROADCAST";
    public static final String COL9 = "REPEAT";
    public static final String COL10 = "OVERDUE";
    public static final String COL11 = "SNOOZED";
    public static final String COL12 = "SHOWONCE";
    public static final String COL13 = "INTERVAL";
    public static final String COL14 = "REPEATINTERVAL";
    public static final String COL15 = "IGNORED";

    //Alarm Table
    public static final String ATABLE = "alarms_table";
    public static final String ACOL1 = "ID";
    public static final String ACOL2 = "HOUR";
    public static final String ACOL3 = "MINUTE";
    public static final String ACOL4 = "AMPM";
    public static final String ACOL5 = "DAY";
    public static final String ACOL6 = "MONTH";
    public static final String ACOL7 = "YEAR";

    //Snooze Table
    public static final String STABLE = "snooze_table";
    public static final String SCOL1 = "ID";
    public static final String SCOL2 = "HOUR";
    public static final String SCOL3 = "MINUTE";
    public static final String SCOL4 = "AMPM";
    public static final String SCOL5 = "DAY";
    public static final String SCOL6 = "MONTH";
    public static final String SCOL7 = "YEAR";

    String TAG = "Data";

    public Database(Context context) {
        super(context, DBNAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE + " (ID INTEGER PRIMARY KEY, " +
                "NOTE TEXT, CHECKLIST BOOLEAN, TIMESTAMP TEXT, TASK TEXT, DUE BOOLEAN," +
                " KILLED BOOLEAN, BROADCAST INTEGER, REPEAT BOOLEAN, OVERDUE BOOLEAN, " +
                "SNOOZED BOOLEAN, SHOWONCE BOOLEAN, INTERVAL INTEGER, REPEATINTERVAL TEXT, IGNORED BOOLEAN/*, SNOOZETIMESTAMP TEXT*/)");
        db.execSQL("create table " + ATABLE + " (ID INTEGER PRIMARY KEY, " +
                "HOUR TEXT, MINUTE TEXT, AMPM TEXT, DAY TEXT, MONTH TEXT, YEAR TEXT)");
        db.execSQL("create table " + STABLE + " (ID INTEGER PRIMARY KEY, " +
                "HOUR TEXT, MINUTE TEXT, AMPM TEXT, DAY TEXT, MONTH TEXT, YEAR TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ATABLE);
        db.execSQL("DROP TABLE IF EXISTS " + STABLE);
        onCreate(db);
    }

    public boolean insertData(int id, String note, String task, int broadcast){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(COL1, id);
        content.put(COL2, note);
        content.put(COL3, false);
        content.put(COL4, "0");
        content.put(COL5, task);
        content.put(COL6, false);
        content.put(COL7, false);
        content.put(COL8, broadcast);
        content.put(COL9, false);
        content.put(COL10, false);
        content.put(COL11, false);
        content.put(COL12, false);
        content.put(COL13, 0);
        content.put(COL14, "");
        content.put(COL15, false);
//        content.put(COL16, "0");
        long result = db.insert(TABLE, null, content);
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean insertAlarmData(int id, String hour, String minute, String ampm, String day, String month, String year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(ACOL1, id);
        content.put(ACOL2, hour);
        content.put(ACOL3, minute);
        content.put(ACOL4, ampm);
        content.put(ACOL5, day);
        content.put(ACOL6, month);
        content.put(ACOL7, year);
        long result = db.insert(ATABLE, null, content);
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

    public boolean insertSnoozeData(int id, String hour, String minute, String ampm, String day, String month, String year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(SCOL1, id);
        content.put(SCOL2, hour);
        content.put(SCOL3, minute);
        content.put(SCOL4, ampm);
        content.put(SCOL5, day);
        content.put(SCOL6, month);
        content.put(SCOL7, year);
        long result = db.insert(STABLE, null, content);
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
        Cursor result = db.rawQuery("select * from " + TABLE + " where " + COL1
                + " == " + id, null);
        return result;
    }

    public Cursor getAllAlarmData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + ATABLE, null);
        return result;
    }

    public Cursor getAllSnoozeData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + STABLE, null);
        return result;
    }

    public Cursor getAlarmData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + ATABLE + " where " + ACOL1
                + " == " + id, null);
        return result;
    }

    public Cursor getSnoozeData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + STABLE + " where " + SCOL1
                + " == " + id, null);
        return result;
    }

    public boolean updateData(String id, String note, Boolean checklist){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL1, id);
        if(MainActivity.inNote) {
            content.put(COL2, note);
        }else if(MainActivity.inChecklist){
            content.put(COL3, checklist);
        }else{
            //decrementing id value because higher ranking task was deleted
            //content.put(COL1, (Integer.valueOf(id) - 1));
            content.put(COL2, note);
            content.put(COL3, checklist);
        }
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateDue(String id, Boolean due){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL6, due);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateSnooze(String id, Boolean snooze){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL11, snooze);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean removeTimestamp(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL4, 0);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateKilled(String id, Boolean killed){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL7, killed);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateName(String id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL5, name);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateRepeat(String id, Boolean repeat){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL9, repeat);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

//    public boolean updateAlarmData(String id, Date time){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues content = new ContentValues();
//        content.put(COL4, String.valueOf(time));
//        db.update(TABLE, content, "ID = ?", new String[] {id});
//        return true;
//    }

    //TODO why do all these methods return a boolean? can they be void?
    public boolean updateTimestamp(String id, String timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(COL4, timestamp);
//        content.put(COL10, true);
        Log.i(TAG, "New stamp: " + timestamp);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateOverdue(String id, Boolean overdue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL10, overdue);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateShowOnce(String id, Boolean showOnce){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL12, showOnce);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateInterval(String id, String interval){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL13, interval);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateRepeatInterval(String id, String interval){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL14, interval);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateIgnored(String id, boolean ignored){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL15, ignored);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

//    public boolean updateSnoozeTimestamp(String id, String stamp){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues content = new ContentValues();
//        content.put(COL16, stamp);
//        db.update(TABLE, content, "ID = ?", new String[] {id});
//        return true;
//    }

    public boolean updateAlarmData(String id, String hour, String minute, String ampm, String day, String month, String year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(ACOL1, id);
        content.put(ACOL2, hour);
        content.put(ACOL3, minute);
        content.put(ACOL4, ampm);
        content.put(ACOL5, day);
        content.put(ACOL6, month);
        content.put(ACOL7, year);
        db.update(ATABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public boolean updateSnoozeData(String id, String hour, String minute, String ampm, String day, String month, String year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(SCOL1, id);
        content.put(SCOL2, hour);
        content.put(SCOL3, minute);
        content.put(SCOL4, ampm);
        content.put(SCOL5, day);
        content.put(SCOL6, month);
        content.put(SCOL7, year);
        db.update(STABLE, content, "ID = ?", new String[] {id});
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

    public boolean addChecklist(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(COL1, id);
        content.put(COL3, true);
        db.update(TABLE, content, "ID = ?", new String[] {id});
        return true;
    }

    public Integer deleteData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE, "ID = ?", new String[] {id});
    }

    public Integer deleteAlarmData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ATABLE, "ID = ?", new String[] {id});
    }

    public Integer deleteSnoozeData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(STABLE, "ID = ?", new String[] {id});
    }

}
