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

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AlertReceiver extends BroadcastReceiver {

    String TAG = "AlertReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        //retrieving task name to set as notification name
        createNotification(context, String.valueOf(intent.getStringExtra("ToDo")), "", "",
                intent.getIntExtra("broadId", 0));

    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert, int broadId){

        //defining intent and action to perform
        PendingIntent notificIntent = PendingIntent.getActivity(context, 1,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder builder;
        RemoteViews remoteViews;

        //getting task data
        String dbTimestamp = "";
        String dbTask = "";
        Boolean dbRepeat = false;
        Boolean dbSnoozed = false;
        String dbRepeatInterval = "";
        Boolean dbManualKill = false;
        Boolean dbKilledEarly = false;
        Cursor dbResult = MainActivity.db.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(broadId)));
        while (dbResult.moveToNext()) {
            dbTimestamp = dbResult.getString(3);
            dbTask = dbResult.getString(4);
            dbRepeat = dbResult.getInt(8) > 0;
            dbSnoozed = dbResult.getInt(10) > 0;
            dbRepeatInterval = dbResult.getString(13);
            dbManualKill = dbResult.getInt(18) > 0;
            dbKilledEarly = dbResult.getInt(19) > 0;
        }
        dbResult.close();

        //allows for notifications
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);


//        Calendar notifCal = new GregorianCalendar();
//        int hour = notifCal.get(Calendar.HOUR_OF_DAY);
//        int minute = notifCal.get(Calendar.MINUTE);

        if(MainActivity.lightDark) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_light);
            remoteViews.setTextViewText(R.id.notif_title, /*msg*/dbTask);
        }else{
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
            remoteViews.setTextViewText(R.id.notif_title, /*msg*/dbTask);
        }

        final int NOTIFICATION_ID = 1;
        final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Integer.parseInt(MainActivity.highlightDec));
//            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //intent to execute when notification is clicked
        //TODO see if possible to make light and dark version
        //TODO set up a way of showing how many tasks are overdue
        builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.small_notific_icon).setLargeIcon(BitmapFactory
                        .decodeResource(context.getResources(), R.drawable.ic_launcher_og))
                .setContentTitle(context.getString(R.string.killThisTask)).setTicker(msgAlert)
                .setContentText(/*msg*/dbTask).setStyle(new NotificationCompat.BigTextStyle());

        //Sets background of small icon
//        builder.setColorized(true).setColor(Color.parseColor("#FFFF0000"));
        builder.setColorized(true).setColor(Color.parseColor(MainActivity.highlight));
        builder.setCustomContentView(remoteViews);
        builder.setLights(/*val*/Integer.parseInt(MainActivity.highlightDec), 500, 500);
        //use phone's default notification sound
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        //ensure app is opened when notification is clicked
        builder.setContentIntent(notificIntent);
        //cancels the notification when clicked
        builder.setAutoCancel(true);

        //post notification
//        notificationManager.notify(1, builder.build());

        if(!dbRepeat){
            notificationManager.notify(1, builder.build());
        } else {

            if(!dbKilledEarly){
                notificationManager.notify(1, builder.build());
            }else{
                MainActivity.db.updateKilledEarly(String.valueOf(MainActivity.sortedIDs.get(broadId)), false);
            }

            if(dbRepeatInterval.equals("day") && !dbSnoozed){

                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the millisecond level
//                long futureStamp = dateNow.getTimeInMillis() + AlarmManager.INTERVAL_DAY;
                long futureStamp = Long.parseLong(dbTimestamp) + (AlarmManager.INTERVAL_DAY / 1000);
                String tempTimestamp = "";
                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                    Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                            MainActivity.sortedIDs.get(i)));
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
                MainActivity.db.updateTimestamp(String.valueOf(
                        MainActivity.sortedIDs.get(broadId)),
                        String.valueOf(futureStamp));

                //setting the name of the task for which the
                // notification is being set
                MainActivity.alertIntent.putExtra("ToDo", /*msg*/dbTask);
                MainActivity.alertIntent.putExtra("broadId", broadId);

                //Setting alarm
                MainActivity.pendIntent = PendingIntent.getBroadcast(
                        context, broadId, MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong(String.valueOf(futureStamp) + "000"),
                        MainActivity.pendIntent);

                //getting alarm data
                Cursor alarmResult = MainActivity.db.getAlarmData(
                        Integer.parseInt(MainActivity.sortedIDs.get(broadId)));
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

