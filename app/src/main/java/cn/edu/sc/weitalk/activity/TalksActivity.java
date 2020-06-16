package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mbg.library.DefaultPositiveRefreshers.PositiveRefresherWithText;
import com.mbg.library.ISingleRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.adapter.TalksAdapter;
import cn.edu.sc.weitalk.fragment.TalksFragment;
import cn.edu.sc.weitalk.javabean.Message;
import cn.edu.sc.weitalk.javabean.Talks;

public class TalksActivity extends AppCompatActivity {
    private ArrayList list;
    private Talks talks;
    private String MyHeaderURL;
    private TalksAdapter talksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talks);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Intent intent = getIntent();
        talks = new Talks();
        MyHeaderURL=intent.getStringExtra("MyHeaderURL");
        talks.setTalksName(intent.getStringExtra("TalksName"));
        talks.setFriendHeaderURL(intent.getStringExtra("FriendHeaderURL"));

        TextView talks_name = findViewById(R.id.talks_name);
        talks_name.setText(talks.getTalksName());
        ImageView back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initMessageList();
        ListView talks_list = findViewById(R.id.talks_message_list);
        talksAdapter = new TalksAdapter(TalksActivity.this,list,talks.getFriendHeaderURL(),MyHeaderURL);
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
                Message message = new Message();
//                message.setName(talks.getUserName());
                message.setMsgText(editMessage.getText().toString());
                message.setMsgType(false);
//                message.setHeader_img(talks.getMyHeaderURL());
                message.setDate(format.format(date));
                list.add(message);
                editMessage.setText("");
                talksAdapter = new TalksAdapter(TalksActivity.this,list,talks.getFriendHeaderURL(),MyHeaderURL);
                talks_list.setAdapter(talksAdapter);
                setListViewHeightBasedOnChildren(talks_list);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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
        Message message;
        list=new ArrayList();
        for(int i=0;i<12;i++) {
            message = new Message();
            if(i%2==0){
//                message.setHeader_img(talks.getFriendHeaderURL());
                message.setMsgType(true);
                String m="";
                for(int j=0;j<=i;j++){
                    m += "这是收到的第"+(i/2+1)+"条消息，";
                }
                message.setMsgText(m);
            }else {
//                message.setHeader_img(talks.getMyHeaderURL());
                message.setMsgType(false);
                String m="";
                for(int j=0;j<=i;j++){
                    m += "这是发出的第"+(i/2+1)+"条消息，";
                }
                message.setMsgText(m);
            }
            list.add(message);
        }
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