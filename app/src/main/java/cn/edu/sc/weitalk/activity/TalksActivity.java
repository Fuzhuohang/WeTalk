package cn.edu.sc.weitalk.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mbg.library.DefaultPositiveRefreshers.PositiveRefresherWithText;
import com.mbg.library.ISingleRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.adapter.TalksAdapter;
import cn.edu.sc.weitalk.adapter.TalksListAdapter;
import cn.edu.sc.weitalk.fragment.MessageListFragment;
import cn.edu.sc.weitalk.fragment.TalksFragment;
import cn.edu.sc.weitalk.javabean.Friend;
import cn.edu.sc.weitalk.javabean.Message;
import cn.edu.sc.weitalk.javabean.Talks;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TalksActivity extends AppCompatActivity {
    private String IPaddress="http://10.133.30.160:8081";
    private ArrayList<Message> list,showlist;
    private String talksName,FriendHeaderURL,FriendsID;
    private String MyHeaderURL,MyID,MyName;
    private TalksAdapter talksAdapter;
    private ListView talks_list;
    private Talks tTalk;
    private int count=0;
    private ScrollView scrollView;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talks);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Intent intent = getIntent();
//        MyHeaderURL=intent.getStringExtra("MyHeaderURL");
        FriendsID = intent.getStringExtra("FriendsID");
        talksName=intent.getStringExtra("TalksName");
        FriendHeaderURL=intent.getStringExtra("FriendHeaderURL");

        list = new ArrayList<Message>();

        List<Talks> Tl = DataSupport.select("*").where("FriendID=?",FriendsID).find(Talks.class);
        if(Tl.size()==1){
            tTalk = Tl.get(0);
        }else if(Tl.size()==0){
            Log.i("ERR","该联系人不存在！");
        }else {
            Log.i("ERR","该联系人不止一个！");
            tTalk = Tl.get(0);
        }

        SharedPreferences editor = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        MyID=editor.getString("userID",null);
        MyName = editor.getString("name",null);
        MyHeaderURL = editor.getString("headURL",null);

        TextView talks_name = findViewById(R.id.talks_name);
        talks_name.setText(talksName);
        ImageView back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initMessageList();
        talks_list = findViewById(R.id.talks_message_list);
        talksAdapter = new TalksAdapter(TalksActivity.this,list,FriendHeaderURL,MyHeaderURL);
        talks_list.setAdapter(talksAdapter);
        setListViewHeightBasedOnChildren(talks_list);

        scrollView = findViewById(R.id.talks_scroll);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        MultiAutoCompleteTextView editMessage = findViewById(R.id.editMessage);
