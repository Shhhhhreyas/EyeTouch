package com.monday2105.eyetouch;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class CameraControlService extends Service {

    String TAG = "CameraControlService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) return START_NOT_STICKY;
        if (intent.hasExtra("fromNotification")) {
            MainActivity.switchCamera();
            MainActivity.updateNotif();
        }
        if(intent.hasExtra("Exit")){
            try {
                unregisterReceiver(MainActivity.mReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                EyesTracker.unregisterTracker();
            }catch (Exception e){
                e.printStackTrace();
            }
            MainActivity.switchCamera(true);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
            stopService(new Intent(this, CreateCamera.class));
            stopService(new Intent(this, CameraControlService.class));
            Log.i(TAG, "onDestroy: DoneAll");
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
