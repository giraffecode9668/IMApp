package com.giraffe.imapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.giraffe.imapp.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationViewEx bnve = findViewById(R.id.bnve);
        bnve.enableAnimation(true);
        bnve.enableShiftingMode(false);
    }
}
