package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

public class AlertReceiver extends BroadcastReceiver {

    String TAG = "AlertReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        //retrieving task name to set as notification name
        createNotification(context, String.valueOf(intent.getStringExtra("ToDo")),
                "", "", intent.getIntExtra("broadId", 0), intent.getBooleanExtra("snoozeStatus", false));

    }

    public void createNotification(Context context, String msg, String msgText,
                                   String msgAlert, int broadId, boolean snoozeStatus){

//        Log.i(TAG, "I'm in here alertReceiver");

        //defining intent and action to perform
        PendingIntent notificIntent = PendingIntent.getActivity(context, 1,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder builder;
        RemoteViews remoteViews;

        //getting task data
        String dbTimestamp = "";
        String dbTask = "";
        Boolean dbRepeat = false;
        boolean dbOverdue = false;
        Boolean dbSnoozed = false;
        String dbRepeatInterval = "";
        Boolean dbManualKill = false;
        Boolean dbKilledEarly = false;
        Cursor dbResult = MainActivity.db.getData(/*Integer.parseInt(
                MainActivity.sortedIDs.get(*/broadId/*))*/);
        while (dbResult.moveToNext()) {
            dbTimestamp = dbResult.getString(3);
            dbTask = dbResult.getString(4);
            dbRepeat = dbResult.getInt(8) > 0;
            dbOverdue = dbResult.getInt(9) > 0;
            dbSnoozed = dbResult.getInt(10) > 0;
            dbRepeatInterval = dbResult.getString(13);
            dbManualKill = dbResult.getInt(18) > 0;
            dbKilledEarly = dbResult.getInt(19) > 0;
        }
        dbResult.close();

        //getting alarm data
        Cursor alarmResult = MainActivity.db.getAlarmData(/*
                Integer.parseInt(MainActivity.sortedIDs.get(*/broadId/*))*/);
        String alarmHour = "";
        String alarmMinute = "";
        String alarmAmpm = "";
        String alarmDay = "";
        String alarmMonth = "";
        String alarmYear = "";
        while(alarmResult.moveToNext()){
            alarmHour = alarmResult.getString(1);
            alarmMinute = alarmResult.getString(2);
            alarmAmpm = alarmResult.getString(3);
            alarmDay = alarmResult.getString(4);
            alarmMonth = alarmResult.getString(5);
            alarmYear = alarmResult.getString(6);
        }
        alarmResult.close();

        //allows for notifications
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(MainActivity.lightDark) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_light);
            remoteViews.setTextViewText(R.id.notif_title, dbTask);
        }else{
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
            remoteViews.setTextViewText(R.id.notif_title, dbTask);
        }

        //Setting up notification channel for Oreo
        final String notificChannelId = "notification_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    notificChannelId, "notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Notifications about due being due");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Integer.parseInt(MainActivity.highlightDec));
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Building the notification
        builder = new NotificationCompat.Builder(context, notificChannelId)
                .setSmallIcon(R.drawable.small_notific_icon).setLargeIcon(BitmapFactory
                        .decodeResource(context.getResources(), R.drawable.ic_launcher_og))
                .setContentTitle(context.getString(R.string.killThisTask)).setTicker(msgAlert)
                .setWhen(0).setContentText(dbTask).setStyle(new NotificationCompat.BigTextStyle())
                .setColorized(true).setColor(Color.parseColor(MainActivity.highlight))
                .setCustomContentView(remoteViews).setLights(Integer.parseInt
                        (MainActivity.highlightDec), 500, 500).setDefaults
                        (NotificationCompat.DEFAULT_SOUND).setContentIntent(notificIntent)
                .setAutoCancel(true);

        if(!dbRepeat && MainActivity.remindersAvailable){

            notificationManager.notify(1, builder.build());

        //need to set up next notification for repeating task
        } else {

            //don't inform user that task is due if they marked it as done
            if(!dbKilledEarly && MainActivity.remindersAvailable){

                notificationManager.notify(1, builder.build());

            }else{

                MainActivity.db.updateKilledEarly(String.valueOf(/*
                        MainActivity.sortedIDs.get(*/broadId/*)*/), false);

            }

            //cancelling any snoozed alarm data
            MainActivity.db.updateSnoozeData(String.valueOf(broadId),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");

            MainActivity.db.updateSnoozedTimestamp(String.valueOf(broadId), "0");

            MainActivity.db.updateSnooze(String.valueOf(broadId), false);

            dbSnoozed = false;

            MainActivity.db.updateIgnored(String.valueOf(broadId), false);

            //snoozed notifications cannot corrupt regular repeating notifications
            if(dbRepeatInterval.equals("day") && !snoozeStatus){

                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the millisecond level
                long futureStamp = Long.parseLong(dbTimestamp) + (AlarmManager.INTERVAL_DAY / 1000);
                String tempTimestamp = "";
                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                    Cursor tempResult = MainActivity.db.getData(i);
                    while (tempResult.moveToNext()) {
                        tempTimestamp = tempResult.getString(3);
                    }
                    tempResult.close();
                    if(futureStamp == Long.parseLong(tempTimestamp)){
                        futureStamp++;
                        i = 0;
                    }

                }

                //updating timestamp
                MainActivity.db.updateTimestamp(String.valueOf(broadId),
                        String.valueOf(futureStamp));

                //setting the name of the task for which the
                // notification is being set
                MainActivity.alertIntent.putExtra("ToDo", dbTask);
                MainActivity.alertIntent.putExtra("broadId", broadId);

                //Setting alarm
                MainActivity.pendIntent = PendingIntent.getBroadcast(
                        context, broadId, MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar alarmCalendar = Calendar.getInstance();
                Long diff = Long.valueOf(0);

                if(!dbKilledEarly) {
                    Calendar currentCal = Calendar.getInstance();
                    Calendar futureCal = Calendar.getInstance();
                    futureCal.setTimeInMillis(futureStamp * 1000);
                    diff = futureCal.getTimeInMillis() - currentCal.getTimeInMillis();
                    diff = diff / 1000;
                    if(diff < 86400) {
                        Log.i(TAG, "one");
                        MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong
                                (String.valueOf(futureStamp) + "000"), MainActivity.pendIntent);
                    }else{
                        Log.i(TAG, "two");
                        int daysOut = (int) (diff / 86400);
                        MainActivity.alarmManager.set(AlarmManager.RTC, (Long.parseLong
                                (String.valueOf(futureStamp) + "000") - (86400000 * daysOut)),
                                MainActivity.pendIntent);
                    }
                }else{
                    Log.i(TAG, "three");
                    MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong
                            (String.valueOf(dbTimestamp) + "000"), MainActivity.pendIntent);
                }

                Calendar currentCal = Calendar.getInstance();

                alarmCalendar.setTimeInMillis(Long.parseLong
                        (String.valueOf(futureStamp) + "000") - AlarmManager.INTERVAL_DAY);

                //alarm data is already updated if user marked task as done
                if(!dbManualKill
                        && (Integer.parseInt(alarmDay) != currentCal.get(Calendar.DAY_OF_MONTH))){
//                    currentCal = Calendar.getInstance();
//                    Calendar futureCal = Calendar.getInstance();
//                    futureCal.setTimeInMillis((futureStamp * 1000) - AlarmManager.INTERVAL_DAY);
                    diff = (Long.parseLong(String.valueOf(futureStamp) + "000")
                            - AlarmManager.INTERVAL_DAY)
                            - currentCal.getTimeInMillis();
                    diff = diff / 1000;

                    if(diff > 0) {
                        int daysOut = (int) (diff / 86400);
                        futureStamp =  futureStamp - (86400 * (daysOut + 1));
                        alarmCalendar.setTimeInMillis(Long.parseLong
                                (String.valueOf(futureStamp) + "000")
                                - AlarmManager.INTERVAL_DAY);
                    }

                    Log.i(TAG, "Minute: " + alarmCalendar.get(Calendar.MINUTE));

                    //updating due date in database
                    MainActivity.db.updateAlarmData(String.valueOf(broadId),
                            String.valueOf(alarmCalendar.get(Calendar.HOUR)),
                            String.valueOf(alarmCalendar.get(Calendar.MINUTE)),
                            String.valueOf(alarmCalendar.get(Calendar.AM_PM)),
                            String.valueOf(alarmCalendar.get(Calendar.DAY_OF_MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.YEAR)));

                }

                MainActivity.db.updateManualKill(String.valueOf(broadId), false);

            }else if(dbRepeatInterval.equals("week") && !snoozeStatus){

                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the millisecond level
                long futureStamp = Long.parseLong(dbTimestamp) +
                        ((AlarmManager.INTERVAL_DAY * 7) / 1000);
                String tempTimestamp = "";
                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                    Cursor tempResult = MainActivity.db.getData(i);
                    while (tempResult.moveToNext()) {
                        tempTimestamp = tempResult.getString(3);
                    }
                    tempResult.close();
                    if(futureStamp == Long.parseLong(tempTimestamp)){
                        futureStamp++;
                        i = 0;
                    }

                }

                //updating timestamp
                MainActivity.db.updateTimestamp(String.valueOf(broadId), String.valueOf(futureStamp));

                //setting the name of the task for which the
                // notification is being set
                MainActivity.alertIntent.putExtra("ToDo", dbTask);
                MainActivity.alertIntent.putExtra("broadId", broadId);

                //Setting alarm
                 MainActivity.pendIntent = PendingIntent.getBroadcast(
                        context, broadId, MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

//                MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong
//                                (String.valueOf(futureStamp) + "000"), MainActivity.pendIntent);

                Calendar alarmCalendar = Calendar.getInstance();
                Long diff = Long.valueOf(0);

                if(!dbKilledEarly) {
                    Calendar currentCal = Calendar.getInstance();
                    Calendar futureCal = Calendar.getInstance();
                    futureCal.setTimeInMillis(futureStamp * 1000);
                    diff = futureCal.getTimeInMillis() - currentCal.getTimeInMillis();
                    diff = diff / 1000;
                    if(diff < (86400 * 7)) {
                        MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong
                                (String.valueOf(futureStamp) + "000"), MainActivity.pendIntent);
                    }else{
                        int daysOut = (int) (diff / (86400 * 7));
                        MainActivity.alarmManager.set(AlarmManager.RTC, (Long.parseLong
                                        (String.valueOf(futureStamp) + "000") - ((86400000 * 7) * daysOut)),
                                MainActivity.pendIntent);
                    }
                }else{
                    MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong
                            (String.valueOf(dbTimestamp) + "000"), MainActivity.pendIntent);
                }

                Calendar currentCal = Calendar.getInstance();

                alarmCalendar.setTimeInMillis(Long.parseLong(String.valueOf(futureStamp)
                        + "000") - (AlarmManager.INTERVAL_DAY * 7));

                //alarm data is already updated if user marked task as done
                if(!dbManualKill
                        && (Integer.parseInt(alarmDay) != currentCal.get(Calendar.DAY_OF_MONTH))){
                    currentCal = Calendar.getInstance();
                    Calendar futureCal = Calendar.getInstance();
                    futureCal.setTimeInMillis((futureStamp * 1000) - (AlarmManager.INTERVAL_DAY * 7));
                    diff = (Long.parseLong(String.valueOf(futureStamp) + "000")
                            - (AlarmManager.INTERVAL_DAY * 7)) - currentCal.getTimeInMillis();
                    diff = diff / 1000;
                    if(diff > 0) {
                        int daysOut = (int) (diff / (86400 * 7));
                        futureStamp =  futureStamp - ((86400 * 7) * (daysOut + 1));
                        alarmCalendar.setTimeInMillis(Long.parseLong
                                (String.valueOf(futureStamp) + "000")
                                - (AlarmManager.INTERVAL_DAY * 7));
                    }

                    Log.i(TAG, "newDay: " + alarmCalendar.get(Calendar.DAY_OF_MONTH));

                    //updating due date in database
                    MainActivity.db.updateAlarmData(String.valueOf(broadId),
                            String.valueOf(alarmCalendar.get(Calendar.HOUR)),
                            String.valueOf(alarmCalendar.get(Calendar.MINUTE)),
                            String.valueOf(alarmCalendar.get(Calendar.AM_PM)),
                            String.valueOf(alarmCalendar.get(Calendar.DAY_OF_MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.YEAR)));

                }

                MainActivity.db.updateManualKill(String.valueOf(broadId), false);

            }else if(dbRepeatInterval.equals("month") && !snoozeStatus){

                //Getting interval in seconds based on specific day and month
                int interval = 0;
                int theYear = Integer.parseInt(alarmYear);
//                int theMonth = Integer.parseInt(alarmMonth);
                int theMonth = Calendar.getInstance().get(Calendar.MONTH);

                Log.i(TAG, "theMonth: " + theMonth);

                int theDay = Integer.parseInt(alarmDay);
                //Month January and day is 29 non leap year 2592000
                if((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)){
                    interval = 2592000;
                    //Month January and day is 30 non leap year 2505600
                }else if((theMonth == 0) && (theDay == 30) && (theYear % 4 != 0)){
                    interval = 2505600;
                    //Month January and day is 31 non leap year 2419200
                }else if((theMonth == 0) && (theDay == 31) && (theYear % 4 != 0)){
                    interval = 2419200;
                    //Month January and day is 30 leap year 2592000
                }else if((theMonth == 0) && (theDay == 30)  && (theYear % 4 == 0)){
                    interval = 2592000;
                    //Month January and day is 31 leap year 2505600
                }else if((theMonth == 0) && (theDay == 31) && (theYear % 4 == 0)){
                    interval = 2505600;
                    //Month March||May||August||October and day is 31 2592000
                }else if(((theMonth == 2) || (theMonth == 4) || (theMonth == 7)
                        || (theMonth == 9)) && (theDay == 31)){
                    interval = 2592000;
                    //Month January||March||May||July||August||October||December 2678400
                }else if((theMonth == 0) || (theMonth == 2) || (theMonth == 4)
                        || (theMonth == 6) || (theMonth == 7) || (theMonth == 9)
                        || (theMonth == 11)){
                    interval = 2678400;
                    //Month April||June||September||November 2592000
                }else if((theMonth == 3) || (theMonth == 5) || (theMonth == 8)
                        || (theMonth == 10)){
                    interval = 2592000;
                    //Month February non leap year 2419200
                }else if((theMonth == 1) && (theYear % 4 != 0)){
                    interval = 2419200;
                    //Month February leap year 2505600
                }else if((theMonth == 1) && (theYear % 4 == 0)){
                    interval = 2505600;
                }

                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the millisecond level
                long futureStamp = (Long.parseLong(dbTimestamp) + interval);
                String tempTimestamp = "";
                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                    Cursor tempResult = MainActivity.db.getData(i);
                    while (tempResult.moveToNext()) {
                        tempTimestamp = tempResult.getString(3);
                    }
                    tempResult.close();
                    if(futureStamp == Long.parseLong(tempTimestamp)){
                        futureStamp++;
                        i = 0;
                    }

                }

                futureStamp = Long.parseLong(String.valueOf(futureStamp) + "000");
                Cursor origResult = MainActivity.db.getData(broadId);
                String originalDay = "";
                while (origResult.moveToNext()) {
                    originalDay = origResult.getString(20);
                }
                origResult.close();
                int daysOut = 0;

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(futureStamp);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                if(day != Integer.parseInt(originalDay)){
                    if(month == 0 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 2 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 3 && (day == 28 || day == 29)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 4 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 5 && (day == 28 || day == 29)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 6 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 7 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 8 && (day == 28 || day == 29)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 9 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 10 && (day == 28 || day == 29)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 11 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }
                }

                futureStamp = futureStamp / 1000;

                String oldStamp = dbTimestamp + "000";

                //updating timestamp
                MainActivity.db.updateTimestamp(String.valueOf(broadId),
                        String.valueOf(futureStamp));