//        EditText editMessage = findViewById(R.id.editMessage);
//        editMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus){
//                    scrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
//                    });
//                }
//            }
//        });
        Button btnSend = findViewById(R.id.sentMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String msgContent=editMessage.getText().toString();
                Message message = new Message();
                message.setMsgText(msgContent);
                message.setMsgType(false);
                message.setDate(date.getTime());
                message.setSendName(MyID);
                message.setReceiveName(FriendsID);
                list.add(message);
                editMessage.setText("");
                talksAdapter = new TalksAdapter(TalksActivity.this,list,FriendHeaderURL,MyHeaderURL);
                talks_list.setAdapter(talksAdapter);
                setListViewHeightBasedOnChildren(talks_list);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("senderID",MyID)
                                    .add("recipient",FriendsID)
                                    .add("content",msgContent)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(getString(R.string.IPAddress)+"/post-api/sendMessage")
                                    .post(requestBody)
                                    .build();

                            Response response = okHttpClient.newCall(request).execute();
                            String responseData = response.body().string();
                            Gson gson = new Gson();
                            JSONObject jsonObject = new JSONObject(responseData);
                            String status = jsonObject.getString("status");
                            if (status.equals("200")){
                                tTalk.setLastMessage(message.getMsgText());
                                tTalk.setLastMessageDate(message.getDate());
                                tTalk.updateAll("FriendID=? and MyID=?",tTalk.getFriendID(),MyID);
                                message.save();
                            }else {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String msg = data.getString("msg");
                                Toast.makeText(TalksActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(TalksActivity.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TalksActivity.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        });

        RefreshRelativeLayout refresh_talks_message = findViewById(R.id.refresh_talks_message);
        refresh_talks_message.setPositiveRefresher(new PositiveRefresherWithText(true));
        refresh_talks_message.setPositiveEnable(true);
        refresh_talks_message.setNegativeEnable(false);
        refresh_talks_message.setPositiveOverlayUsed(true);
        refresh_talks_message.setPositiveDragEnable(true);

        refresh_talks_message.addPositiveRefreshListener(new ISingleRefreshListener() {
            @Override
            public void onRefresh() {
//                refresh();
//                talksAdapter = new TalksAdapter(TalksActivity.this,showlist,FriendHeaderURL,MyHeaderURL);
//                talks_list.setAdapter(talksAdapter);
//                setListViewHeightBasedOnChildren(talks_list);
                refresh_talks_message.positiveRefreshComplete();
            }
        });

        RefreshReceiver receiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.edu.sc.weitalk.activity.talks");
        registerReceiver(receiver,intentFilter);
//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                boolean ifRefresh = intent.getExtras().getBoolean("ifrefresh");
//                if(ifRefresh){
//                    initMessageList();
//                    talksAdapter = new TalksAdapter(TalksActivity.this,showlist,FriendHeaderURL,MyHeaderURL);
//                    talks_list.setAdapter(talksAdapter);
//                    setListViewHeightBasedOnChildren(talks_list);
//                }
//            }
//        };
//        broadcastManager.registerReceiver(broadcastReceiver,intentFilter);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private void initMessageList(){
//        DataSupport.deleteAll(Message.class);
//        for(int i=0;i<12;i++) {
//            Message message = new Message();
//            if(i%2==0){
////                message.setHeader_img(talks.getFriendHeaderURL());
//                message.setMsgType(true);
//                String m="";
//                for(int j=0;j<=i;j++){
//                    m += "这是收到的第"+(i/2+1)+"条消息，";
//                }
//                message.setMsgText(m);
//                message.setSendName(talksName);
//                message.setReceiveName(MyID);
//            }else {
////                message.setHeader_img(talks.getMyHeaderURL());
//                message.setMsgType(false);
//                String m="";
//                for(int j=0;j<=i;j++){
//                    m += "这是发出的第"+(i/2+1)+"条消息，";
//                }
//                message.setMsgText(m);
//                message.setSendName(MyID);
//                message.setReceiveName(talksName);
//            }
//            message.save();
//        }
//        list = DataSupport.findAll(Message.class);
        list = (ArrayList<Message>)DataSupport.select("*").where("(sendID=? and receiveID=?) or (receiveID=? and sendID=?)",FriendsID,MyID,FriendsID,MyID).order("date").find(Message.class);
//        showlist = new ArrayList<>();
//        Collections.reverse(list);
//        if(list.size()<10){
//            showlist.addAll(list.subList(count,list.size()));
//            count=list.size();
//        }else {
//            showlist.addAll(list.subList(count,count+10));
//            count+=10;
//        }
//        Collections.reverse(showlist);
    }

    private void refresh(){
        Collections.reverse(showlist);
        if((list.size()-count)==0){
            Toast.makeText(TalksActivity.this,"没有更多的消息了！",Toast.LENGTH_LONG).show();
        }else if((list.size()-count)<10){
            Toast.makeText(TalksActivity.this,"刷新成功！",Toast.LENGTH_LONG).show();
            showlist.addAll(list.subList(count,list.size()));
            count=list.size();
        }else{
            Toast.makeText(TalksActivity.this,"刷新成功！",Toast.LENGTH_LONG).show();
            showlist.addAll(list.subList(count,count+10));
            count+=10;
        }
        Collections.reverse(showlist);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter==null){
            return;
        }
        int totalHeight = 0;
        for(int i=0;i<listAdapter.getCount();i++){
            View listItem=listAdapter.getView(i,null,listView);
            listItem.measure(0,0);
            totalHeight+=listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight+(listView.getDividerHeight()*(listAdapter.getCount()-1));
        listView.setLayoutParams(params);
    }

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean ifRefresh = intent.getExtras().getBoolean("ifrefresh");
            if(ifRefresh){
                initMessageList();
                talksAdapter = new TalksAdapter(TalksActivity.this,list,FriendHeaderURL,MyHeaderURL);
                talks_list.setAdapter(talksAdapter);
//                talksAdapter.data=list;
//                talksAdapter.notifyDataSetChanged();
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                setListViewHeightBasedOnChildren(talks_list);
            }
        }
    }




}