package com.helloworld.jingle.huaweiweatherview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private HuaWeiWeatherView huaWeiWeatherView;
    private android.widget.LinearLayout activitymain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activitymain = (LinearLayout) findViewById(R.id.activity_main);
        this.huaWeiWeatherView = (HuaWeiWeatherView) findViewById(R.id.huaWeiWeatherView);
        huaWeiWeatherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                huaWeiWeatherView.chageAngle(200f);
            }
        });
        huaWeiWeatherView.setOnAngleColorListener(new HuaWeiWeatherView.OnAngleColorListener() {
            @Override
            public void colorListener(int red, int green) {
                Color color = new Color();
                int backgroundColor = color.argb(50,red,green,0);
                activitymain.setBackgroundColor(backgroundColor);
            }
        });
    }
}
