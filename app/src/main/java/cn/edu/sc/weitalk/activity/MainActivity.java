package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.fragment.MainFragment;
import cn.edu.sc.weitalk.javabean.Comments;
import cn.edu.sc.weitalk.javabean.MomentsMessage;
import cn.edu.sc.weitalk.service.MainService;

public class MainActivity extends BaseActivity {

    BottomNavigationView bottomView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        DataSupport.deleteAll(MomentsMessage.class);
//        SharedPreferences config=getSharedPreferences("USER_INFO", MODE_PRIVATE);
//        SharedPreferences.Editor editor = config.edit();
//        editor.putString("userID","testid");
//        editor.putString("password","testpasssword");
//        editor.putString("name","testname");
//        editor.putString("headURL","res://drawable/" + R.drawable.dragon);
//        editor.putString("birthday","testbirth");
//        editor.putString("location","testlocat");
//        editor.putString("phone","testphone");
//        editor.putString("eMail","testemail");
//        editor.putString("registerTime","testtime");
////        editor.putString("lastTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        editor.commit();
//        editor.clear();
        Intent intent = new Intent(MainActivity.this, MainService.class);
        bindService(intent,mainConn,BIND_AUTO_CREATE);
        Connector.getDatabase();
        requestMyPermissions();
        bottomView = findViewById(R.id.bottom_view);
        bottomView.setItemIconTintList(null);
//        MainFragment mainFragment = new MainFragment(config);
    }

    ServiceConnection mainConn = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainService.MainBinder binder = (MainService.MainBinder)service;
            MainService mainService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void requestMyPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            //Log.d(TAG, "requestMyPermissions: 有写SD权限");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            //Log.d(TAG, "requestMyPermissions: 有读SD权限");
        }
    }
}