//                Calendar tempCal = Calendar.getInstance();
//                tempCal.setTimeInMillis(futureStamp * 1000);

                //setting the name of the task for which the
                // notification is being set
                MainActivity.alertIntent.putExtra("ToDo", dbTask);
                MainActivity.alertIntent.putExtra("broadId", broadId);

                //Setting alarm
                MainActivity.pendIntent = PendingIntent.getBroadcast(
                        context, broadId, MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

//                MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong
//                                (String.valueOf(futureStamp) + "000"), MainActivity.pendIntent);

                Calendar alarmCalendar = Calendar.getInstance();
//                alarmCalendar.setTimeInMillis(Long.parseLong(oldStamp));
                Long diff = Long.valueOf(0);

                if(!dbKilledEarly) {
                    Calendar currentCal = Calendar.getInstance();
                    Calendar futureCal = Calendar.getInstance();
                    futureCal.setTimeInMillis(futureStamp * 1000);
                    diff = futureCal.getTimeInMillis() - currentCal.getTimeInMillis();
                    diff = diff / 1000;
                    if(diff < (interval + ((AlarmManager.INTERVAL_DAY * daysOut) / 1000))) {
                        Log.i(TAG, "day: " + futureCal.get(Calendar.DAY_OF_MONTH));
                        futureCal.setTimeInMillis(Long.parseLong
                                (String.valueOf(futureStamp) + "000"));
                        Log.i(TAG, "day: " + futureCal.get(Calendar.DAY_OF_MONTH));
                        MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong
                                (String.valueOf(futureStamp) + "000"), MainActivity.pendIntent);
                    }else{
                        int daysWrong = (int) (diff / (interval + ((AlarmManager.INTERVAL_DAY * daysOut) / 1000)));
                        MainActivity.alarmManager.set(AlarmManager.RTC, (Long.parseLong
                                        (String.valueOf(futureStamp) + "000") - (((interval + (AlarmManager.INTERVAL_DAY * daysOut)) * daysWrong))),
                                MainActivity.pendIntent);
                    }
                }else{
                    MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong
                            (String.valueOf(dbTimestamp) + "000"), MainActivity.pendIntent);
                }

                Calendar currentCal = Calendar.getInstance();

                Long futureStampForCalculation = Long.parseLong(String.valueOf(futureStamp) + "000");
                Long intervalForCalculation = Long.parseLong(String.valueOf(interval)) * 1000;
                Long dayDriftForCalculation = AlarmManager.INTERVAL_DAY * daysOut;

                alarmCalendar.setTimeInMillis(futureStampForCalculation
                        - (intervalForCalculation + dayDriftForCalculation));

