package com.monday2105.eyetouch;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.FingerprintGestureController;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import static android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_DOWN;
import static android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_LEFT;
import static android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_RIGHT;
import static android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_UP;

public class EyeAccessibilityService extends AccessibilityService {

    String TAG = "EyeAsbService";
    private FingerprintGestureController gestureController;
    private FingerprintGestureController
            .FingerprintGestureCallback fingerprintGestureCallback;
    private boolean mIsGestureDetectionAvailable;

    private static EyeAccessibilityService mEyeAsbservice;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {
    }


    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Intent i = new Intent("com.monday2105.eyetouch.update");
        i.putExtra("serviceOn",true);
        this.sendBroadcast(i);
        Log.i(TAG, "onServiceConnected");
    }

    public static EyeAccessibilityService getInstance(){
        return mEyeAsbservice;
    }

    public void fingerPrint(){
        if (fingerprintGestureCallback != null
                || !mIsGestureDetectionAvailable) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fingerprintGestureCallback =
                    new FingerprintGestureController.FingerprintGestureCallback() {

                        @Override
                        public void onGestureDetected(int gesture) {
                            Log.i(TAG, "onGestureDetected: ");
                            switch (gesture) {
                                case FINGERPRINT_GESTURE_SWIPE_DOWN:
                                    break;
                                case FINGERPRINT_GESTURE_SWIPE_LEFT:
                                    break;
                                case FINGERPRINT_GESTURE_SWIPE_RIGHT:
                                    break;
                                case FINGERPRINT_GESTURE_SWIPE_UP:
                                    break;
                                default:
                                    Log.e(TAG,"Error: Unknown gesture type detected!");
                                    break;
                            }
                        }

                        @Override
                        public void onGestureDetectionAvailabilityChanged(boolean available) {
                            mIsGestureDetectionAvailable = available;
                        }
                    };
        }

        if (fingerprintGestureCallback != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                gestureController.registerFingerprintGestureCallback(
                        fingerprintGestureCallback, null);
            }
        }
    }

    public void swipe(int direction){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        int middleYValue = displayMetrics.heightPixels / 2;
        final int leftSideOfScreen = displayMetrics.widthPixels / 4;
        final int rightSizeOfScreen = leftSideOfScreen * 3;
        final int height = displayMetrics.heightPixels;
        final float top = height/4f;
        final float bottom = height * .75f;
        final float midX = displayMetrics.widthPixels / 2f;
        GestureDescription.Builder gestureBuilder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            gestureBuilder = new GestureDescription.Builder();
            Path path = new Path();

            switch(direction) {


                case 1:
                    //Swipe left
                    path.moveTo(rightSizeOfScreen, middleYValue);
                    path.lineTo(leftSideOfScreen, middleYValue);
                    break;
                case 2:
                    //Swipe right
                    path.moveTo(leftSideOfScreen, middleYValue);
                    path.lineTo(rightSizeOfScreen, middleYValue);
                    break;
                case 3:
                    //Swipe Up
                    Log.i(TAG, "swipe: up");
                    path.moveTo(midX, bottom);
                    path.lineTo(midX, top);
                    break;
                case 4:
                    //Swipe Down
                    Log.i(TAG, "swipe: down");
                    path.moveTo(midX, top);
                    path.lineTo(midX, bottom);
            }

            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 100, 50));
            dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    Log.w(TAG, "Gesture Completed");
                    super.onCompleted(gestureDescription);
                }
            }, null);
        }
    }
}
