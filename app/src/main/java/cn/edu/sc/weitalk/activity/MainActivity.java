package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.Comments;
import cn.edu.sc.weitalk.javabean.MomentsMessage;

public class MainActivity extends BaseActivity {

    BottomNavigationView bottomView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        //DataSupport.deleteAll(MomentsMessage.class);
        Connector.getDatabase();
        bottomView = findViewById(R.id.bottom_view);
        bottomView.setItemIconTintList(null);
    }
}