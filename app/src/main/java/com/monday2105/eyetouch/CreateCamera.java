package com.monday2105.eyetouch;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

public class CreateCamera extends Service {

    static CameraSource cameraSource;
    static String TAG = "CreateCamera";
    FaceDetector detector;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createCameraSource();
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            EyesTracker.unregisterTracker();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createCameraSource() {
        detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerFactory(this)).build());

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(60.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity)getApplicationContext(), new String[]{Manifest.permission.CAMERA}, 1);
                Toast.makeText(this, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
                return;
            }
            //cameraSource.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int cameraSwitch(boolean camera){
        if(camera) {
            try {
                cameraSource.start();
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        else{
            cameraSource.stop();
            return 1;
        }
    }

}
