package kr.ac.koreatech.swkang.msp02orientationsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManger;
    private Sensor mOrientation;
    TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManger = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManger.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mText = (TextView)findViewById(R.id.text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // SensorEventListener 등록
        mSensorManger.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // SensorEventListener 해제
        mSensorManger.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            mText.setText("방향 센서값\n\n방위각: " + event.values[0]
                    + "\n피치: " + event.values[1] + "\n롤: " + event.values[2]);
        }
    }
}

