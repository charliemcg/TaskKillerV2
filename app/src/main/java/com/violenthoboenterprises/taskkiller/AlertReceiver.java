package com.violenthoboenterprises.taskkiller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlertReceiver extends BroadcastReceiver {

    String TAG;

    @Override
    public void onReceive(Context context, Intent intent) {

        //retrieving task name to set as notification name
        createNotification(context, String.valueOf(intent.getStringExtra("ToDo")),
                "This is message text", "This is a message alert");

    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert){

        //defining intent and action to perform
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        //intent to execute when notification is clicked
        //TODO find out what's going on with setSmallIcon()
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.bell).setLargeIcon(BitmapFactory.decodeResource(context
                .getResources(), R.drawable.bell)).setContentTitle(msg).setTicker(msgAlert)
                .setContentText(msgText);

        //use phone's default notification sound
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        //ensure app is opened when notification is clicked
        mBuilder.setContentIntent(notificIntent);

        //cancels the notification when clicked
        mBuilder.setAutoCancel(true);

        //allows for notifications
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //post notification
        notificationManager.notify(1, mBuilder.build());

    }

}
