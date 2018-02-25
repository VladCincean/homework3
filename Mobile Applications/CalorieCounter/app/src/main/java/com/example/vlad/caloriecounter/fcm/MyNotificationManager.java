package com.example.vlad.caloriecounter.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.vlad.caloriecounter.R;

public class MyNotificationManager {
    private static final String TAG = MyNotificationManager.class.getName();
    private static final int NOTIFICATION_ID = 42;

    private Context context;

    public MyNotificationManager(Context context) {
        this.context = context;
    }

    public void showNotification(String from, String notification, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification mNotification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(from)
                .setContentText(notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .build();

        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        Log.w(TAG, mNotification.toString());
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIFICATION_ID, mNotification);
    }
}
