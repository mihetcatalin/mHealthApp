package com.example.bpmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
/*This activity allows the user to turn on/off the fall detector with a siple switch*/
public class Fall extends AppCompatActivity {

    private Switch onOff;
    private TextView text1;
    private TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall);
        text1 = findViewById(R.id.title);
        text2 = findViewById(R.id.description);
        onOff = findViewById(R.id.onOff);


        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startService();
                } else{
                    stopService();
                }
            }
        });

    }

    private void startService() {
        Intent serviceIntent = new Intent(this, FallService.class);
        startService(serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, FallService.class);
        stopService(serviceIntent);
    }
}
