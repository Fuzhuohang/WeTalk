package cn.edu.sc.weitalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class TestCircle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.nav_header_main);
        SimpleDraweeView temp=findViewById(R.id.drawee_img);
        temp.setImageURI("res://drawable/" + R.drawable.dragon);
    }

}