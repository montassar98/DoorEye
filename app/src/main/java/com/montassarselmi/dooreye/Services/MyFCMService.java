package com.montassarselmi.dooreye.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.montassarselmi.dooreye.CallingActivity;
import com.montassarselmi.dooreye.CheckFrontDoorActivity;
import com.montassarselmi.dooreye.ConfigureActivity;
import com.montassarselmi.dooreye.ContactUsActivity;
import com.montassarselmi.dooreye.EditActivity;
import com.montassarselmi.dooreye.EventHistoryActivity;
import com.montassarselmi.dooreye.FamilyActivity;
import com.montassarselmi.dooreye.MainActivity;
import com.montassarselmi.dooreye.R;
import com.montassarselmi.dooreye.RegistrationActivity;
import com.montassarselmi.dooreye.SplashActivity;
import com.montassarselmi.dooreye.VideoChatActivity;
import com.montassarselmi.dooreye.WaitingActivity;

public class MyFCMService extends FirebaseMessagingService {

    private static final String TAG = "MyFCMService";

    private final String CHANNEL_ID = "123";
    private final int NOTIFICATION_ID = 321;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " +remoteMessage.toString());
        if (remoteMessage.getNotification() != null){
            Log.d(TAG, "notification title: " +remoteMessage.getNotification().getTitle());
            Log.d(TAG, "notification body: " +remoteMessage.getNotification().getBody());
            Log.d(TAG, "channel id: " +remoteMessage.getNotification().getChannelId());
            sendNotification(remoteMessage.getNotification().getBody());
            sendChatMessageNotification("title","body");


            if (!remoteMessage.getNotification().getBody().equals("null")){
                Log.d(TAG, "user receive a call. ");
                //TODO code for receiving a call
                basicNotification("Call From Home",remoteMessage.getNotification().getBody());
                sendBroadcastNotification("Call From Home",remoteMessage.getNotification().getBody());
                sendNotification(remoteMessage.getNotification().getBody());
                sendChatMessageNotification("title","body");

            }else {
                Log.d(TAG, "the call ended.");
                //TODO code for receiving a call

            }
        }
        if (remoteMessage.getData().size() > 0){
            Log.d(TAG, "Message Data payload: "+ remoteMessage.getData());
            if (isApplicationInForeground()){
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                sendBroadcastNotification(title, body);
                sendNotification1(title, body);
                sendChatMessageNotification(title,body);

            }
            //status: app in background or closed
            else  if (!isApplicationInForeground()){
                Log.d(TAG, "app in background or closed ");
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                sendBroadcastNotification(title, body);
                sendNotification1(title, body);

                sendChatMessageNotification(title,body);
            }

        }

    }

    private void sendNotification(String messageBody){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Notification", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                1 ,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_call)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setLargeIcon(Bitmap.createBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.logo_call)))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());



    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void basicNotification(String title, String body){
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_call)
                .setContentTitle(title)
                .setContentText(body)
                .addAction(R.drawable.ic_call,"replay",null)
                //.addAction(R.drawable.ic_airplane,"check", null)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Much longer text that cannot fit one line... and so more other stuff."))
                .setLargeIcon(Bitmap.createBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.profile)))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void sendBroadcastNotification(String title, String message){
        Log.d(TAG, "sendBroadcastNotification: building an admin broadcast notification");

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                getString(R.string.channel_name));
        // Creates an Intent for the Activity
        Intent notifyIntent = new Intent(this, MainActivity.class);
        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //add properties to the builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setSmallIcon(R.drawable.ic_call)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.ic_front_door))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setColor(getColor(R.color.black))
                    .setAutoCancel(true);
        }

        builder.setContentIntent(notifyPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private boolean isApplicationInForeground(){
        //check all the activities to see if any of them are running
        boolean isActivityRunning = MainActivity.isActivityRunning
                || CallingActivity.isActivityRunning || CheckFrontDoorActivity.isActivityRunning
                || ConfigureActivity.isActivityRunning || ContactUsActivity.isActivityRunning
                || EditActivity.isActivityRunning || EventHistoryActivity.isActivityRunning
                || WaitingActivity.isActivityRunning|| FamilyActivity.isActivityRunning
                || RegistrationActivity.isActivityRunning|| SplashActivity.isActivityRunning
                || VideoChatActivity.isActivityRunning;
        if(isActivityRunning) {
            Log.d(TAG, "isApplicationInForeground: application is in foreground.");
            return true;
        }
        Log.d(TAG, "isApplicationInForeground: application is in background or closed.");    return false;
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_call)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotification1(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_call)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
    }

    private int buildNotificationId(String id){
        Log.d(TAG, "buildNotificationId: building a notification id.");

        int notificationId = 0;
        for(int i = 0; i < 9; i++){
            notificationId = notificationId + id.charAt(0);
        }
        Log.d(TAG, "buildNotificationId: id: " + id);
        Log.d(TAG, "buildNotificationId: notification id:" + notificationId);
        return notificationId;
    }
    private void sendChatMessageNotification(String title, String message){
        Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");

        //get the notification id
        int notificationId = buildNotificationId("1234");

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                getString(R.string.default_notification_channel_name));
        // Creates an Intent for the Activity
        Intent pendingIntent = new Intent(this, MainActivity.class);
        // Sets the Activity to start in a new, empty task
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //pendingIntent.putExtra(getString(R.string.intent_chatroom), chatroom);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        pendingIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //add properties to the builder
        builder.setSmallIcon(R.drawable.ic_call)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.profile))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContentText("New messages in ")
                //.setColor(getColor(R.color.black))
                .setAutoCancel(true)
                .setSubText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("New messages in "));
//				.setOnlyAlertOnce(true)


        builder.setContentIntent(notifyPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationId, builder.build());

    }




}
