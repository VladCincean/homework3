package com.example.vlad.caloriecounter.fcm;

import android.content.Intent;
import android.util.Log;

import com.example.vlad.caloriecounter.LoginMainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message Data Payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        notifyUser(
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody()
        );
    }

    public void notifyUser(String from, String notification) {
        MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
        myNotificationManager.showNotification(
                from,
                notification,
                new Intent(getApplicationContext(), LoginMainActivity.class)
        );
    }
}
