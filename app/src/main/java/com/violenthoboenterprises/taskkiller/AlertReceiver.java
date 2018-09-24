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

        if(MainActivity.lightDark) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_light);
            remoteViews.setTextViewText(R.id.notif_title, msg);
        }else{
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
//            remoteViews.setImageViewResource(R.id.notif_icon, R.mipmap.ic_launcher);
            remoteViews.setTextViewText(R.id.notif_title, msg);
        }

        //intent to execute when notification is clicked
        //TODO see if possible to make light and dark version
        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.small_notific_icon).setLargeIcon(BitmapFactory
                        .decodeResource(context.getResources(), R.drawable.mushroom_cloud))
                .setContentTitle(context.getString(R.string.killThisTask)).setTicker(msgAlert).setContentText(msg/*Text*/);

        //Sets background of small icon
        builder.setColorized(true).setColor(Color.parseColor("#FF000000"));

        builder.setCustomContentView(remoteViews);

        String digits = "0123456789ABCDEF";
        int val = 0;
        for (int i = 1; i < MainActivity.highlight.length(); i++) {
            char c = MainActivity.highlight.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }

        builder.setLights(/*0xFF00FF00*/val, 500, 500);

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
