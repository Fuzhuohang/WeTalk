package cn.edu.sc.weitalk.activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.os.IBinder;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.util.List;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.Friend;
import cn.edu.sc.weitalk.javabean.MomentsMessage;
import cn.edu.sc.weitalk.service.MainService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private final String TAG = "MainActivity";
    private BottomNavigationView bottomView;    //底部导航栏
    private String userId;
    private final String IPaddress="http://10.132.162.182:8081";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences config=getSharedPreferences("USER_INFO", MODE_PRIVATE);
        SharedPreferences.Editor editor = config.edit();
        editor.putString("userID","100000");
//        editor.putString("password","testpasssword");
//        editor.putString("name","testname");
//        editor.putString("headURL","res://drawable/" + R.drawable.dragon);
//        editor.putString("birthday","testbirth");
//        editor.putString("location","testlocat");
//        editor.putString("phone","testphone");
//        editor.putString("eMail","testemail");
//        editor.putString("registerTime","testtime");
//        editor.putString("lastTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        editor.commit();
        editor.clear();

        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
//        DataSupport.deleteAll(Talks.class);
//        DataSupport.deleteAll(Message.class);
        DataSupport.deleteAll(MomentsMessage.class);
        Intent intent = new Intent(MainActivity.this, MainService.class);
        //bindService(intent,mainConn,BIND_AUTO_CREATE);
        Connector.getDatabase();
        requestMyPermissions();
        bottomView = findViewById(R.id.bottom_view);
        bottomView.setItemIconTintList(null);

        //获取userId，让后获取服务器的好友列表，存入本地数据库
        /*SharedPreferences*/ config = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        userId = config.getString("userID", "");
        if(!userId.isEmpty()){
            Log.i(TAG, userId);
            updateFriendDatabase();
        }else
            Log.i(TAG, "没有获得userId");

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


    /**
     * 从服务器获取好友列表数据，存入本地数据库
     *      **为了区分不同用户的好友，Friend有一个MyID字段，添加数据时注意添加MyID
     *      **只存用户id，用户名，头像，备注名，用户状态，当在个人资料界面展示好友信息时，再通过服务器查找好友来获取好友其他信息
     */
    private void updateFriendDatabase(){
        //DataSupport.deleteAll(Friend.class);
        new Thread(new Runnable(){
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                String a = "?userID=" + userId;
                Request request = new Request.Builder()
                        .url(IPaddress + "/get-api/getFriendList" + a)
                        .build();
                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if(status.equals("200")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.i(TAG, "获得好友列表，好友数量：" + jsonArray.length());
                        for(int i = 0;i < jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            Friend friend = new Friend();
                            friend.setMyID(userId);
                            friend.setUserID(object.getString("userID"));
                            //先查找是否有这位好友，如果有，update；如果没有，存新建的friend
                            List<Friend> friend1 = DataSupport.select("*", "MyID=? and userID=?", userId, object.getString("userID")).find(Friend.class);
                            Log.i(TAG, "friend1.size = " + friend1.size());
                            if( friend1.size() != 0){
                                ContentValues values = new ContentValues();
                                values.put("img", IPaddress + object.getString("headURL"));
                                values.put("note", object.getString("note"));
                                values.put("username", object.getString("name"));
                                values.put("status", Boolean.parseBoolean(object.getString("status")));
                                DataSupport.updateAll(Friend.class, values, "MyID=? and userID=?", userId, object.getString("userID"));
                            }
                            else {
                                friend.setImg(IPaddress + object.getString("headURL"));
                                friend.setNote(object.getString("note"));
                                friend.setUsername(object.getString("name"));
                                friend.setStatus(Boolean.parseBoolean(object.getString("status")));
                                friend.save();
                                Log.i(TAG, friend.isSaved()?"friend saved":"friend not saved");
                            }
                        }
                    }
                    else if(status.equals("404")){
                        /**********404**********/
                        Log.i(TAG, "获得好友列表404");
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "Json异常");
                    e.printStackTrace();
                } catch (IOException e){
                    Log.i(TAG, "其他异常");
                    e.printStackTrace();
                }
            }
        }).start();
    }


}