//                alarmCalendar.setTimeInMillis(Long.parseLong(String.valueOf(futureStamp)
//                        + "000")/* - (interval + (AlarmManager.INTERVAL_DAY * daysOut))*/);

                //alarm data is already updated if user marked task as done
                if(!dbManualKill && (Integer.parseInt(alarmMonth) != currentCal.get(Calendar.MONTH))
                    /*(Integer.parseInt(alarmDay) != currentCal.get(Calendar.DAY_OF_MONTH))*/){
//                    currentCal = Calendar.getInstance();
//                    Calendar futureCal = Calendar.getInstance();
//                    futureCal.setTimeInMillis((futureStamp * 1000)/* - (interval + (AlarmManager.INTERVAL_DAY * daysOut))*/);
//                    futureStampForCalculation = Long.parseLong(String.valueOf(futureStamp) + "000");
//                    intervalForCalculation = Long.parseLong(String.valueOf(interval)) * 1000;
//                    dayDriftForCalculation = AlarmManager.INTERVAL_DAY * daysOut;
                    Long currentMillisForCalculation = currentCal.getTimeInMillis();
//                    diff = (Long.parseLong(String.valueOf(futureStamp) + "000")
//                            - ((interval * 1000) + (AlarmManager.INTERVAL_DAY * daysOut))
//                            - currentCal.getTimeInMillis());
                    diff = futureStampForCalculation - (intervalForCalculation
                            + dayDriftForCalculation) - currentMillisForCalculation;
                    diff = diff / 1000;

                    if(diff > 0) {
                        int daysWrong = (int) (diff / (interval + ((AlarmManager.INTERVAL_DAY / 1000) * daysOut)));
                        futureStamp = futureStamp - ((interval + ((AlarmManager.INTERVAL_DAY / 1000) * daysOut) * (daysWrong + 1)));
                        alarmCalendar.setTimeInMillis(Long.parseLong
                                (String.valueOf(futureStamp) + "000")
                                - (interval + (AlarmManager.INTERVAL_DAY * daysOut)));
                    }

                    Log.i(TAG, "Hour.: " + alarmCalendar.get(Calendar.HOUR));
                    Log.i(TAG, "Minute.: " + alarmCalendar.get(Calendar.MINUTE));
                    Log.i(TAG, "Am/Pm.: " + alarmCalendar.get(Calendar.AM_PM));
                    Log.i(TAG, "Day.: " + alarmCalendar.get(Calendar.DAY_OF_MONTH));
                    Log.i(TAG, "Month.: " + alarmCalendar.get(Calendar.MONTH));
                    Log.i(TAG, "Year.: " + alarmCalendar.get(Calendar.YEAR));

                    //updating due date in database
                    MainActivity.db.updateAlarmData(String.valueOf(broadId),
                            String.valueOf(alarmCalendar.get(Calendar.HOUR)),
                            String.valueOf(alarmCalendar.get(Calendar.MINUTE)),
                            String.valueOf(alarmCalendar.get(Calendar.AM_PM)),
                            String.valueOf(alarmCalendar.get(Calendar.DAY_OF_MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.YEAR)));

                }

                MainActivity.db.updateManualKill(String.valueOf(broadId), false);

            }
        }
    }
}
