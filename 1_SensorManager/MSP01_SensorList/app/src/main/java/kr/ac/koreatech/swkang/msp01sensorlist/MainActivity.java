package kr.ac.koreatech.swkang.msp01sensorlist;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String list = "";
        // SensorManager 객체를 getSystemService 메소드를 통해 얻음
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        // 모든 타입의 센서 목록을 얻음
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);

        list += "전체 센서 수: " + sensors.size() + "\n";
        int i = 0;
        for(Sensor s: sensors) {
            list += "" + i++ + " name: " + s.getName() + "\n" + "power: " + s.getPower() + "\n"
                    + "resolution: " + s.getResolution() + "\n" + "range: " + s.getMaximumRange() + "\n"
                    + "vendor: " + s.getVendor() + "\n" + "min delay: " + s.getMinDelay() + "\n\n";
        }

        TextView text = (TextView)findViewById(R.id.text);
        // TextView에 텍스트 내용이 화면 크기를 넘어서 들어갈 때 스크롤 가능하게 만들기 위한 메소드 호출
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(list);
    }
}
