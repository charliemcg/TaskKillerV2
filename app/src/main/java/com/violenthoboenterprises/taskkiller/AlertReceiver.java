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


        //allows for notifications
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);


//        Calendar notifCal = new GregorianCalendar();
//        int hour = notifCal.get(Calendar.HOUR_OF_DAY);
//        int minute = notifCal.get(Calendar.MINUTE);

        if(MainActivity.lightDark) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_light);
            remoteViews.setTextViewText(R.id.notif_title, msg);
        }else{
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
            remoteViews.setTextViewText(R.id.notif_title, msg);
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
                .setContentText(msg).setStyle(new NotificationCompat.BigTextStyle());

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
        notificationManager.notify(1, builder.build());

        //getting task data
        Boolean dbRepeat = false;
        String dbRepeatInterval = "";
        Cursor dbResult = MainActivity.db.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(broadId)));
        while (dbResult.moveToNext()) {
            dbRepeat = dbResult.getInt(8) > 0;
            dbRepeatInterval = dbResult.getString(13);
        }
        dbResult.close();

        if(dbRepeat) {

            if(dbRepeatInterval.equals("day")){
                Log.i(TAG, "Create next alarm here with id: " + broadId);

                //setting the name of the task for which the
                // notification is being set
                MainActivity.alertIntent.putExtra("ToDo", msg);
                MainActivity.alertIntent.putExtra("broadId", broadId);

                //Setting alarm
                MainActivity.pendIntent = PendingIntent.getBroadcast(
                        context, broadId, MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar dateNow = Calendar.getInstance();
                MainActivity.alarmManager.set(AlarmManager.RTC, (dateNow
                                .getTimeInMillis() + AlarmManager.INTERVAL_DAY),
                        MainActivity.pendIntent);

            }else if(dbRepeatInterval.equals("week")){

            }else if(dbRepeatInterval.equals("month")){

            }
        }

    }

}
