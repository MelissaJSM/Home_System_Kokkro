package com.example.auto_door;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.OkHttpClient;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.Request;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.Response;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.ResponseBody;
import java.util.List;
import java.util.Vector;


public class DoorService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";


    BluetoothAdapter mBluetoothAdapter;

    BluetoothLeScanner mBluetoothLeScanner;

    BluetoothLeAdvertiser mBluetoothLeAdvertiser;


    ScanSettings.Builder mScanSettings;

    List<ScanFilter> scanFilters;

    List<ScanFilter> scanFilters_in;


    double array[] = new double[5];

    double resultdata;

    double resultdata_in;

    double result_distance = 100.0;

    int distance_count = 0;

    Handler check_bluetooth_out = new Handler();

    int check_bluetooth_out_count = 0;

    Handler check_bluetooth_in = new Handler();

    int check_bluetooth_in_count = 0;

    int result_count_in = 1;


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("아루지사마! 현관문 자동 열기 시스템이 시작되었어요!").setContentText(input).setSmallIcon(R.mipmap.ic_launcher_foreground).setContentIntent(pendingIntent).build();
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();


        // 여기에서 필요한 작업을 해주면 된다.
        ////////////////////////////////////////////////////////////////////////////////////////////////////


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mScanSettings = new ScanSettings.Builder();
        mScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        ScanSettings scanSettings = mScanSettings.build();

        scanFilters = new Vector<>();
        ScanFilter.Builder scanFilter = new ScanFilter.Builder();
        scanFilter.setDeviceAddress("D4:36:39:A6:38:D1"); //ex) 00:00:00:00:00:00  D4:36:39:A6:38:D1 : 외부
        ScanFilter scan = scanFilter.build();
        scanFilters.add(scan);
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);


        scanFilters_in = new Vector<>();
        ScanFilter.Builder scanFilters_in_a = new ScanFilter.Builder();
        scanFilters_in_a.setDeviceAddress("D4:36:39:A6:68:AC"); //ex) 00:00:00:00:00:00  D4:36:39:A6:68:AC : 내부
        ScanFilter scan_in = scanFilters_in_a.build();
        scanFilters_in.add(scan_in);
        mBluetoothLeScanner.startScan(scanFilters_in, scanSettings, mScanCallback_in);


        //////////////////////////////////////이부분이 http 링크로 전송하는 영역/////////////////////////

        //System.out.println("핸들러 들어오는거 맞아?");


        ////////////////////////////////////////////////////////////////////////////////////////////////////


        return START_NOT_STICKY;
    }


    ScanCallback mScanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            try {
                //ScanRecord scanRecord = result.getScanRecord();
                //// 이 로그 파일이 결과값 표시하는 데이터.
                //TX 파워도 출력하니 공식에 써먹을 수 있다.
                //rssi 가 100넘게 튀는 이슈가있으니 참고바람.
                //Log.d("getTxPowerLevel()", scanRecord.getTxPowerLevel() + "");
                //Log.d("onScanResult()", result.getDevice().getAddress() + "\n" + result.getRssi() + "\n" + result.getDevice().getName() + "\n" + result.getDevice().getBondState() + "\n" + result.getDevice().getType());

                double rssi_data = result.getRssi();

                //System.out.println("rssi_data 값 : " + rssi_data);
                int txpower_data = -59;
                //beaconTextView1.setText(Double.toString(Math.pow(10.0, ((-63 - rssi_data) / (10 * 2))))); // 이걸 메시지 송신해야함


                if (rssi_data == 0) {
                    //  Log.d("거리 계산 결과", "-1.0");
                }
                double ratio = rssi_data * 1.0 / txpower_data;
                //System.out.println("ratio 값 : " + ratio);
                if (ratio < 1.0) {

                    double mathpoe = Math.pow(ratio, 10);
                    //System.out.println("math pow 결과 : " + mathpoe);
                    resultdata = mathpoe;
                    //beaconTextView1.setText(Double.toString(mathpoe));
                }
                else {
                    double distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
                    // System.out.println("최종 거리 결과 : " + distance);
                    //beaconTextView1.setText(Double.toString(distance));
                    resultdata = distance;
                }


                if (resultdata < 3.0 && resultdata > 0.0) {
                    array[distance_count] = resultdata;
                    distance_count++;
                }

                if (distance_count >= 3) {
                    distance_count = 0;


                    /*
                    Arrays.sort(array); // 배열 정렬
                    for (int z = 0; z < array.length; z++) {
                        System.out.println(array[z]);
                    }
                    */
                    if ((array[1] + array[2] + array[3]) / 3 <= 2.0) {
                        result_distance = (array[1] + array[2] + array[3]) / 3;

                        array[1] = 0;
                        array[2] = 0;
                        array[3] = 0;

                        // 여기에 거리 됐으니 처리하는 영역 넣으면됨.

                        System.out.println("result_distance 값 : " + result_distance);


                        if (check_bluetooth_out_count == 0 && result_count_in == 1) {
                            network_request();
                            network_request_in();
                        }
                        //네트워크 요청 하기 위한 조건
                        // 1. 내부 네트워크가 잡히고 있지 않아야함.
                        // 2. 외부 네트워크에 연결 시도 후 30분 쿨타임을 무조건 가져야함.

                    }
                    //원래 이렇게 평균값 처리 안해도되는데... 수정하기 귀찮았어.

                    //beaconTextView1.setText(Double.toString(result_distance));

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d("onBatchScanResults", results.size() + "");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("onScanFailed()", errorCode + "");
        }

    };

    ScanCallback mScanCallback_in = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            try {
                System.out.println("내부 블루투스 연결확인");
                ScanRecord scanRecord = result.getScanRecord();
                //// 이 로그 파일이 결과값 표시하는 데이터.
                //TX 파워도 출력하니 공식에 써먹을 수 있다.
                //rssi 가 100넘게 튀는 이슈가있으니 참고바람.
                Log.d("getTxPowerLevel()", scanRecord.getTxPowerLevel() + "");
                Log.d("onScanResult()", result.getDevice().getAddress() + "\n" + result.getRssi() + "\n" + result.getDevice().getName() + "\n" + result.getDevice().getBondState() + "\n" + result.getDevice().getType());

                double rssi_data = result.getRssi();

                System.out.println("rssi_data 값 : " + rssi_data);
                int txpower_data = -59;
                //beaconTextView_in.setText(Double.toString(Math.pow(10.0, ((-63 - rssi_data) / (10 * 2))))); // 이걸 메시지 송신해야함

                if (rssi_data == 0) {
                    Log.d("거리 계산 결과", "-1.0");
                }
                double ratio = rssi_data * 1.0 / txpower_data;
                System.out.println("ratio 값 : " + ratio);
                if (ratio < 1.0) {
                    double mathpoe = Math.pow(ratio, 10);
                    System.out.println("math pow 결과 : " + mathpoe);
                    resultdata_in = mathpoe;
                    //beaconTextView1.setText(Double.toString(mathpoe));
                }
                else {
                    double distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
                    System.out.println("최종 거리 결과 : " + distance);
                    //beaconTextView1.setText(Double.toString(distance));
                    resultdata_in = distance;
                    //차단변수
                }


                if (resultdata_in <= 20) {
                    //여기에 처리 다된 영억 넣으면 됨.

                    check_bluetooth_in_count = 0;

                    check_bluetooth_in.removeMessages(0); //계속 초기화가 필요함.
                    //check_bluetooth_in.removeCallbacksAndMessages(null);

                    check_bluetooth_in.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //내부 블루투스 감지 확인 (30초)
                            if (check_bluetooth_in_count >= 30) { // 값이 감지가안되면 result_count_in을 1으로 돌리려는 시도를 한다.
                                //중지
                                check_bluetooth_in_count = 0;


                                    // 여기에 나갔을때 가정한 시나리오 넣으면 됨.
                                    // 어차피 30초이후에 핸들러 멈춤...
                                network_request_out();




                                result_count_in = 1; // 네트워크 진입에 쓰일예정
                                check_bluetooth_in.removeMessages(0); // 현재 핸들러가 안멈추는 오류가 있음. 이거 확인해야함.
                                check_bluetooth_in.removeCallbacksAndMessages(null);
                                return; // 메소드에 들어온 핸들러는 리턴 처리를 해줘야 멈춤 ㅅㅂ.......
                                // 변수 하나 필요.

                            }
                            check_bluetooth_in_count++;
                            System.out.println("check_bluetooth_in_count 의 값 측정 : " + check_bluetooth_in_count);

                            result_count_in = 0;


                            check_bluetooth_in.postDelayed(this, 1000);
                        }


                    }, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d("onBatchScanResults", results.size() + "");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("onScanFailed()", errorCode + "");
        }

    };


    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothLeScanner.stopScan(mScanCallback);
        mBluetoothLeScanner.stopScan(mScanCallback_in);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher_foreground);
        builder.setContentTitle("도어 오픈 알림!");
        builder.setContentText("아루지사마! 자동으로 비밀번호를 해제했어요!");
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }

    private void createNotification_out() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher_foreground);
        builder.setContentTitle("외출 감지!");
        builder.setContentText("아루지사마! 자동으로 외출모드 진입했어요!");
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }

    private void network_request() {
        if (result_distance > 0.0 && result_distance <= 1.5) {
            // 여기에 링크 삽입.
            result_distance = 100;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("스레드 들어오는거 맞아?");


                    try {
                        String url = "https://maker.ifttt.com/trigger/door_open/json/with/key/키를 입력하세요.";


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
                                createNotification();
                                //Toast.makeText(DoorService.this, "문앞입니다. 문을 열겠습니다.", Toast.LENGTH_SHORT).show();


                                //외부 블루투스 감지 후 문연뒤 동작 대기 30초
                                check_bluetooth_out.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (check_bluetooth_out_count >= 30) {
                                            check_bluetooth_out_count = 0;
                                            check_bluetooth_out.removeMessages(0); // 현재 핸들러가 안멈추는 오류가 있음. 이거 확인해야함.
                                            check_bluetooth_out.removeCallbacksAndMessages(null);
                                            return; // 메소드에 들어온 핸들러는 리턴 처리를 해줘야 멈춤 ㅅㅂ.......
                                            // 작동 중지

                                        }
                                        check_bluetooth_out_count++;
                                        System.out.println("check_bluetooth_out_count 의 값 측정 : " + check_bluetooth_out_count);
                                        check_bluetooth_out.postDelayed(this, 1000);
                                    }


                                }, 0);

                            }
                        }
                        else
                            System.err.println("Error Occurred");
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Toast.makeText(DoorService.this, "에러 발생.", Toast.LENGTH_SHORT).show();
                    }
                }


            }).start();


            System.out.println("주소입력창에 진입!");

        }
    }

    private void network_request_in() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("스레드 들어오는거 맞아?");
                    try {
                        String url = "https://maker.ifttt.com/trigger/myroom_in/json/with/key/키를 입력하세요.";


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
                                //Toast.makeText(DoorService.this, "문앞입니다. 문을 열겠습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                            System.err.println("Error Occurred");
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Toast.makeText(DoorService.this, "에러 발생.", Toast.LENGTH_SHORT).show();
                    }
                }


            }).start();


            System.out.println("주소입력창에 진입!");


    }

    private void network_request_out() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("스레드 들어오는거 맞아?");
                try {
                    String url = "https://maker.ifttt.com/trigger/myroom_out/json/with/key/키를 입력하세요.";


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
                            createNotification_out();
                            //Toast.makeText(DoorService.this, "문앞입니다. 문을 열겠습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        System.err.println("Error Occurred");
                } catch (Exception e) {
                    e.printStackTrace();
                    // Toast.makeText(DoorService.this, "에러 발생.", Toast.LENGTH_SHORT).show();
                }
            }


        }).start();


        System.out.println("주소입력창에 진입!");


    }

}