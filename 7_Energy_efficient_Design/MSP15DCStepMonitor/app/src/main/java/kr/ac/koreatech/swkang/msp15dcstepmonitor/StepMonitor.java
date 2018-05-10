package kr.ac.koreatech.swkang.msp15dcstepmonitor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepMonitor implements SensorEventListener {

    private Context context;
    private SensorManager mSensorManager;
    private Sensor mLinear;

    public StepMonitor(Context context) {
        this.context = context;

        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void onStart() {
        // SensorEventListener 등록
        if (mLinear != null) {
            Log.d("DC_STEP_MONITOR", "Register Accel Listener!");
            mSensorManager.registerListener(this, mLinear, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onStop() {
        // SensorEventListener 등록 해제
        if (mSensorManager != null) {
            Log.d("DC_STEP_MONITOR", "Unregister Accel Listener!");
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // 센서 데이터가 업데이트 되면 호출
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            //***** sensor data collection *****//
            // event.values 배열의 사본을 만들어서 values 배열에 저장
            float[] values = event.values.clone();

            // rms calculation
            computeRms(values);
        }
    }

    private void computeRms(float[] values) {
        // 현재 업데이트 된 accelerometer x, y, z 축 값의 Root Mean Square 값 계산
        double rms = Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);

        Log.d("DC_STEP_MONITOR", "rms: " + rms);

        Intent intent = new Intent("kr.ac.koreatech.msp.dcstepmonitor");
        intent.putExtra("rms", rms);
        // broadcast 전송
        context.sendBroadcast(intent);
    }
}
