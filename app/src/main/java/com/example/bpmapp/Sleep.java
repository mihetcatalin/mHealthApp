package com.example.bpmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.TimeZoneFormat;
import android.os.Bundle;
import android.widget.TextView;

import java.awt.font.TextAttribute;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

/*This activity performs addition to the current time in order to display in the textBoxes the suggested wake-up time to the user*/

public class Sleep extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        TextView cycle1 = findViewById(R.id.cycle1);
        TextView cycle2 = findViewById(R.id.cycle2);
        TextView cycle3 = findViewById(R.id.cycle3);
        TextView cycle4 = findViewById(R.id.cycle4);
        TextView cycle5 = findViewById(R.id.cycle5);
        TextView cycle6 = findViewById(R.id.cycle6);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, 15);
        Date afterFall = cal.getTime();
        cal.setTime(afterFall);

        cal.add(Calendar.MINUTE, 90);
        Date d1 = cal.getTime();
        cycle1.setText(df.format(d1));

        cal.add(Calendar.MINUTE, 90);
        Date d2 = cal.getTime();
        cycle2.setText(df.format(d2));

        cal.add(Calendar.MINUTE, 90);
        Date d3 = cal.getTime();
        cycle3.setText(df.format(d3));

        cal.add(Calendar.MINUTE, 90);
        Date d4 = cal.getTime();
        cycle4.setText(df.format(d4));

        cal.add(Calendar.MINUTE, 90);
        Date d5 = cal.getTime();
        cycle5.setText(df.format(d5));

        cal.add(Calendar.MINUTE, 90);
        Date d6 = cal.getTime();
        cycle6.setText(df.format(d6));

    }
}
