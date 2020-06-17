package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
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
import java.util.Date;
import java.util.List;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.adapter.TalksAdapter;
import cn.edu.sc.weitalk.fragment.TalksFragment;
import cn.edu.sc.weitalk.javabean.Message;
import cn.edu.sc.weitalk.javabean.Talks;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TalksActivity extends AppCompatActivity {
    private String IPaddress="http://localhost:8081";
    private List<Message> list;
    private String MyHeaderURL,talksName,FriendHeaderURL;
    private String MyID = "123456";
    private TalksAdapter talksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talks);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Intent intent = getIntent();
//        MyHeaderURL=intent.getStringExtra("MyHeaderURL");
        talksName=intent.getStringExtra("TalksName");
        FriendHeaderURL=intent.getStringExtra("FriendHeaderURL");

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
        ListView talks_list = findViewById(R.id.talks_message_list);
        talksAdapter = new TalksAdapter(TalksActivity.this,list,FriendHeaderURL,MyHeaderURL);
        talks_list.setAdapter(talksAdapter);
        setListViewHeightBasedOnChildren(talks_list);

        ScrollView scrollView = findViewById(R.id.talks_scroll);
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
                message.setDate(format.format(date));
                message.setSendName(MyID);
                message.setReceiveName(talksName);
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
                                    .add("recipient",talksName)
                                    .add("content",msgContent)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(IPaddress+"/post-api/sendMessage")
                                    .post(requestBody)
                                    .build();

                            Response response = okHttpClient.newCall(request).execute();
                            String responseData = response.body().string();
                            Gson gson = new Gson();
                            JSONObject jsonObject = new JSONObject(responseData);
                            String status = jsonObject.getString("status");
                            if (status=="200"){
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
                });
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
                Toast.makeText(TalksActivity.this,"刷新成功！",Toast.LENGTH_LONG).show();
                refresh_talks_message.positiveRefreshComplete();
            }
        });

    }

    private void initMessageList(){
        DataSupport.deleteAll(Message.class);
        for(int i=0;i<12;i++) {
            Message message = new Message();
            if(i%2==0){
//                message.setHeader_img(talks.getFriendHeaderURL());
                message.setMsgType(true);
                String m="";
                for(int j=0;j<=i;j++){
                    m += "这是收到的第"+(i/2+1)+"条消息，";
                }
                message.setMsgText(m);
                message.setSendName(talksName);
                message.setReceiveName(MyID);
            }else {
//                message.setHeader_img(talks.getMyHeaderURL());
                message.setMsgType(false);
                String m="";
                for(int j=0;j<=i;j++){
                    m += "这是发出的第"+(i/2+1)+"条消息，";
                }
                message.setMsgText(m);
                message.setSendName(MyID);
                message.setReceiveName(talksName);
            }
            message.save();
        }
        list = DataSupport.findAll(Message.class);
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
}