//                Log.i(TAG, "Alarm Data\nyear: " + alarmYear + " month: " + alarmMonth + " day: " + alarmDay + " hour: " + alarmHour + " minute: " + alarmMinute + " ampm: " + alarmAmpm);

                Calendar alarmCalendar = Calendar.getInstance();
                alarmCalendar.setTimeInMillis(Long.parseLong(String.valueOf(futureStamp) + "000") - AlarmManager.INTERVAL_DAY);

                if(!dbManualKill){

                    //updating due date in database
                    MainActivity.db.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(broadId)),
                            String.valueOf(alarmCalendar.get(Calendar.HOUR)),
                            String.valueOf(alarmCalendar.get(Calendar.MINUTE)),
                            String.valueOf(alarmCalendar.get(Calendar.AM_PM)),
                            String.valueOf(alarmCalendar.get(Calendar.DAY_OF_MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.YEAR)));

                }

                MainActivity.db.updateManualKill(String.valueOf(MainActivity.sortedIDs.get(broadId)), false);

            }else if(dbRepeatInterval.equals("week") && !dbSnoozed){

                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the millisecond level
                long futureStamp = Long.parseLong(dbTimestamp) + ((AlarmManager.INTERVAL_DAY * 7) / 1000);
                String tempTimestamp = "";
                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                    Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                            MainActivity.sortedIDs.get(i)));
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
                MainActivity.db.updateTimestamp(String.valueOf(
                        MainActivity.sortedIDs.get(broadId)),
                        String.valueOf(futureStamp));

                //setting the name of the task for which the
                // notification is being set
                MainActivity.alertIntent.putExtra("ToDo", /*msg*/dbTask);
                MainActivity.alertIntent.putExtra("broadId", broadId);

                //Setting alarm
                MainActivity.pendIntent = PendingIntent.getBroadcast(
                        context, broadId, MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong(String.valueOf(futureStamp) + "000"),
                        MainActivity.pendIntent);

                Calendar alarmCalendar = Calendar.getInstance();
                alarmCalendar.setTimeInMillis(Long.parseLong(String.valueOf(futureStamp) + "000") - (AlarmManager.INTERVAL_DAY * 7));

                if(!dbManualKill){

                    //updating due date in database
                    MainActivity.db.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(broadId)),
                            String.valueOf(alarmCalendar.get(Calendar.HOUR)),
                            String.valueOf(alarmCalendar.get(Calendar.MINUTE)),
                            String.valueOf(alarmCalendar.get(Calendar.AM_PM)),
                            String.valueOf(alarmCalendar.get(Calendar.DAY_OF_MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.YEAR)));

                }

                MainActivity.db.updateManualKill(String.valueOf(MainActivity.sortedIDs.get(broadId)), false);

            }else if(dbRepeatInterval.equals("month")){

                //getting alarm data
                Cursor alarmResult = MainActivity.db.getAlarmData(
                        Integer.parseInt(MainActivity.sortedIDs.get(broadId)));
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

                Calendar currentCal = Calendar.getInstance();
                int currentYear = currentCal.get(Calendar.YEAR);
                int currentMonth = currentCal.get(Calendar.MONTH);
                int currentDay = currentCal.get(Calendar.DAY_OF_MONTH);

                //Getting interval in seconds based on specific day and month//TODO double check that alarm data is always previous non snoozed due
                int interval = 0;
//                int theYear = Integer.parseInt(alarmYear);
//                int theMonth = Integer.parseInt(alarmMonth);
//                int theDay = Integer.parseInt(alarmDay);
                int theYear = currentYear;
                int theMonth = currentMonth;
                int theDay = currentDay;
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
                long futureStamp = Long.parseLong(dbTimestamp) + interval;
//                long futureStamp = Long.parseLong(String.valueOf(dbTimestamp) + "000") + Long.parseLong(String.valueOf(interval) + "000");

                String tempTimestamp = "";
//                futureStamp = futureStamp / 1000;
                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                    Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                            MainActivity.sortedIDs.get(i)));
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
                Cursor origResult = MainActivity.db.getData(Integer.parseInt(
                        MainActivity.sortedIDs.get(broadId)));
                String originalDay = "";
                while (origResult.moveToNext()) {
                    //tempTimestamp = tempResult.getString(3);
                    originalDay = origResult.getString(20);
                }
                origResult.close();

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(futureStamp);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                if(day != Integer.parseInt(originalDay)){
                    int daysOut = 0;
                    if(month == 0 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 2 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 3 && (day == 28 || day == 29/* || day == 30*/)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 4 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 5 && (day == 28 || day == 29/* || day == 30*/)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 6 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 7 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 8 && (day == 28 || day == 29/* || day == 30*/)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 9 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 10 && (day == 28 || day == 29/* || day == 30*/)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }else if(month == 11 && (day == 28 || day == 29 || day == 30)){
                        daysOut = Integer.parseInt(originalDay) - day;
                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                    }
                    ///////////////////////
                    cal.setTimeInMillis(futureStamp);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                    month = cal.get(Calendar.MONTH);
                    Log.i(TAG, "Timestamp: " + futureStamp + " Day: " + day + " Month: " + month + " Original: " + originalDay);
                }

                futureStamp = futureStamp / 1000;

                //updating timestamp
                MainActivity.db.updateTimestamp(String.valueOf(
                        MainActivity.sortedIDs.get(broadId)),
                        String.valueOf(futureStamp));

                //setting the name of the task for which the
                // notification is being set
                MainActivity.alertIntent.putExtra("ToDo", /*msg*/dbTask);
                MainActivity.alertIntent.putExtra("broadId", broadId);

                //Setting alarm
                MainActivity.pendIntent = PendingIntent.getBroadcast(
                        context, broadId, MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                MainActivity.alarmManager.set(AlarmManager.RTC, Long.parseLong(String.valueOf(futureStamp) + "000"),
                        MainActivity.pendIntent);

                Calendar alarmCalendar = Calendar.getInstance();
                alarmCalendar.setTimeInMillis(Long.parseLong(String.valueOf(futureStamp) + "000") - Long.parseLong(String.valueOf(interval) + "000"));

                if(!dbManualKill){

                    //updating due date in database
                    MainActivity.db.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(broadId)),
                            String.valueOf(alarmCalendar.get(Calendar.HOUR)),
                            String.valueOf(alarmCalendar.get(Calendar.MINUTE)),
                            String.valueOf(alarmCalendar.get(Calendar.AM_PM)),
                            String.valueOf(alarmCalendar.get(Calendar.DAY_OF_MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.MONTH)),
                            String.valueOf(alarmCalendar.get(Calendar.YEAR)));

                }

                MainActivity.db.updateManualKill(String.valueOf(MainActivity.sortedIDs.get(broadId)), false);

            }
        }

    }

}
