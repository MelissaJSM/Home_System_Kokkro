package com.example.auto_door;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.OkHttpClient;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.Request;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.Response;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends AppCompatActivity{

    private static final int PERMISSIONS = 100;


    TextView beaconTextView;

    TextView beaconTextView_in;

    TextView beaconRunningView;

    int running_check=0;

    //SharedPreferences 활성화
    SharedPreferences hud_pref;
    SharedPreferences.Editor hud_editor;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        beaconRunningView = (TextView) findViewById(R.id.beaconRunningView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION

            }, 0);

        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION

            }, 0);
        }

        // 절전모드 해제 권한
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }

        hud_pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        hud_editor = hud_pref.edit();

        running_check = hud_pref.getInt("running_check", 0);


        if(running_check ==0){
            beaconRunningView.setText("현재 시스템이 동작하지 않고 있어요!");
        }
        else{
            beaconRunningView.setText("현재 시스템이 동작 중 이에요!");
        }

        ImageButton btnStartService = (ImageButton) findViewById(R.id.buttonStartService);
        ImageButton btnStopService = (ImageButton) findViewById(R.id.buttonStopService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
                running_check = 1;

                hud_editor.putInt("running_check", running_check);
                hud_editor.commit();
                beaconRunningView.setText("현재 시스템이 동작 중 이에요!");

            }
        });
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
                running_check = 0;

                hud_editor.putInt("running_check", running_check);
                hud_editor.commit();
                beaconRunningView.setText("현재 시스템이 동작하지 않고 있어요!");
            }
        });
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, DoorService.class);
        serviceIntent.putExtra("inputExtra", "콧코로의 현관문 자동 열기 시스템"); // 여기를 이용해서 데이터 송신도 가능해보임.
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, DoorService.class);
        stopService(serviceIntent);
    }


}


