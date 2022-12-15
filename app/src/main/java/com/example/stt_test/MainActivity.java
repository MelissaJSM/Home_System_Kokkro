package com.example.stt_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.alphamovie.lib.AlphaMovieView;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.OkHttpClient;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.Request;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.Response;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.ResponseBody;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements RecognitionListener, LocationListener {

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private int maxLinesInput = 100;
    private TextView returnedText;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "음성인식 진행중";
    boolean listening = false;

    //SharedPreferences 활성화
    SharedPreferences lucia_pref;
    SharedPreferences.Editor lucia_editor;

    TypedArray lucia_array;

    TypedArray lucia_array_video;

    TypedArray lucia_array_voice;

    TypedArray getLucia_array_address;

    // 오드로이드에서 화면 밝기 조절이 안되는 치명적인 버그가 발생되어 사용을 중지함.
    //private WindowManager.LayoutParams params;


    AlphaMovieView lucia_video;

    MediaPlayer mediaPlayer;

    Uri videoUri;


    //아래 내용은 시간, 날짜, 타이머, 알람 관련 내용


    int toDayyyyy;
    int toDayMM;
    int toDaydd;
    int toDayHH;
    int toDayss;
    int toDaymin;
    int toDayu;
    String toDayE; // 요일을 한국어로 설정함.

    ImageView num_10h;
    ImageView num_1h;
    ImageView num_10min;
    ImageView num_1min;
    ImageView num_10s;
    ImageView num_1s;

    ImageView num_1000y;
    ImageView num_100y;
    ImageView num_10y;
    ImageView num_1y;
    ImageView num_10m;
    ImageView num_1m;
    ImageView num_10d;
    ImageView num_1d;


    LinearLayout time_layout; // 시간 onoff
    LinearLayout date_layout; // 날짜 onoff


    Handler watch_data_Handler = new Handler();

    TypedArray typedArray_num; /// 숫자 이미지 배열


    int alart_hour = Constants.lucia_alarm_timer_empty_error;
    int alart_min = Constants.lucia_alarm_timer_empty_error;

    int timer_min = Constants.lucia_alarm_timer_empty_error;
    int timer_sec = Constants.lucia_alarm_timer_empty_error;

    int all_reset_value = 0;



    //아래 내용은 날씨 관련

    LinearLayout weather_layout;
    LinearLayout weather_max_min_layout;
    LinearLayout today_temp_humi_layout;

    ImageView today_tomorrow_select;
    ImageView weather_imo;
    ImageView weather_temp_10;
    ImageView weather_temp_1;
    ImageView weather_humi_10;
    ImageView weather_humi_1;
    ImageView weather_max_temp_10;
    ImageView weather_max_temp_1;
    ImageView weather_max_humi_10;
    ImageView weather_max_humi_1;
    ImageView weather_min_temp_10;
    ImageView weather_min_temp_1;
    ImageView weather_min_humi_10;
    ImageView weather_min_humi_1;
    ImageView weather_temp_minus;
    ImageView weather_max_temp_minus;
    ImageView weather_min_temp_minus;


    Double latitude;
    Double longtitude;


    //아래 부분은 블루투스 송수신 관련


    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;

    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // 오드로이드에서 화면 밝기 조절이 안되는 치명적인 버그가 발생되어 사용을 중지함.
    /*
    TypedArray lucia_array_illumination_lv;  //알람 시 배열
    */


    private LocationManager locationManager;

    String weather_key = "키를 입력하세요";
    String weather_json;

    String ifttt_key = "키를 입력하세요.";

    String result_weather;
    int today_temp;
    int today_humidity;
    int today_feels_like;
    int today_temp_min;
    int today_temp_max;
    int today_humidity_max;
    int today_humidity_min;


    int tomorrow_temp_min;
    int tomorrow_temp_max;
    int tomorrow_humidity_max;
    int tomorrow_humidity_min;
    // 일출, 일몰 넣을지 말지는 추후 결정

    int today_weather_value = Constants.weather_value_null; //0 값이 적용이 되는 경우가 있어서 이걸로한다.

    String mixed_weather_array[] = new String[18];

    int mixed_temp_array[] = new int[18];

    int mixed_humidity_array[] = new int[18];

    int today_temp_array[] = new int[9];

    int today_humidity_array[] = new int[9];

    int tomorrow_temp_array[] = new int[9];

    int tomorrow_humidity_array[] = new int[9];


    TypedArray lucia_array_weather_stirng; // 날씨 스트링 값

    TypedArray lucia_array_weather_font_temp; // 온도관련 폰트

    TypedArray lucia_array_weather_font_humi; // 습도관련 폰트

    TypedArray lucia_array_weather_imo; // 이모티콘 관련

    TypedArray lucia_array_weather_background_video; // 날씨 백그라운드 비디오

    TypedArray lucia_array_weather_background_stirng; // 날씨 백그라운드 스트링

    VideoView background_video;

    // 까만화면으로 바꾸기 위한 레이아웃

    FrameLayout sleep_layout;

    int standby_mode = Constants.video_standby_sleep; // 프런트 모드에서는 스탠바이 모드의 의미는 블랙화면일때 토스트 안뜨게 하기 위함이다.

    int door_value;
    int room_value;
    int lock_value; // stt_on 중복실행 방지

    int min_1_timeout; // 혹시나 인식미스났을때 1분되면 초기화 실시하는 조건넣는게좋음.




    @SuppressLint({"MissingPermission", "HandlerLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_PERMISSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION

            }, 0);

        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION

            }, 0);
        }


        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 화면 켜짐 강제 유지


        returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);


        //여기부터 시간, 날짜, 알람, 타이머 부분

        watch_data();

        num_10h = (ImageView) findViewById(R.id.num_10h);
        num_1h = (ImageView) findViewById(R.id.num_1h);
        num_10min = (ImageView) findViewById(R.id.num_10min);
        num_1min = (ImageView) findViewById(R.id.num_1min);
        num_10s = (ImageView) findViewById(R.id.num_10s);
        num_1s = (ImageView) findViewById(R.id.num_1s);

        num_1000y = (ImageView) findViewById(R.id.num_1000y);
        num_100y = (ImageView) findViewById(R.id.num_10y);
        num_10y = (ImageView) findViewById(R.id.num_10y);
        num_1y = (ImageView) findViewById(R.id.num_1y);
        num_10m = (ImageView) findViewById(R.id.num_10m);
        num_1m = (ImageView) findViewById(R.id.num_1m);
        num_10d = (ImageView) findViewById(R.id.num_10d);
        num_1d = (ImageView) findViewById(R.id.num_1d);

        time_layout = (LinearLayout) findViewById(R.id.time_layout);
        date_layout = (LinearLayout) findViewById(R.id.date_layout);








        typedArray_num = getResources().obtainTypedArray(R.array.png_num_0); /// 숫자 이미지 배열

        // 아래 부분은 날씨 관련

        lucia_array_weather_stirng = getResources().obtainTypedArray(R.array.lucia_select_weather_string); // 날짜의 요일 string 데이터
        lucia_array_weather_font_temp = getResources().obtainTypedArray(R.array.png_num_temp); // 온도관련 폰트
        lucia_array_weather_font_humi = getResources().obtainTypedArray(R.array.png_num_humi); // 습도관련 폰트
        lucia_array_weather_imo = getResources().obtainTypedArray(R.array.lucia_select_weather_imo);

        today_tomorrow_select = findViewById(R.id.today_tomorrow_select);
        weather_imo = findViewById(R.id.weather_imo);
        weather_temp_10 = findViewById(R.id.weather_temp_10);
        weather_temp_1 = findViewById(R.id.weather_temp_1);
        weather_humi_10 = findViewById(R.id.weather_humi_10);
        weather_humi_1 = findViewById(R.id.weather_humi_1);
        weather_max_temp_10 = findViewById(R.id.weather_max_temp_10);
        weather_max_temp_1 = findViewById(R.id.weather_max_temp_1);
        weather_max_humi_10 = findViewById(R.id.weather_max_humi_10);
        weather_max_humi_1 = findViewById(R.id.weather_max_humi_1);
        weather_min_temp_10 = findViewById(R.id.weather_min_temp_10);
        weather_min_temp_1 = findViewById(R.id.weather_min_temp_1);
        weather_min_humi_10 = findViewById(R.id.weather_min_humi_10);
        weather_min_humi_1 = findViewById(R.id.weather_min_humi_1);
        weather_temp_minus = findViewById(R.id.weather_temp_minus);
        weather_max_temp_minus = findViewById(R.id.weather_max_temp_minus);
        weather_min_temp_minus = findViewById(R.id.weather_min_temp_minus);
        weather_layout = findViewById(R.id.weather_layout);
        weather_max_min_layout = findViewById(R.id.weather_max_min_layout);
        today_temp_humi_layout = findViewById(R.id.today_temp_humi_layout);

        //블루투스 연결 배열
        // 오드로이드에서 화면 밝기 조절이 안되는 치명적인 버그가 발생되어 사용을 중지함.
        //lucia_array_illumination_lv = getResources().obtainTypedArray(R.array.lucia_illumination_lv);

        // 셀렉트 보이스

        lucia_array_voice = getResources().obtainTypedArray(R.array.lucia_select_voice);

        //백그라운드 비디오 배열

        lucia_array_weather_background_video = getResources().obtainTypedArray(R.array.lucia_select_weather_background_video);

        lucia_array_weather_background_stirng = getResources().obtainTypedArray(R.array.lucia_select_weather_background_string);

        background_video = (VideoView) findViewById(R.id.background_video);

        background_video.setVisibility(View.INVISIBLE);


        // 10분 뒤 잠수모드에서 사용해야 할 레이아웃을 모아놨음

        sleep_layout = (FrameLayout) findViewById(R.id.sleep_layout);


        // sharedpref 에서 get 작업

        lucia_pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        lucia_editor = lucia_pref.edit();

        alart_hour = lucia_pref.getInt("alart_hour", Constants.lucia_alarm_timer_empty_error);
        alart_min = lucia_pref.getInt("alart_min", Constants.lucia_alarm_timer_empty_error);
        timer_min = lucia_pref.getInt("timer_min", Constants.lucia_alarm_timer_empty_error);
        timer_sec = lucia_pref.getInt("timer_sec", Constants.lucia_alarm_timer_empty_error);


        today_temp_max = lucia_pref.getInt("today_temp_max", 0);
        today_temp_min = lucia_pref.getInt("today_temp_min", 0);

        today_humidity_max = lucia_pref.getInt("today_humidity_max", 0);
        today_humidity_min = lucia_pref.getInt("today_humidity_min", 0);

        tomorrow_temp_max = lucia_pref.getInt("tomorrow_temp_max", 0);
        tomorrow_temp_min = lucia_pref.getInt("tomorrow_temp_min", 0);

        tomorrow_humidity_max = lucia_pref.getInt("tomorrow_humidity_max", 0);
        tomorrow_humidity_min = lucia_pref.getInt("tomorrow_humidity_min", 0);

        mixed_weather_array[11] = lucia_pref.getString("mixed_weather_array[11]", "Clear");


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //블루투스 연결 어댑터

        bluetoothOn();

        mBluetoothHandler = new Handler() {

            public void handleMessage(android.os.Message msg) {
                System.out.println(" 이 핸들러에 값 제대로 들어오는지 확인");
                if (msg.what == BT_MESSAGE_READ) {
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8"); // 아니면 여기서 필터링을 자체적으로 걸어도됨.
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    System.out.println(readMessage);

                    if(readMessage.contains("distance_door_on")){
                        door_value = 1;
                    }

                    if(readMessage.contains("distance_room_on")){
                        room_value = 1;
                    }




                    //텍스트 메시지 처리하는곳
                }
            }
        };

        listPairedDevices(); // 블루투스 핸들러 아래에 연결 할 것.



        //gps 수신 및 날씨 네트워크 연결 부분

        gps_location_catch();


        weather_net_connect();


        //////////// gps 에러시 무조건 화ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㄱ인
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);


        ////////////////////비디오 플레이어/////////////////////////////////
        lucia_video = findViewById(R.id.alpha_player);

        int video_change_value = Constants.video_idle;

        player_change_video(video_change_value);


        /////////////비디오 플레이어//////////////////

        // 0시 6시 12시 18시에 한번씩 재부팅 해주는 설정을 넣을 예정.
        restart_hanlder();

        stt_on();

        stt_off();


        front_hanlder();



    }


    @SuppressLint("MissingPermission")
    void bluetoothOn() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        }
        else {
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                }
                else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("MissingPermission")
    void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);


                for (int i = 0; i < items.length; i++) {
                    System.out.println("블루투스 리스트 " + i + " : " + items[i]);
                }
                connectSelectedDevice(items[0].toString());
                System.out.println("블루투스 연결된 아이템 이름 : " + items[0].toString());


                //AlertDialog alert = builder.create();
                //alert.show();
            }
            else {
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    void connectSelectedDevice(String selectedDeviceName) {
        for (BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
            Toast.makeText(getApplicationContext(), "블루투스와의 연결에 성공했습니다.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        //SystemClock.sleep(100); // 이거 핸들러로 교체하는게?
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
    }


    public void start() {
        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxLinesInput);
    }

    public void turnOf() {
        speech.stopListening();
        speech.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(standby_mode == Constants.wake_up) {
                        Toast.makeText(MainActivity.this, "지금 말하시면 됩니다!", Toast.LENGTH_SHORT).show();
                    }
                    speech.startListening(recognizerIntent);
                }
                else {
                    Toast.makeText(MainActivity.this, "블루투스 퍼미션 체크중..", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        lucia_video.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lucia_video.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //        if (speech != null) {
        //            speech.destroy();
        //            Log.i(LOG_TAG, "destroy");
        //        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
        if (!listening) {
            turnOf();
        }
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(LOG_TAG, "onBufferReceived: " + bytes);

    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        returnedText.setText(errorMessage);
        speech.startListening(recognizerIntent);

    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        Log.i(LOG_TAG, "onResults=" + text);
        returnedText.setText(text);

        speech.startListening(recognizerIntent);

        // stt 가 활성화 되었을때 해당 요청사항을 리스트에서 찾도록 한다.

        for (int list_value = 0; list_value < 2; list_value++) {  // 여기서 리스트 밸류는 호출하는 단어가 2개일 가능성을 고려하여 생성함.

            switch (list_value) { // 호출은 여러 이름으로 할 수 있음을 가정하여 작업한다.
                case 0:
                    lucia_array = getResources().obtainTypedArray(R.array.lucia_select);
                    break;
                case 1:
                    lucia_array = getResources().obtainTypedArray(R.array.lucia_select2); // 추후 중복을 피하기위한 다른 배열 쓰는걸로 진입할예정
                    break;
            }


            for (int select = 0; select < lucia_array.length(); select++) { // select에 호출하는 이름이 정확하게 있을 경우.
                if (text.contains(lucia_array.getString(select))) {
                    network_trans(select);
                    return;
                }
            }
        }



        // 동작시 값 입력 장치 필요.
    }

    @Override
    public void onPartialResults(Bundle results) {
        Log.i(LOG_TAG, "onPartialResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        Log.i(LOG_TAG, "onPartialResults=" + text);

        // 루시아가 인식을 했을경우 사용하도록 함.

    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");

    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                turnOf();
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }




    private void stt_off() {
        //음성인식 조절
        listening = false;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
        turnOf();

    }

    private void stt_on() {
        // 음성인식 조절
        listening = true;
        start();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_PERMISSION);

    }

    private void player_change_video(int video_value) {
        System.out.println("player_change_video 진입 성공");

        lucia_array_video = getResources().obtainTypedArray(R.array.lucia_select_video);


        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + lucia_array_video.getResourceId(video_value, -1));

        System.out.println("루시아 콜 번호 : " + video_value);
        lucia_video.setVideoFromUri(MainActivity.this, videoUri);


    }

    private void video_completion_idle() {

        // TODO Auto-generated method stub
        int video_change_value;


        video_change_value = Constants.video_idle;

        player_change_video(video_change_value);

    }

    private void player_change_audio(int audio_value) {

        stt_off();

        mediaPlayer = MediaPlayer.create(MainActivity.this, lucia_array_voice.getResourceId(audio_value, -1));


        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                video_completion_idle();
                stt_on();

            }

        });

    }



    private void network_trans(int call_check) {


        new Thread(new Runnable() {
            @Override
            public void run() {

                getLucia_array_address = getResources().obtainTypedArray(R.array.network_address);


                try {
                    String url = "https://maker.ifttt.com/trigger/" + getLucia_array_address.getString(call_check) + "/json/with/key/" + ifttt_key;

                    System.out.println(url);


                    System.out.println("url 입력 전송중");

                    // OkHttp 클라이언트 객체 생성
                    OkHttpClient client = new OkHttpClient();

                    // GET 요청 객체 생성
                    Request.Builder builder = new Request.Builder().url(url).get();
                    Request request = builder.build();

                    // OkHttp 클라이언트로 GET 요청 객체 전송
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        // 응답 받아서 처리
                        ResponseBody body = response.body();
                        if (body != null) {
                            System.out.println("Response:" + body.string()); // 대답 받는 부위

                            //성공적으로 받았으면 여기에 입력한다..


                            runOnUiThread(new Runnable() { // 지금 배열의 최대 크기만큼 반복하고 있음. 원인찾아야함.
                                @Override
                                public void run() {
                                    stt_off();
                                    int video_change_value = Constants.video_success;
                                    player_change_video(video_change_value);

                                    int audio_change_value = call_check;
                                    player_change_audio(audio_change_value);


                                }
                            });

                        }
                    }
                    else
                        //성공적으로 못 받았으면 여기에 입력한다.
                        System.err.println("Error Occurred");


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("이번엔 주소가 잘못되었나본데");

                    runOnUiThread(new Runnable() { // 지금 배열의 최대 크기만큼 반복하고 있음. 원인찾아야함.
                        @Override
                        public void run() {
                            stt_off();
                            int video_change_value = Constants.video_net_error;
                            player_change_video(video_change_value);


                            int audio_change_value = Constants.audio_net_error;
                            player_change_audio(audio_change_value);


                        }
                    });

                }


            }
        }).start();


    }

    private void watch_data() { // 실시간 시간 체크

        watch_data_Handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                Date nowDate = new Date();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.KOREAN);
                toDayyyyy = Integer.parseInt(simpleDateFormat.format(nowDate));

                simpleDateFormat = new SimpleDateFormat("MM", Locale.KOREAN);
                toDayMM = Integer.parseInt(simpleDateFormat.format(nowDate));

                simpleDateFormat = new SimpleDateFormat("dd", Locale.KOREAN);
                toDaydd = Integer.parseInt(simpleDateFormat.format(nowDate));

                simpleDateFormat = new SimpleDateFormat("HH", Locale.KOREAN);
                toDayHH = Integer.parseInt(simpleDateFormat.format(nowDate));

                simpleDateFormat = new SimpleDateFormat("mm", Locale.KOREAN);
                toDaymin = Integer.parseInt(simpleDateFormat.format(nowDate));

                simpleDateFormat = new SimpleDateFormat("ss", Locale.KOREAN);
                toDayss = Integer.parseInt(simpleDateFormat.format(nowDate));

                simpleDateFormat = new SimpleDateFormat("u", Locale.KOREAN);
                toDayu = Integer.parseInt(simpleDateFormat.format(nowDate));

                simpleDateFormat = new SimpleDateFormat("E", Locale.KOREAN);
                toDayE = simpleDateFormat.format(nowDate);

                // 여기에 1초마다 핸들러 필요함.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        num_1000y.setImageResource(typedArray_num.getResourceId(toDayyyyy / 1000, -1));
                        num_100y.setImageResource(typedArray_num.getResourceId((toDayyyyy % 1000) / 100, -1));
                        num_10y.setImageResource(typedArray_num.getResourceId(((toDayyyyy % 1000) % 100) / 10, -1));
                        num_1y.setImageResource(typedArray_num.getResourceId(((toDayyyyy % 1000) % 100) % 10, -1));


                        num_10m.setImageResource(typedArray_num.getResourceId(toDayMM / 10, -1));
                        num_1m.setImageResource(typedArray_num.getResourceId(toDayMM % 10, -1));


                        num_10d.setImageResource(typedArray_num.getResourceId(toDaydd / 10, -1));
                        num_1d.setImageResource(typedArray_num.getResourceId(toDaydd % 10, -1));


                        num_10h.setImageResource(typedArray_num.getResourceId(toDayHH / 10, -1));
                        num_1h.setImageResource(typedArray_num.getResourceId(toDayHH % 10, -1));


                        num_10min.setImageResource(typedArray_num.getResourceId(toDaymin / 10, -1));
                        num_1min.setImageResource(typedArray_num.getResourceId(toDaymin % 10, -1));


                        num_10s.setImageResource(typedArray_num.getResourceId(toDayss / 10, -1));
                        num_1s.setImageResource(typedArray_num.getResourceId(toDayss % 10, -1));


                    }
                });

                watch_data_Handler.postDelayed(this, 1000);


            }
        }, 1000);


    }

    //여기까지 시간,날짜,타이머,알람 관련 부분 메소드
    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void gps_location_catch() {
        /// gps 좌표
        Handler gps_now_location_check_Handler = new Handler();
        gps_now_location_check_Handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //현재 위도와 경도 좌표 수신
                        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        try {
                            latitude = location.getLatitude();
                            longtitude = location.getLongitude();
                        } catch (NullPointerException e) {
                            latitude = 0.0;
                            longtitude = 0.0;
                            System.out.println(" 널값 발생으로 인한 임시 지정된 좌표 지정");
                        }
                        System.out.println("위도와 경도 : " + latitude + ", " + longtitude); // 값이 비어있는경우 0.0 이 들어오는거 확인 완료.


                    }
                }).start();


                gps_now_location_check_Handler.postDelayed(this, 1000);


            }
        }, 0);
    }

    private void weather_net_connect() {

        Handler weather_net_connect_Handler = new Handler();
        weather_net_connect_Handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        System.out.println("스레드 들어오는거 맞아?");

                        for (int replay = 0; replay < 2; replay++) {

                            String url;
                            try {

                                if (replay == 0) {
                                    url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longtitude + "&appid=" + weather_key + "&units=metric"; // 오늘 예보
                                    System.out.println("오늘 날씨 값 주소 : " + url);
                                }
                                else {
                                    url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longtitude + "&cnt=18&appid=" + weather_key + "&units=metric"; // 내일 예보용
                                    System.out.println("내일 날씨 값 주소 : " + url);
                                }



                                //cnt 가 어디까지 작업 할 지에 관한 내용임.
                                //이걸 이용하면 오늘 날짜의 최소 최대를 알 수 있음.


                                // 내일 날씨는 시간별로 표시하게 할 수도 있음. (화면에만)
                                // 사운드는 내일 날씨 9시 기준으로만 설명 하도록 하자.

                                // 3시간 마다 알리는 내용은 첫시작이 항상 15시 부터이다.

                                // 15 18 21         24        3 6 9 12 15
                                // 0  1  2          3         4 5 6 7  8

                                // 24는 오늘 내일 둘다 계산할때 쓰여야 한다.


                                System.out.println(url);


                                System.out.println("url 입력 전송중");

                                // OkHttp 클라이언트 객체 생성
                                OkHttpClient client = new OkHttpClient();

                                // GET 요청 객체 생성
                                Request.Builder builder = new Request.Builder().url(url).get();
                                Request request = builder.build();

                                // OkHttp 클라이언트로 GET 요청 객체 전송
                                Response response = client.newCall(request).execute();
                                if (response.isSuccessful()) {
                                    // 응답 받아서 처리
                                    ResponseBody body = response.body();
                                    if (body != null) {

                                        weather_json = body.string();
                                        System.out.println("Response:" + weather_json); // 대답 받는 부위

                                        if (replay == 0) {
                                            JSONParse_today(weather_json);
                                            lucia_today_weather_array_set(); // 오늘 날씨 표시해주는건데.. 이거 5초뒤 메소드에 넣는게 좋을듯?
                                        }
                                        else { // 여기에 새벽 6시 이후로 값 얻어오는 작업이 하나 필요하긴 한데....
                                            if(toDayHH>=6 && toDayHH<=7) {
                                                JSONParse_tomorrow(weather_json);
                                                lucia_today_weather_array_set(); // 오늘 날씨 표시해주는건데.. 이거 5초뒤 메소드에 넣는게 좋을듯?
                                            }
                                        }

                                    }
                                }
                                else
                                    //성공적으로 못 받았으면 여기에 입력한다.
                                    System.err.println("Error Occurred");


                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("이번엔 주소가 잘못되었나본데");

                            }

                        }
                    }
                }).start();

                weather_net_connect_Handler.postDelayed(this, 3600000); // 이건 반복 실행이다.


            }
        }, 5000); // 다시한번 말하지만 코드 햇갈리지 마라. 이건 늦게 실행이다.


    }

    private void JSONParse_today(String weather_value) throws JSONException {

        JSONObject original_JSON;
        try {
            original_JSON = new JSONObject(weather_value); //전체 배열 가져오기 중복 없음!
        } catch (NullPointerException e) {
            System.out.println("original_JSON 에서 null 값 발생으로 인한 리턴");
            return;
        }

        JSONArray weather_JSON = original_JSON.optJSONArray("weather"); // 전체 배열 중 resultdata 아래 있는 데이터 가져오기) 중복 없음!
        if (weather_JSON == null) { // null 값 검증
            System.out.println("resultData_JSON 0값 발생했음 ");
            return;
        }
        JSONObject weather_JSON_in = weather_JSON.optJSONObject(0); // 이 배열이 동일값 배열 쭉 나열하는거일텐데... 아마 여기서 몇번째 값만 추출하면 내일 날씨 알 수도 있다.!
        System.out.println("weather_JSON_in 의 배열 값 : " + weather_JSON_in + " 그리고 i 의 값 : " + 0);
        System.out.println("weather_JSON_in 배열 길이 : " + weather_JSON_in.length());
        if (weather_JSON_in == null) {
            System.out.println("jo 값 마저 null 발생함.");
            return;
        }
        result_weather = weather_JSON_in.optString("main", "");
        System.out.println("현재 날씨는? : " + result_weather);

        ////////////////////////////////////////////////////////////////////////////////////////////


        JSONObject main_JSON = original_JSON.optJSONObject("main"); // 전체 배열 중 resultdata 아래 있는 데이터 가져오기) 중복 없음!
        if (main_JSON == null) { // null 값 검증
            System.out.println("main_JSON 0값 발생했음 ");
            return;
        }
        System.out.println("main_JSON 내용 : " + main_JSON);

        today_temp = main_JSON.optInt("temp", 0);
        today_feels_like = main_JSON.optInt("feels_like", 0);
        //today_temp_min = main_JSON.optInt("temp_min", 0);
        //today_temp_max = main_JSON.optInt("temp_max", 0);  // 최대 최소는 3시간마다 알려주는 데이터를 이용하여 작업하자.
        today_humidity = main_JSON.optInt("humidity", 0);


        System.out.println("현재 온도는? : " + today_temp);
        System.out.println("현재 체감 온도는? : " + today_feels_like);
        //System.out.println("현재 최소 온도는? : " + today_temp_min);
        //System.out.println("현재 최대 온도는? : " + today_temp_max);
        System.out.println("현재 습도는? : " + today_humidity);


        //여기에 날씨에 따른 비디오 넣으면 됨.

        for (int select = 0; select < lucia_array_weather_background_stirng.length(); select++) { //여기서의 셀렉트는 날씨 밸류임.
            if (result_weather.contains(lucia_array_weather_background_stirng.getString(select))) {  // 현재 test = 이제 들어올 음성

                if (select == 24) {
                    select = 0;
                }

                if (select >= 25 && select <= 33) {
                    select = 20;
                }

                int background_before_time;

                if (toDayHH >= 7 && toDayHH <= 14) {
                    background_before_time = 0;
                }
                else if (toDayHH >= 15 && toDayHH <= 19) {
                    background_before_time = 1;
                }
                else if ((toDayHH >= 20 && toDayHH <= 24) || (toDayHH >= 0 && toDayHH <= 3)) {
                    background_before_time = 2;
                }
                else {
                    background_before_time = 3;
                }

                System.out.println("###################################################################");
                System.out.println("투데이 HH 값 : " + toDayHH);
                System.out.println("그로인한 합산 값 : " + (select + background_before_time));
                System.out.println("SELECT 값 : " + (select));

                Uri backgroundUri = Uri.parse("android.resource://" + getPackageName() + "/" + lucia_array_weather_background_video.getResourceId((select + background_before_time), -1));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 비디오 동작 소스 작동
                        background_video.setVisibility(View.VISIBLE);
                        background_video.setVideoURI(backgroundUri);
                        video_repeat_set();
                    }
                });
                break;
            }


        }

    }

    private void JSONParse_tomorrow(String weather_value) throws JSONException {

        JSONObject original_JSON;
        try {
            original_JSON = new JSONObject(weather_value); //전체 배열 가져오기 중복 없음!
        } catch (NullPointerException e) {
            System.out.println("original_JSON 에서 null 값 발생으로 인한 리턴");
            return;
        }

        JSONArray JSON_list = original_JSON.optJSONArray("list"); // 전체 배열 중 resultdata 아래 있는 데이터 가져오기) 중복 없음!
        if (JSON_list == null) { // null 값 검증
            System.out.println("resultData_JSON 0값 발생했음 ");
            return;
        }

        for (int tommorow = 0; tommorow < JSON_list.length(); tommorow++) {
            // 오늘 날씨, 오늘 온도 기입

            //내일 날씨 부분
            JSONArray weather_JSON = JSON_list.optJSONObject(tommorow).optJSONArray("weather"); // 전체 배열 중 resultdata 아래 있는 데이터 가져오기) 중복 없음!
            if (weather_JSON == null) { // null 값 검증
                System.out.println("resultData_JSON 0값 발생했음 ");
                return;
            }
            JSONObject weather_JSON_in = weather_JSON.optJSONObject(0); // 이 배열이 동일값 배열 쭉 나열하는거일텐데... 아마 여기서 몇번째 값만 추출하면 내일 날씨 알 수도 있다.!
            System.out.println("weather_JSON_in 의 배열 값 : " + weather_JSON_in + " 그리고 i 의 값 : " + 0);
            System.out.println("weather_JSON_in 배열 길이 : " + weather_JSON_in.length());
            if (weather_JSON_in == null) {
                System.out.println("jo 값 마저 null 발생함.");
                return;
            }
            mixed_weather_array[tommorow] = weather_JSON_in.optString("main", "");


            //내일 온습도 관련
            JSONObject main_JSON = JSON_list.optJSONObject(tommorow).optJSONObject("main"); // 전체 배열 중 resultdata 아래 있는 데이터 가져오기) 중복 없음!
            if (main_JSON == null) { // null 값 검증
                System.out.println("main_JSON 0값 발생했음 ");
                return;
            }
            System.out.println("main_JSON 내용 : " + main_JSON);

            mixed_temp_array[tommorow] = main_JSON.optInt("temp", 0);
            //today_temp_min = main_JSON.optInt("temp_min", 0);
            //today_temp_max = main_JSON.optInt("temp_max", 0);  // 최대 최소는 3시간마다 알려주는 데이터를 이용하여 작업하자.
            mixed_humidity_array[tommorow] = main_JSON.optInt("humidity", 0);


        }


        // 15 18 21         24        3 6 9 12 15
        // 0  1  2          3         4 5 6 7  8
        // 오늘 내일 분리작업 잊지말자!


        // 0 3 6 9 12 15 18 21 (24  0) 3 6 9 12 15 18 21 24

        /*
        for(int i=0; i<8; i++){ // 나중에 여기서 순서필터해서 최소 최대를 뽑아내자.
            System.out.println("현재/내일 날씨는? : " + mixed_weather_array[i]);
            System.out.println("현재/내일 온도는? : " + (mixed_temp_array[i] - 273));
            System.out.println("현재/내일 습도는? : " + mixed_humidity_array[i]);//
        }

         */

        // 오늘 / 내일 분리작업
        for (int today = 0; today < 9; today++) {
            today_temp_array[today] = mixed_temp_array[today];
            today_humidity_array[today] = mixed_humidity_array[today];
        }

        for (int today = 9; today < 18; today++) {
            tomorrow_temp_array[today - 9] = mixed_temp_array[today];
            tomorrow_humidity_array[today - 9] = mixed_humidity_array[today];
        }

        // 최소 / 최대 정리작업
        Arrays.sort(today_temp_array);
        Arrays.sort(today_humidity_array);

        Arrays.sort(tomorrow_temp_array);
        Arrays.sort(tomorrow_humidity_array);

        // 최소 / 최대 등록작업
        today_temp_max = today_temp_array[8];
        today_temp_min = today_temp_array[0];

        today_humidity_max = today_humidity_array[8];
        today_humidity_min = today_humidity_array[0];


        tomorrow_temp_max = tomorrow_temp_array[8];
        tomorrow_temp_min = tomorrow_temp_array[0];

        tomorrow_humidity_max = tomorrow_humidity_array[8];
        tomorrow_humidity_min = tomorrow_humidity_array[0];

        // Thunderstorm, Drizzle, Rain, Snow, Clear, Clouds, (Mist, Smoke, Haze, Dust, Fog, Sand, Dust, Ash, Squall), Tornado - 이건 태풍으로 써야할듯?


    }

    private void lucia_today_weather_array_set() {


        System.out.println("오늘 날씨는? : " + result_weather);

        System.out.println("오늘 최고 온도는? : " + today_temp_max);
        System.out.println("오늘 최저 온도는? : " + today_temp_min);

        System.out.println("오늘 최고 습도는? : " + today_humidity_max);
        System.out.println("오늘 최저 습도는? : " + today_humidity_min);


        today_tomorrow_select.setImageResource(R.drawable.today_weather); // 오늘 날씨 선정
        today_temp_humi_layout.setVisibility(View.VISIBLE);

        for (int select = Constants.weather_list; select < lucia_array_weather_stirng.length(); select++) {
            if (result_weather.contains(lucia_array_weather_stirng.getString(select))) {  // 현재 test = 이제 들어올 음성
                System.out.println(select + " 현재 날씨 밸류값");
                today_weather_value = select;
                weather_imo.setImageResource(lucia_array_weather_imo.getResourceId(select, -1));

            }

        }

        if (today_weather_value >= Constants.weather_mist_start && today_weather_value <= Constants.weather_mist_end) {
            today_weather_value = Constants.weather_mist_start;
            weather_imo.setImageResource(lucia_array_weather_imo.getResourceId(today_weather_value, -1));
        }

        if (today_weather_value == Constants.weather_value_null) {
            //player_change_video(Constants.video_alarm_error);
            //player_change_audio(Constants.video_alarm_error);
            //
            System.out.println("값을 처리 못했어 에러 표시필요.");
        }
        else {
            int today_temp_filter;
            //영하 처리가 하나 필요해.
            if (Integer.toString(today_temp).contains("-")) {
                today_temp_filter = Integer.parseInt(Integer.toString(today_temp).replaceAll("-", ""));
                weather_temp_minus.setVisibility(View.VISIBLE); // 영하권 미표시
            }
            else {
                weather_temp_minus.setVisibility(View.GONE); // 영하권 미표시
                today_temp_filter = today_temp;
            }

            ////////////////////////// 온도 숫자
            if (today_temp_filter / 10 != 0) {
                if (today_temp_filter / 10 == 1) {
                    weather_temp_10.setImageResource(lucia_array_weather_font_temp.getResourceId(Constants.weather_font_1, -1));
                }
                else {
                    weather_temp_10.setVisibility(View.VISIBLE); // 영하권 미표시
                    weather_temp_10.setImageResource(lucia_array_weather_font_temp.getResourceId(today_temp_filter / 10, -1)); // 영하권 표시

                }
            }
            else {
                weather_temp_10.setVisibility(View.GONE);
            }

            weather_temp_1.setImageResource(lucia_array_weather_font_temp.getResourceId(today_temp_filter % 10, -1)); // 영하권 표시

            /////////////////////////// 온도 숫자 끝


            ////////////////////////// 습도 숫자
            if (today_humidity / 10 != 0) {
                if (today_humidity / 10 == 1) {
                    weather_humi_10.setVisibility(View.VISIBLE);
                    weather_humi_10.setImageResource(lucia_array_weather_font_humi.getResourceId(Constants.weather_font_1, -1)); // 영하권 표시
                }
                else {
                    weather_humi_10.setVisibility(View.VISIBLE);
                    weather_humi_10.setImageResource(lucia_array_weather_font_humi.getResourceId(today_humidity / 10, -1));
                }
            }
            else {
                weather_humi_10.setVisibility(View.GONE);
            }
            weather_humi_1.setImageResource(lucia_array_weather_font_humi.getResourceId(today_humidity % 10, -1));
            /////////////////////////// 습도 숫자 끝

            //최대 최소 데이터 출력
            int today_temp_max_filter;
            //영하 처리가 하나 필요해.
            if (Integer.toString(today_temp_max).contains("-")) { // 영하처리
                today_temp_max_filter = Integer.parseInt(Integer.toString(today_temp_max).replaceAll("-", ""));
                weather_max_temp_minus.setVisibility(View.VISIBLE);
            }
            else {
                today_temp_max_filter = today_temp_max;
                weather_max_temp_minus.setVisibility(View.GONE);
            }

            ////////////////////////// 최대 온도 숫자
            if (today_temp_max_filter / 10 != 0) {
                if (today_temp_max_filter / 10 == 1) {
                    weather_max_temp_10.setVisibility(View.VISIBLE);
                    weather_max_temp_10.setImageResource(lucia_array_weather_font_temp.getResourceId(Constants.weather_font_1, -1)); //
                }
                else {
                    weather_max_temp_10.setVisibility(View.VISIBLE);
                    weather_max_temp_10.setImageResource(lucia_array_weather_font_temp.getResourceId(today_temp_max_filter / 10, -1)); //
                }
            }
            else {
                weather_max_temp_10.setVisibility(View.GONE);
            }
            weather_max_temp_1.setImageResource(lucia_array_weather_font_temp.getResourceId(today_temp_max_filter % 10, -1)); //
            /////////////////////////// 최대 온도 숫자 끝

            ////////////////////////// 최대 습도 숫자
            if (today_humidity_max / 10 != 0) {
                if (today_humidity_max / 10 == 1) {
                    weather_max_humi_10.setVisibility(View.VISIBLE);
                    weather_max_humi_10.setImageResource(lucia_array_weather_font_humi.getResourceId(Constants.weather_font_1, -1)); //
                }
                else {
                    weather_max_humi_10.setVisibility(View.VISIBLE);
                    weather_max_humi_10.setImageResource(lucia_array_weather_font_humi.getResourceId(today_humidity_max / 10, -1)); //
                }
            }
            else {
                weather_max_humi_10.setVisibility(View.GONE);
            }
            weather_max_humi_1.setImageResource(lucia_array_weather_font_humi.getResourceId(today_humidity_max % 10, -1)); //
            /////////////////////////// 최대습도 숫자 끝

            //최대 최소 데이터 출력
            int today_temp_min_filter;
            //영하 처리가 하나 필요해.
            if (Integer.toString(today_temp_min).contains("-")) { // 영하처리
                today_temp_min_filter = Integer.parseInt(Integer.toString(today_temp_min).replaceAll("-", ""));
                weather_min_temp_minus.setVisibility(View.VISIBLE);

            }
            else {
                today_temp_min_filter = today_temp_min;
                weather_min_temp_minus.setVisibility(View.GONE);
            }

            ////////////////////////// 최소 온도 숫자
            if (today_temp_min_filter / 10 != 0) {
                if (today_temp_min_filter / 10 == 1) {
                    weather_min_temp_10.setVisibility(View.VISIBLE);
                    weather_min_temp_10.setImageResource(lucia_array_weather_font_temp.getResourceId(Constants.weather_font_1, -1)); //

                }
                else {
                    weather_min_temp_10.setVisibility(View.VISIBLE);
                    weather_min_temp_10.setImageResource(lucia_array_weather_font_temp.getResourceId(today_temp_min_filter / 10, -1)); //
                }
            }
            else {
                weather_min_temp_10.setVisibility(View.GONE);
            }

            weather_min_temp_1.setImageResource(lucia_array_weather_font_temp.getResourceId(today_temp_min_filter % 10, -1)); //
            /////////////////////////// 최소 온도 숫자 끝

            ////////////////////////// 최소 습도 숫자
            if (today_humidity_min / 10 != 0) {
                if (today_humidity_min / 10 == 1) {
                    weather_min_humi_10.setVisibility(View.VISIBLE);
                    weather_min_humi_10.setImageResource(lucia_array_weather_font_humi.getResourceId(Constants.weather_font_1, -1)); //
                }
                else {
                    weather_min_humi_10.setVisibility(View.VISIBLE);
                    weather_min_humi_10.setImageResource(lucia_array_weather_font_humi.getResourceId(today_humidity_min / 10, -1)); //
                }
            }
            else {
                weather_min_humi_10.setVisibility(View.GONE);
            }

            weather_min_humi_1.setImageResource(lucia_array_weather_font_humi.getResourceId(today_humidity_min % 10, -1)); //
            /////////////////////////// 최소 습도 숫자 끝


            //weather_layout.setVisibility(View.VISIBLE);
            //weather_max_min_layout.setVisibility(View.VISIBLE);



        }

    }

    private void video_repeat_set() {

        //동영상을 읽어오는데 시간이 걸리므로..
        //비디오 로딩 준비가 끝났을 때 실행하도록..
        //리스너 설정
        background_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //비디오 시작

                background_video.start();
            }
        });

        background_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                while (background_video.isPlaying() == true) {
                }
                background_video.start();
            }

        });

    }


    private void restart_hanlder() {

        Handler restart_handler = new Handler();
        restart_handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 핸들러 처리해주면됨.

                if((toDayHH ==0 || toDayHH == 6 || toDayHH == 12 || toDayHH == 18) && toDayss==0 && toDaymin==1){

                    lucia_editor.putInt("alart_hour", alart_hour);
                    lucia_editor.commit();
                    lucia_editor.putInt("alart_min", alart_min);
                    lucia_editor.commit();

                    lucia_editor.putInt("timer_min", timer_min);
                    lucia_editor.commit();
                    lucia_editor.putInt("timer_sec", timer_sec);
                    lucia_editor.commit();


                    //새벽 6시에 값 불러오기 전까진 기존 값 이용하도록 해야한다.

                    lucia_editor.putInt("today_temp_max", today_temp_max);
                    lucia_editor.commit();
                    lucia_editor.putInt("today_temp_min", today_temp_min);
                    lucia_editor.commit();
                    lucia_editor.putInt("today_humidity_max", today_humidity_max);
                    lucia_editor.commit();
                    lucia_editor.putInt("today_humidity_min", today_humidity_min);
                    lucia_editor.commit();
                    lucia_editor.putInt("tomorrow_temp_max", tomorrow_temp_max);
                    lucia_editor.commit();
                    lucia_editor.putInt("tomorrow_temp_min", tomorrow_temp_min);
                    lucia_editor.commit();
                    lucia_editor.putInt("tomorrow_humidity_max", tomorrow_humidity_max);
                    lucia_editor.commit();
                    lucia_editor.putInt("tomorrow_humidity_min", tomorrow_humidity_min);
                    lucia_editor.commit();

                    lucia_editor.putString("mixed_weather_array[11]", mixed_weather_array[11]);
                    lucia_editor.commit();



                    PackageManager packageManager = getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                    ComponentName componentName = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                    startActivity(mainIntent);
                    System.exit(0);

                    // 재부팅 넣어주면됨.
                }
                restart_handler.postDelayed(this, 1000);
            }
        }, 0);



    }

    private void front_hanlder() {

        Handler restart_handler = new Handler();
        restart_handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 핸들러 처리해주면됨.


                if(door_value ==0 && room_value ==0){
                    stt_off();
                    sleep_layout.setVisibility(View.VISIBLE);
                    //락밸류 초기화
                    lock_value=0;
                    min_1_timeout=0;
                    standby_mode = Constants.video_standby_sleep;
                }

                else if(door_value ==1 && room_value ==0){ // 당신은 현재 외부에서 들어오고있습니다.
                    sleep_layout.setVisibility(View.GONE);
                    //락밸류 동작
                    if(lock_value==0){
                        stt_on();
                        lock_value=1;
                        standby_mode = Constants.wake_up;

                        // 여기에 어서오세요 콧코로 넣고 뭘해야할지에 대한 영상 재생시키도록 함.
                        // idle 처리하고
                        int video_change_value = Constants.video_front_help;
                        player_change_video(video_change_value);

                        int audio_change_value = Constants.video_front_help;
                        player_change_audio(audio_change_value);
                    }
                    else{
                        System.out.println("lock 동작중(도어 접근됨)");
                    }


                }

                else if(door_value ==0 && room_value ==1){ // 당신은 현재 내부에서 나가려고 하고있습니다.
                    sleep_layout.setVisibility(View.GONE);
                    //락밸류 동작
                    if(lock_value==0){
                        stt_on();
                        lock_value=1;
                        standby_mode=Constants.wake_up;

                        // 여기에 엘리베이터 호출 물어보게 한다.
                        // idle 처리하고

                        int video_change_value = Constants.video_elevator_request;
                        player_change_video(video_change_value);

                        int audio_change_value = Constants.video_elevator_request;
                        player_change_audio(audio_change_value);
                    }
                    else{
                        System.out.println("lock 동작중(룸 접근됨)");
                    }


                }

                else if(door_value ==1 && room_value ==1){
                    stt_off();
                    sleep_layout.setVisibility(View.VISIBLE);
                    standby_mode=Constants.video_standby_sleep;
                    //락밸류 초기화

                    if(lock_value == 1) {
                        lock_value=0; // 1이었던거 일단 초기화 해야 핸들러 동시동작 막는다.
                        one_one_hanlder();
                    }

                    // 1 1 처리되었을때 외부 핸들러 작업을 통하여 5초카운트뒤 초기화 해야할듯..


                }

                if(lock_value ==1){ // 무언가의 오류로 1분동안 꼬였을경우 초기화 시킴.
                    min_1_timeout++;
                    if(min_1_timeout>=600){
                        door_value=0;
                        room_value=0;
                    }
                }



                // 101 100
                restart_handler.postDelayed(this, 100);
            }
        }, 0);



    }

    private void one_one_hanlder() {



        Handler one_one_hanlder = new Handler();
        one_one_hanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 핸들러 처리해주면됨.

                if(all_reset_value>5 ) {
                    all_reset_value=0;

                    //
                    lock_value=0;
                    min_1_timeout=0;
                    door_value=0;
                    room_value=0;
                    //

                    one_one_hanlder.removeMessages(0); // 현재 핸들러가 안멈추는 오류가 있음. 이거 확인해야함.
                    one_one_hanlder.removeCallbacksAndMessages(null);
                    return; // 메소드에 들어온 핸들러는 리턴 처리를 해줘야 멈춤 ㅅㅂ.......
                }

                all_reset_value++;
                // 101 100
                one_one_hanlder.postDelayed(this, 1000);
            }
        }, 0);



    }


}
