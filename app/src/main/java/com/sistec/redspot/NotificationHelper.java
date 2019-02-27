package com.sistec.redspot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationHelper {

    private static final String MY_CHANNEL_ID = "redSpotDangerWarning";
    private static final String MY_CHANNEL_NAME = "RedSpot Alert";
    private static final String MY_CHANNEL_DESC = "Give alert when you are in accident prone area";
    private static final int MY_NOTIFICATION_ID = 111;
    private static NotificationManagerCompat notificationManagerCompat;
    private static NotificationCompat.Builder mBuilder;

    public static void showNewNotification(Context context, String title, String desc, String extraDesc){
        NotificationManagerCompat notificationManagerCompat;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MY_CHANNEL_ID);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 ,intent, 0);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setColor(context.getResources().getColor(R.color.danger_highest));
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(desc);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(extraDesc + "\nClick this notification to know all danger area information."));
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        notificationManagerCompat = NotificationManagerCompat.from(context);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(MY_CHANNEL_ID, MY_CHANNEL_NAME, importance);
            channel.setDescription(MY_CHANNEL_DESC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManagerCompat.notify(MY_NOTIFICATION_ID, mBuilder.build());
    }
    public static void hideOldNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

}