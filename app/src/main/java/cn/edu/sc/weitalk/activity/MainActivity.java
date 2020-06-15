package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;

import cn.edu.sc.weitalk.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

    }
}