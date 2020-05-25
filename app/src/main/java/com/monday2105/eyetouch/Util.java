package com.monday2105.eyetouch;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class Util {

    private EyeAccessibilityService mEyeAsbService;
    private String TAG = "Util";

    Util(){
        mEyeAsbService = EyeAccessibilityService.getInstance();
    }

    void leftEye(Context context){
                performAction(context.getSharedPreferences("settings",MODE_PRIVATE)
                        .getInt(Integer.toString(R.id.lefteyefun),0));

    }

    void rightEye(Context context){
        performAction(context.getSharedPreferences("settings",MODE_PRIVATE)
                .getInt(Integer.toString(R.id.righteyefun),0));

    }

    void bothEyes(Context context){
        performAction(context.getSharedPreferences("settings",MODE_PRIVATE)
                .getInt(Integer.toString(R.id.botheyefun),0));

    }

    void blink(Context context){
        performAction(context.getSharedPreferences("settings",MODE_PRIVATE)
                .getInt(Integer.toString(R.id.blink),0));

    }

    void noFace(Context context){
        performAction(context.getSharedPreferences("settings",MODE_PRIVATE)
                .getInt(Integer.toString(R.id.nofacefun),0));

    }

    private void performAction(int func){
        if(func>=3 && func<=6){
            if(mEyeAsbService==null){
                Log.i(TAG, "performAction: No AccessibilityService");
                return;
            }
        }
        switch(func){
            case 0:
                return;
            case 1:
                EyeFunctions.runSUCommand("input keyevent "+ KeyEvent.KEYCODE_HOME);
                break;
            case 2:
                EyeFunctions.runSUCommand("input keyevent "+KeyEvent.KEYCODE_BACK);
                break;
            case 3:
                mEyeAsbService.swipe(1);
                break;
            case 4:
                mEyeAsbService.swipe(2);
                break;
            case 5:
                mEyeAsbService.swipe(3);
                break;
            case 6:
                mEyeAsbService.swipe(4);
                break;
            case 7:
                String l ="lock";
                break;
        }
    }
}
