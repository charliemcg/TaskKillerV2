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

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Database db = new Database(context);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alertIntent;
            PendingIntent pendIntent;

            int taskListSize = db.getTotalRows();

            ArrayList<Integer> IDList = db.getIDs();

            for( int i = 0 ; i < taskListSize ; i++ ) {

                String dbTimestamp = "";
                String dbTask = "";
                Boolean dbDue = false;
                int dbBroadcast = 0;
                Boolean dbOverdue = false;
                Cursor dbResult = db.getData(IDList.get(i));
                while (dbResult.moveToNext()) {
                    dbTimestamp = dbResult.getString(3);
                    dbTask = dbResult.getString(4);
                    dbDue = dbResult.getInt(5) > 0;
                    dbBroadcast = dbResult.getInt(7);
                    dbOverdue = dbResult.getInt(9) > 0;
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
