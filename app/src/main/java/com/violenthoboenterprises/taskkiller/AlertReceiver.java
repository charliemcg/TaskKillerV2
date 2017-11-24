package com.violenthoboenterprises.taskkiller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context, "Times Up", "5 Seconds Has Passed", "Alert");

    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert){

        // Define an Intent and an action to perform with it by another application
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        // Defines the Intent to fire when the notification is clicked
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.bell).setContentTitle(msg).setTicker(msgAlert).setContentText(msgText);

        // Set the default notification option
        // DEFAULT_SOUND : Make sound
        // DEFAULT_VIBRATE : Vibrate
        // DEFAULT_LIGHTS : Use the default light notification
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        // Auto cancels the notification when clicked on in the task bar
        mBuilder.setAutoCancel(true);

        // Gets a NotificationManager which is used to notify the user of the background event
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Post the notification
        mNotificationManager.notify(1, mBuilder.build());

    }

}
