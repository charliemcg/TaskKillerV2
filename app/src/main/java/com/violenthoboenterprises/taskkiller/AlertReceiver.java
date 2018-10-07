package com.violenthoboenterprises.taskkiller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

    String TAG;

    @Override
    public void onReceive(Context context, Intent intent) {

        //retrieving task name to set as notification name
        createNotification(context, String.valueOf(intent.getStringExtra("ToDo")), "", "");

    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert){

        //defining intent and action to perform
        PendingIntent notificIntent = PendingIntent.getActivity(context, 1,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder builder;
        RemoteViews remoteViews;

//        Calendar notifCal = new GregorianCalendar();
//        int hour = notifCal.get(Calendar.HOUR_OF_DAY);
//        int minute = notifCal.get(Calendar.MINUTE);

        if(MainActivity.lightDark) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_light);
            remoteViews.setTextViewText(R.id.notif_title, msg);
        }else{
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
            remoteViews.setTextViewText(R.id.notif_title, msg);
//            remoteViews.setTextViewText(R.id.notific_time, String.valueOf(notifCal.getTime()));
        }

        //intent to execute when notification is clicked
        //TODO see if possible to make light and dark version
        //TODO set up a way of showing how many tasks are overdue
        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.small_notific_icon).setLargeIcon(BitmapFactory
                        .decodeResource(context.getResources(), R.drawable.ic_launcher_og))
                .setContentTitle(context.getString(R.string.killThisTask)).setTicker(msgAlert)
                .setContentText(msg).setStyle(new NotificationCompat.BigTextStyle());

        //Sets background of small icon
        builder.setColorized(true).setColor(Color.parseColor("#FFFF0000"));

        builder.setCustomContentView(remoteViews);

        builder.setLights(/*val*/Integer.parseInt(MainActivity.highlightDec), 500, 500);

        //use phone's default notification sound
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        //ensure app is opened when notification is clicked
        builder.setContentIntent(notificIntent);

        //cancels the notification when clicked
        builder.setAutoCancel(true);

        //allows for notifications
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //post notification
        notificationManager.notify(1, builder.build());

    }

}
