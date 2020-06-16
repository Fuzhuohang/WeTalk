package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import cn.edu.sc.weitalk.R;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        bottomView = findViewById(R.id.bottom_view);
        bottomView.setItemIconTintList(null);



    }
}