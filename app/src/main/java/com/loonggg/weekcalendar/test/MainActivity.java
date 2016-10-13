package com.loonggg.weekcalendar.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loonggg.weekcalendar.view.WeekCalendar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Toolbar mToolbar;
    TextView mTvTitle;//标题
    WeekCalendar weekCalendar;//自定义日历控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weekCalendar = (WeekCalendar) findViewById(R.id.mc_calendar);
        List<String> list = new ArrayList<>();
        list.add("2016-09-13");
        list.add("2016-10-13");
        list.add("2016-10-11");
        list.add("2016-10-10");
        list.add("2016-10-16");
        weekCalendar.setSelectDates(list);
        //设置日历点击事件
        weekCalendar.setOnDateClickListener(new WeekCalendar.OnDateClickListener() {
            @Override
            public void onDateClick(View view, int position) {
                Toast.makeText(MainActivity.this, weekCalendar.getTheDayOfSelected(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
