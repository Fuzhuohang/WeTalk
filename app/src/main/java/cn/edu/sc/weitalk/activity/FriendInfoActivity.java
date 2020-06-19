package cn.edu.sc.weitalk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.Friend;
import cn.edu.sc.weitalk.javabean.Talks;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FriendInfoActivity extends BaseActivity {

    private Friend friend;
    private boolean isYourself = false;
    private String HeaderUrl;
    private String MyID;
    String friendId;

    @BindView(R.id.btn_add_friend)
    Button btnAddFriend;
    @BindView(R.id.head_ic_friend_info)
    SimpleDraweeView headIcFriendInfo;
    @BindView(R.id.iv_back_friend_info)
    ImageView ivBackFriendInfo;
    @BindView(R.id.tv_mask_back_friend_info)
    TextView tvMaskBackFriendInfo;
    @BindView(R.id.tv_nick_friend_info)
    TextView tvNickFriendInfo;
    @BindView(R.id.tv_id_nick_friend_info)
    TextView tvIdNickFriendInfo;
    @BindView(R.id.iv_status_friend_info)
    ImageView ivStatusFriendInfo;
    @BindView(R.id.tv_note_friend_info)
    TextView tvNoteFriendInfo;
    @BindView(R.id.tv_nickname_friend_info)
    TextView tvNicknameFriendInfo;
    @BindView(R.id.tv_location_friend_info)
    TextView tvLocationFriendInfo;
    @BindView(R.id.tv_birthday_friend_info)
    TextView tvBirthdayFriendInfo;
    @BindView(R.id.tv_phone_friend_info)
    TextView tvPhoneFriendInfo;
    @BindView(R.id.tv_email_friend_info)
    TextView tvEmailFriendInfo;
    @BindView(R.id.btn_send_msg_friend_info)
    Button btnSendMsgFriendInfo;
    @BindView(R.id.tv_title_friend_info)
    TextView tvTitleFriendInfo;
    @BindView(R.id.btn_back_friend_info)
    ImageView btnBackFriendInfo;
    @BindView(R.id.tv_modify_friend_info)
    TextView tvModifyFriendInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(FriendInfoActivity.this);
        setContentView(R.layout.activity_friend_info);
        ButterKnife.bind(this);

        SharedPreferences editor = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        MyID=editor.getString("userID",null);
        //从intent读取好友的id
        Intent intent = getIntent();
        friendId = intent.getStringExtra("id");

        List<Friend> list = DataSupport.select("*").where("UserID=? and MyID=?",friendId,MyID).find(Friend.class);
        if(list.size()==0){
            Toast.makeText(FriendInfoActivity.this,"该好友不存在！！！",Toast.LENGTH_SHORT).show();
            finish();
        }else {
            friend = list.get(0);
        }
       if(friend.getLocation().length()==0||friend.getLocation()==null
                ||friend.getPhoneNum().length()==0||friend.getPhoneNum()==null
                ||friend.getBirthday().length()==0||friend.getBirthday()==null
                ||friend.getEmail().length()==0||friend.getEmail()==null){
            getFriendInfo();
        }

        SetUI();


        HeaderUrl="res://drawable/" + R.drawable.tu;

        headIcFriendInfo.setImageURI(HeaderUrl);
    }

    private void SetUI(){
        headIcFriendInfo.setImageURI(friend.getImg());
        if(friend.getNote()!=null||friend.getNote().length()!=0){
            tvNickFriendInfo.setText(friend.getNote());
        }else {
            tvNickFriendInfo.setText(friend.getUsername());
        }
        tvIdNickFriendInfo.setText(friend.getUserID());
        tvNicknameFriendInfo.setText(friend.getUsername());
        tvNoteFriendInfo.setText(friend.getNote());
        tvLocationFriendInfo.setText(friend.getLocation());
        tvBirthdayFriendInfo.setText(friend.getBirthday());
        tvPhoneFriendInfo.setText(friend.getPhoneNum());
        tvEmailFriendInfo.setText(friend.getEmail());
        if (friend.isStatus()){
            ivStatusFriendInfo.setImageDrawable(getResources().getDrawable(R.drawable.green_point));
        }else {
            ivStatusFriendInfo.setImageDrawable(getResources().getDrawable(R.drawable.search_btn));
        }
    }

    private void getFriendInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String info = "?userID="+friendId;
                    Request request = new Request.Builder()
                            .url(R.string.IPAddress+"/get-api/SearchFriend"+info)
                            .build();
                    Response response = null;
                    response = okHttpClient.newCall(request).execute();
                    final String responseData = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");

                    if (status.equals("200")){
                        JSONObject jsonData = jsonObject.getJSONObject("data");

                        friend.setPhoneNum(jsonData.getString("phone"));
                        friend.setLocation(jsonData.getString("location"));
                        friend.setEmail(jsonData.getString("eMail"));
                        friend.setBirthday(jsonData.getString("birthday"));
                        friend.updateAll("userid=? and MyID=?",friendId,MyID);
                    }else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String msg = data.getString("msg");
                        Log.e("GETFRIEND",msg);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @OnClick({R.id.btn_back_friend_info, R.id.btn_send_msg_friend_info, R.id.tv_modify_friend_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back_friend_info:
                finish();
                break;
            /**
             * 点击按钮发消息
             */
            case R.id.btn_send_msg_friend_info:
                String id = tvIdNickFriendInfo.getText().toString();
                Log.i("SendMessage",id);
                Talks talks;
                List<Talks> list = DataSupport.select("*").where("FriendID=?",id).find(Talks.class);
                if (list.size()==0){
                    talks = new Talks();
                    if (tvNoteFriendInfo.getText().toString().length()!=0){
                        talks.setTalksName(tvNoteFriendInfo.getText().toString());
                    }else {
                        talks.setTalksName(tvNickFriendInfo.getText().toString());
                    }
                    talks.setFriendID(id);
                    talks.setMyID(MyID);
                    talks.setFriendHeaderURL(HeaderUrl);
                    talks.setUnReadNum(0);
                    talks.save();
                }else {
                    talks = list.get(0);
                }

                Intent intent = new Intent(FriendInfoActivity.this, TalksActivity.class);
                intent.putExtra("FriendsID",talks.getFriendID());
                intent.putExtra("TalksName",talks.getTalksName());
                intent.putExtra("FriendHeaderURL",talks.getFriendHeaderURL());
                startActivity(intent);
                finish();
                break;
            case R.id.tv_modify_friend_info:
                PopupMenu popupMenu = new PopupMenu(FriendInfoActivity.this,tvModifyFriendInfo);

                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.friend_info_setting,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.modify_note:
                                View modifyView = LayoutInflater.from(FriendInfoActivity.this).inflate(R.layout.modify_friend_note,null);
                                EditText editNote = modifyView.findViewById(R.id.editNote);
                                Button commitbtn = modifyView.findViewById(R.id.commitEdit);
                                editNote.setText(friend.getNote());
                                AlertDialog dialog = new AlertDialog.Builder(FriendInfoActivity.this).setView(modifyView).create();
                                commitbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String note = editNote.getText().toString();
                                        if(!friend.getNote().equals(note)){
                                            modifyNote(note);
                                        }
                                        dialog.cancel();
                                    }
                                });
                                dialog.show();
                                break;
                            case R.id.delete_f:
                                AlertDialog dialog1 = new AlertDialog.Builder(FriendInfoActivity.this)
                                        .setTitle("删除好友")
                                        .setMessage("你确定要删除该好友吗？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteFriend(MyID,friendId);
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).create();
                                dialog1.show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                break;
        }
    }

    private void deleteFriend(String senderID,String friendID){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("senderID",senderID)
                            .add("friendID",friendID)
                            .build();
                    Request request = new Request.Builder()
                            .url(R.string.IPAddress+"/post-api/delFriend")
                            .post(requestBody)
                            .build();

                    Response response = okHttpClient.newCall(request).execute();
                    final String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status.equals("200")){
                        DataSupport.deleteAll(Friend.class,"userID=? and MyID=?",friendID,senderID);
                    }else{
                        JSONObject data = jsonObject.getJSONObject("data");
                        String msg = data.getString("msg");
                        Log.e("DELFRIEND",msg);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void modifyNote(String note){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userID",MyID)
                            .add("friendID",friendId)
                            .add("note",note)
                            .build();
                    Request request = new Request.Builder()
                            .url(R.string.IPAddress+"/post-api/changeNote")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    final String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status.equals("200")){
                        friend.setNote(note);
                        friend.updateAll("userid=? and MyID=?",friendId,MyID);
                        tvNoteFriendInfo.setText(note);
                    }else{
                        JSONObject data = jsonObject.getJSONObject("data");
                        String msg = data.getString("msg");
                        Log.e("GETFRIEND",msg);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @OnClick(R.id.btn_add_friend)
    public void onViewClicked() {

    }
}