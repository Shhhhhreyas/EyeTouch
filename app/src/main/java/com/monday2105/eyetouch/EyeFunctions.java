package com.monday2105.eyetouch;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import android.os.Handler;

import java.io.IOException;

public class EyeFunctions {

    private static int status = -1;
    private static int blink_ = -1;
    private static int blinkCount = 0;
    private static String TAG = "EyeFunctions";

    static void whichEye(float leftEye, float rightEye, float THRESHOLD, Context context){
        //Log.w(TAG, "BlinkCount: "+blinkCount);
        if (leftEye > THRESHOLD && rightEye <= THRESHOLD){

            if(status==0) return;
            else status=0;
            Log.i(TAG, "whichEye: Right");
            Intent i = new Intent("com.monday2105.eyetouch.update");
            i.putExtra("eye","1");
            context.sendBroadcast(i);
            return;
        }

        else if (leftEye <= THRESHOLD && rightEye > THRESHOLD){
            if(status==1) return;
            else status=1;
            Log.i(TAG, "whichEye: Left");
            Intent i = new Intent("com.monday2105.eyetouch.update");
            i.putExtra("eye","2");
            context.sendBroadcast(i);
            return;

        }

        else if (leftEye > THRESHOLD && rightEye > THRESHOLD){
            if(status==2) return;
            else status=2;
            Log.i(TAG, "whichEye: Both Open");
            Intent i = new Intent("com.monday2105.eyetouch.update");
            i.putExtra("eye","3");
            context.sendBroadcast(i);
            blink(2,context);

            return;
        }

        else if (leftEye <= THRESHOLD && rightEye <= THRESHOLD){
            if(status==3) return;
            else status=3;
            Log.i(TAG, "whichEye: Both closed");
            Intent i = new Intent("com.monday2105.eyetouch.update");
            i.putExtra("eye","4");
            context.sendBroadcast(i);
            blink(3,context);
            return;
        }
    }

    private static int state = 0;
    static void blink(int status,Context context) {
        switch(status){
            case 2:
                Log.w(TAG, "blink: 2");
                if(blink_== 2) break;
                else if(state==1){
                    blinkCount++;
                    if(blinkCount%2==0) {
                        MainActivity.util.blink(context);
                    }
                    state=0;
                    blink_ = 2;

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            blinkCount=0;
                        }
                    }, 2000);

                }
                else {
                    blink_= 2;
                    break;
                }
            case 3:
                Log.w(TAG, "blink: 3");
                if(blink_==3) break;
                else{
                    blink_=3;
                    state = 1;

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            state=0;
                            //Log.w(TAG, "run: resetting state");
                        }
                    }, 1000);
                    break;
                }

        }
    }

    static void runSUCommand(String command) {

        try {
            Runtime.getRuntime().exec(new String[] { "su", "-c", command});
            //Log.w(TAG, "screenLock: executed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void runCommand(String command) {

        try {
            Runtime.getRuntime().exec(command);
            //Log.w(TAG, "screenLock: executed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
