package com.monday2105.eyetouch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

public class EyesTracker extends Tracker<Face> {

    //private String TAG = "EyesTracker";
    float THRESHOLD = 0.7f;
    private static Context context;
    static BroadcastReceiver mReceiver;

    EyesTracker(Context context) {
       this.context = context;
        IntentFilter filter = new IntentFilter("com.monday2105.eyetouch.tracker");
        mReceiver = new trackerReceiver();
        context.registerReceiver(mReceiver, filter);
    }



    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {

        float leftEye = face.getIsLeftEyeOpenProbability();
        float rightEye = face.getIsRightEyeOpenProbability();

        EyeFunctions.whichEye(leftEye,rightEye,THRESHOLD,context);

    }

    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        super.onMissing(detections);
        Intent i = new Intent("com.monday2105.eyetouch.update");
        i.putExtra("eye","5");
        context.sendBroadcast(i);
        //Log.i(TAG, "onUpdate: faceNotFound broadastSent");
    }

    @Override
    public void onDone() {
        super.onDone();
    }

    public  class trackerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("threshold",70);
            THRESHOLD = progress/100f;
            //Log.i(TAG, "onReceiveTracker: Threshold: "+THRESHOLD);
        }

    }

    public static void unregisterTracker(){
        context.unregisterReceiver(mReceiver);
    }

}
