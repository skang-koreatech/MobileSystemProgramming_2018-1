package kr.ac.koreatech.swkang.msp10wifiscantimer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer mTimer = new Timer();
    TimerTask m1000msCountTimerTask = null;

    //***********************************
    // wake lock을 사용하는 경우 필요한 코드
    PowerManager pm;
    PowerManager.WakeLock wl;
    //***********************************
    TextView scanResultText;

    WifiManager wifiManager;
    List<ScanResult> scanResultList;

    boolean isPermitted = false;
    final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
                getWifiInfo();
        }
    };

    private void getWifiInfo() {
        scanResultList = wifiManager.getScanResults();
        scanResultText.setText("===================================\n");
        for(int i = 0; i < scanResultList.size(); i++) {
            ScanResult result = scanResultList.get(i);
            scanResultText.append((i+1) + "- SSID: " + result.SSID + "\t RSSI: " + result.level + " dBm\n");
        }
        scanResultText.append("===================================");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestRuntimePermission();

        //**************************************************************
        // wake lock을 사용하는 경우 필요한 코드
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Tag: partial wake lock");
        wl.acquire();
        //**************************************************************

        scanResultText = (TextView)findViewById(R.id.result);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        if(wifiManager.isWifiEnabled() == false)
            wifiManager.setWifiEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
    }

    public void onClick(View view) {
        if(view.getId() == R.id.start) {
            //Toast.makeText(this, "WiFi scan start!!", Toast.LENGTH_LONG).show();
            startTimerTask();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 타이머는 작업 스레드이기 때문에 액티비티가 종료될 때
        // 반드시 중단하여 스레드를 제거시키도록 한다
        mTimer.cancel();

        //***********************************
        // wake lock을 사용하는 경우 필요한 코드
        wl.release();
        //***********************************
    }

    private void startTimerTask() {
        // 1. TimerTask 실행 중이라면 중단한다
        stopTimerTask();

        // 2. TimerTask 객체를 생성한다
        // 1000 밀리초마다 카운팅 되는 태스크를 등록한다
        m1000msCountTimerTask = new TimerTask() {
            @Override
            public void run() {
                wifiManager.startScan();
                //scanResultText.setText("");

            }
        };

        // 3. TimerTask를 Timer를 통해 실행시킨다
        // 1초 후에 타이머를 구동하고 5000 밀리초 단위로 반복한다
        // 	schedule(TimerTask task, long delay, long period)
        mTimer.schedule(m1000msCountTimerTask, 1000, 5000);
        //*** Timer 클래스 메소드 이용법 참고 ***//
        // http://developer.android.com/intl/ko/reference/java/util/Timer.html
        //***********************************//
    }

    private void stopTimerTask() {
        // 1. 모든 태스크를 중단한다
        if(m1000msCountTimerTask != null) {
            m1000msCountTimerTask.cancel();
            m1000msCountTimerTask = null;
        }
    }

    private void requestRuntimePermission() {
        //*******************************************************************
        // Runtime permission check
        //*******************************************************************
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
        } else {
            // ACCESS_FINE_LOCATION 권한이 있는 것
            isPermitted = true;
        }
        //*********************************************************************
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // read_external_storage-related task you need to do.

                    // ACCESS_FINE_LOCATION 권한을 얻음
                    isPermitted = true;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // 권한을 얻지 못 하였으므로 location 요청 작업을 수행할 수 없다
                    // 적절히 대처한다
                    isPermitted = false;

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
