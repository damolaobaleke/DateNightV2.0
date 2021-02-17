/*
 * Copyright 2021 Damola Obaleke. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.immersia_ltd_datenight.utils.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.RemoteMessage;
import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.utils.constants.DatabaseConstants;
import com.immersia_ltd_datenight.views.authentication.SignUpActivity;

import org.jetbrains.annotations.NotNull;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    final private static String TAG = "FirebaseMessagingService";
    DocumentReference userDocRef;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public FirebaseMessagingService() {
        //Initialize
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    public void onMessageReceived(@NotNull RemoteMessage message) {
        super.onMessageReceived(message);
        //Here we can handle the pushed notification
        //message will have the content that is pushed by the firebase

        if (message.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData());
        }

        if (message.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + message.getNotification().getBody());
            sendNotification(message);
        }

    }

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup when they SIGNUP in our case
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */

    @Override
    public void onNewToken(@NotNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        userDocRef = db.collection(DatabaseConstants.USER_DATA_NODE).document(mAuth.getCurrentUser().getUid());
        userDocRef.update("fcmToken", token);

    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message body received.
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT);

        //We're arent really using channels right now
        String channelId = "channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.datehub_logo)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel chats", NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 , notificationBuilder.build());
    }

}
