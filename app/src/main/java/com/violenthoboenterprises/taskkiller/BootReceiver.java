package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    String TAG = "BootReceiver";
    static boolean booted = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            booted = true;

            Database db = new Database(context);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alertIntent;
            PendingIntent pendIntent;

            int taskListSize = db.getTotalRows();

            ArrayList<Integer> IDList = db.getIDs();

            for( int i = 0 ; i < taskListSize ; i++ ) {

                int dbID = 0;
                String dbNote = "";
                String dbTimestamp = "";
                String dbTask = "";
                Boolean dbDue = false;
                Boolean dbKilled = false;
                int dbBroadcast = 0;
                Boolean dbRepeat = false;
                Boolean dbOverdue = false;
                Boolean dbSnooze = false;
                String dbRepeatInterval = "";
                Boolean dbIgnored = false;
                int dbChecklistSize = 0;
                String dbSnoozedStamp = "";
                Cursor dbResult = db.getData(IDList.get(i));
                while (dbResult.moveToNext()) {
                    dbID = dbResult.getInt(0);
                    dbNote = dbResult.getString(1);
                    dbTimestamp = dbResult.getString(3);
                    dbTask = dbResult.getString(4);
                    dbDue = dbResult.getInt(5) > 0;
                    dbKilled = dbResult.getInt(6) > 0;
                    dbBroadcast = dbResult.getInt(7);
                    dbRepeat = dbResult.getInt(8) > 0;
                    dbOverdue = dbResult.getInt(9) > 0;
                    dbSnooze = dbResult.getInt(10) > 0;
                    dbRepeatInterval = dbResult.getString(13);
                    dbIgnored = dbResult.getInt(14) > 0;
                    dbChecklistSize = dbResult.getInt(17);
                    dbSnoozedStamp = dbResult.getString(21);
                }
                dbResult.close();

                if(dbDue && !dbOverdue) {
                    alertIntent = new Intent(context, AlertReceiver.class);
                    alertIntent.putExtra("ToDo", dbTask);
                    alertIntent.putExtra("broadId", dbBroadcast);
                    pendIntent = PendingIntent.getBroadcast(context, dbBroadcast,
                            alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    long dueTimestamp = Long.parseLong(dbTimestamp + "000");
                    alarmManager.set(AlarmManager.RTC, dueTimestamp, pendIntent);
                }
            }
        }
    }
}
