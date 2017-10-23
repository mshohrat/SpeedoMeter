package com.mshohrat.speedometerDemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mshohrat.speedometer.Speedometer;

public class MainActivity extends AppCompatActivity {

    Button increaseBtn;
    Button decreaseBtn;
    Speedometer speedometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        increaseBtn = (Button) findViewById(R.id.btn_inc);
        decreaseBtn = (Button) findViewById(R.id.btn_dec);
        speedometer = (Speedometer) findViewById(R.id.tv_speed);
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speedometer.speedTo(speedometer.getCurrentSpeed()+16);
            }
        });

        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speedometer.speedTo(speedometer.getCurrentSpeed()-16);
            }
        });
    }
}
