package com.monday2105.eyetouch;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    static TextView eye;
    //static VideoView videoView;
    TextView sensiText;
    Spinner leftEye;
    Spinner rightEye;
    Spinner bothEyes;
    Spinner blink;
    Spinner noFace;
    String[] items;
    SeekBar sensitivity;
    static boolean camera = false;
    static Button cameraOnOff;
    static BroadcastReceiver mReceiver;
    NotificationActivity mNotif;
    static NotificationCompat.Builder builder;
    static Context context;
    static Util util;

    //For looking logs
    ArrayAdapter adapter;
    ArrayList<String> list = new ArrayList<>();

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
        }
        else {
            /*videoView = findViewById(R.id.videoView);
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videoplayback));
            *///videoView.start();
            sensitivity = findViewById(R.id.sensitivity);
            sensiText = findViewById(R.id.textView3);
            eye = findViewById(R.id.eye);
            adapter = new ArrayAdapter<>(this,   android.R.layout.simple_list_item_1, list);
            leftEye = findViewById(R.id.lefteyefun);
            rightEye = findViewById(R.id.righteyefun);
            bothEyes = findViewById(R.id.botheyefun);
            blink = findViewById(R.id.blinkfun);
            noFace = findViewById(R.id.nofacefun);

            items = new String[]{"Do Nothing", "Home", "Back", "Swipe Left", "Swipe Right", "Swipe Up", "Swipe Down","Lock"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            leftEye.setAdapter(adapter);
            rightEye.setAdapter(adapter);
            bothEyes.setAdapter(adapter);
            blink.setAdapter(adapter);
            noFace.setAdapter(adapter);

            SharedPreferences sharedPref = getSharedPreferences("settings",MODE_PRIVATE);
            ArrayList<Integer> spinners = new ArrayList<>();
            spinners.add(R.id.lefteyefun);
            spinners.add(R.id.righteyefun);
            spinners.add(R.id.botheyefun);
            spinners.add(R.id.blinkfun);
            spinners.add(R.id.nofacefun);
            for(int i=0;i<spinners.size();i++){
                int spinner = spinners.get(i);
                int spinnerValue = sharedPref.getInt(Integer.toString(spinner),0);
                //Log.i(TAG, "forSpinner "+spinner+": "+spinnerValue);
                switch (spinner){
                    case R.id.lefteyefun:
                        leftEye.setSelection(spinnerValue);
                        break;
                    case R.id.righteyefun:
                        rightEye.setSelection(spinnerValue);
                        break;
                    case R.id.botheyefun:
                        bothEyes.setSelection(spinnerValue);
                        break;
                    case R.id.blinkfun:
                        blink.setSelection(spinnerValue);
                        break;
                    case R.id.nofacefun:
                        noFace.setSelection(spinnerValue);
                        break;
                }
            }


            leftEye.setOnItemSelectedListener(this);
            rightEye.setOnItemSelectedListener(this);
            bothEyes.setOnItemSelectedListener(this);
            blink.setOnItemSelectedListener(this);
            noFace.setOnItemSelectedListener(this);



            sensitivity.setProgress(70);
            sensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Intent i = new Intent("com.monday2105.eyetouch.tracker");
                    i.putExtra("threshold",progress);
                    sendBroadcast(i);
                    sensiText.setText(String.format("Sensitivity: %.2f", (progress/100f)));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            cameraOnOff = findViewById(R.id.camera);
            cameraOnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   switchCamera();
                }
            });
            //cameraOnOff.setEnabled(false);

            IntentFilter filter = new IntentFilter("com.monday2105.eyetouch.update");
            mReceiver = new updateReceive();
            registerReceiver(mReceiver, filter);

            mNotif = new NotificationActivity(this,"Camera", "Eye Tracking",
                    "Off","Eye tracker",1);
            mNotif.createNotificationChannel();
            builder = mNotif.createNotification(true);
            builder =  mNotif.setIntent(builder);

            startService(new Intent(this, CreateCamera.class));
            startService(new Intent(this, CameraControlService.class));

            this.sharedPref = getSharedPreferences("settings",0);

            util = new Util();


        }
    }

    static  void switchCamera() {
        if(camera){
            camera = false;
            try {
                cameraOnOff.setText(R.string.camera_off);
            }catch (Exception e){
                e.printStackTrace();
            }
            eye.setText(R.string.camera_off);
            updateNotif();
        }
        else{
            camera = true;
            cameraOnOff.setText(R.string.camera_on);
            updateNotif();
        }
    }

    static void switchCamera(boolean exit){
        camera = false;
        updateNotif();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        try {
            EyesTracker.unregisterTracker();
        }catch (Exception e){
            e.printStackTrace();
        }
        switchCamera(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
        stopService(new Intent(this, CreateCamera.class));
        stopService(new Intent(this, CameraControlService.class));
        Log.i(TAG, "onDestroy: DoneAll");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position>=3 && position<=6){
            boolean asbService = isAccessibilityServiceEnabled(MainActivity.context,EyeAccessibilityService.class);
            if(!asbService){
                Toast.makeText(MainActivity.context,"Please enable Accessibility service to use this feature",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 0);
                return;
            }
        }
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putInt(Integer.toString(parent.getId()),position);
        prefEditor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class updateReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("serviceOn")){
                Log.i(TAG, "run: gotInstance");
            }
            String eyes = intent.getStringExtra("eye");
            //Log.i(TAG, "onReceive: "+eyes+" "+face);
            if (eyes != null) {

                switch (eyes) {
                    case "1":
                        util.leftEye(MainActivity.context);
                        eye.setText("Right Open");
                        break;
                    case "2":
                        util.rightEye(MainActivity.context);
                        eye.setText("Left Open");
                        break;
                    case "3":
                        eye.setText("Both Open");
                        break;
                    case "4":
                        util.bothEyes(MainActivity.context);
                        eye.setText("Both Closed");
                        break;
                    case "5":
                        util.noFace(MainActivity.context);
                        eye.setText("No Face Detected");
                        break;
                }
            }
        }
    }

    public static void updateNotif(){
        builder = NotificationActivity.updateNotification(builder,camera);
    }

    public static void failedCamera(){
        camera = false;
        cameraOnOff.setText(R.string.camera_off);
        eye.setText(R.string.camera_off);
        Toast.makeText(context,"Can't start camera",Toast.LENGTH_LONG).show();
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }

}

