package com.monday2105.eyetouch;

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

public class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

    private Context context;

    FaceTrackerFactory(Context context) {
    this.context = context;
    }

    @Override
    public Tracker<Face> create(Face face) {
        return new EyesTracker(context);
    }
}
