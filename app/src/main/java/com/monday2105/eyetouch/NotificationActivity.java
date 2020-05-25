package com.monday2105.eyetouch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

class NotificationActivity {

    private String CHANNEL_ID;
    private static Context context;
    private String title;
    private String text;
    private String channel_description;
    private static int notificationId;

    NotificationActivity(Context context, String CHANNEL_ID, String title, String text,
                         String channel_description, int notificationId){
        this.context = context;
        this.CHANNEL_ID = CHANNEL_ID;
        this.title = title;
        this.text = text;
        this.channel_description = channel_description;
        this.notificationId = notificationId;

    }

    NotificationCompat.Builder createNotification(boolean onGoing) {

        Intent exit = new Intent(context, CameraControlService.class);
        exit.putExtra("Exit", true);
        exit.setAction("EXIT");
        PendingIntent exitPendingIntent =
                PendingIntent.getService(context, 0, exit, 0);

         NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_stat_name))
                 .setColor(context.getResources().getColor(R.color.colorPrimary))
                 .setContentTitle(title)
                 .setContentText(text)
                 .setOngoing(onGoing)
                 .setOnlyAlertOnce(onGoing)
                 .addAction(R.drawable.ic_stat_name,"Exit", exitPendingIntent)
                 .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());

        return builder;
    }

    void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = channel_description;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    NotificationCompat.Builder setIntent(NotificationCompat.Builder builder){
        Intent myIntent = new Intent(context, CameraControlService.class);
        myIntent.putExtra("fromNotification", true);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, Intent.FILL_IN_ACTION);
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());

        return  builder;
    }

    static NotificationCompat.Builder updateNotification(NotificationCompat.Builder builder, boolean camera){

        if(camera) {
            builder.setContentText("On");
            int res = CreateCamera.cameraSwitch(camera);
            if(res==0){
                MainActivity.failedCamera();
                builder.setContentText("Off");
            }
        }
        else{
            builder.setContentText("Off");
            CreateCamera.cameraSwitch(camera);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());

        return builder;
    }
}
