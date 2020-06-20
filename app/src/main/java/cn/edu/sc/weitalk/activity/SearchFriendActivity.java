package cn.edu.sc.weitalk.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.adapter.FriendFoundListAdapter;
import cn.edu.sc.weitalk.javabean.Friend;
import cn.edu.sc.weitalk.util.Constant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFriendActivity extends BaseActivity {

    private final String TAG = "SearchFriendActivity";
    private final String IPaddress = "http://10.132.162.182:8081";
    private String searchID;
    @BindView(R.id.iv_scan_search_friend)
    ImageView ivScanSearchFriend;
    @BindView(R.id.tv_tip_search_friend)
    TextView tvTipSearchFriend;
    private String MyID;
    @BindView(R.id.btn_back_search_friend)
    ImageView btnBackSearchFriend;
    @BindView(R.id.tv_search_search_friend)
    TextView tvSearchSearchFriend;
    @BindView(R.id.edt_search_friend)
    EditText edtSearchFriend;
    @BindView(R.id.rv_friends_found_list)
    RecyclerView rvFriendsFoundList;
    FriendFoundListAdapter adapter;
    List<Friend> friendFoundList = new ArrayList<Friend>();
    List<Friend> personNotFriendList = new ArrayList<Friend>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        searchID = intent.getStringExtra("searchID");
        edtSearchFriend.setText(searchID);

        if(searchID.length()!=0){
            searchFriend();
        }

        SharedPreferences config = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        MyID = config.getString("userID", "");

        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchFriendActivity.this);
        adapter = new FriendFoundListAdapter(this, friendFoundList, MyID);
        rvFriendsFoundList.setLayoutManager(layoutManager);
        rvFriendsFoundList.setAdapter(adapter);

        setTextChangeListener();
    }

    /**
     * 在文本改变后，首先查询本地数据库是否包含好友（查找可查note，username，userID）
     * 然后将输入内容作为userID，去服务器查找好友
     */
    private void setTextChangeListener() {
        searchFriend();
    }

    private void searchFriend(){
        edtSearchFriend.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                friendFoundList.clear();
                personNotFriendList.clear();
                findFriendLocally();
                if(friendFoundList.size() == 0)
                    searchFriendOnServer();
                //如果friendFoundList大小为0，设置提示信息可见
                if(friendFoundList.size() == 0)
                    tvTipSearchFriend.setVisibility(View.VISIBLE);
                else
                    tvTipSearchFriend.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 根据输入框输入，查找本地好友（进行note，username，userID三项查找）
     * 然后存入friendFoundList
     */
    private void findFriendLocally() {
        String txt = edtSearchFriend.getText().toString();
        if (txt.length() != 0)
            friendFoundList = DataSupport.select("*").where("MyID=? and (note like ? or username like ? or userID like ?)", MyID, txt + "%", txt + "%", txt + "%").find(Friend.class);
        adapter.setList(friendFoundList);
        adapter.notifyDataSetChanged();
    }

    /**
     * 根据输入，作为userID，向服务器查询用户
     * 查询到的结果存入Friend时，MyID设为"-1"，以示非我的好友
     */
    private void searchFriendOnServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                String arg = "?userID=" + edtSearchFriend.getText().toString();
                Request request = new Request.Builder()
                        .url(IPaddress + "/get-api/searchFriend" + arg)
                        .build();
                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status.equals("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Friend friend = new Friend();
                            friend.setMyID("-1");
                            friend.setUserID(object.getString("userID"));
                            friend.setUsername(object.getString("name"));
                            friend.setImg(object.getString("headURL"));
                            friend.setPhoneNum(object.getString("phone"));
                            friend.setLocation(object.getString("location"));
                            personNotFriendList.add(friend);
                            //合并查找到的本地好友列表，和服务器查找到的列表
                            if (personNotFriendList.size() != 0) {
                                friendFoundList.add(personNotFriendList.get(0));
                                Log.i(TAG, "服务器查找到了" + personNotFriendList.size() +"个好友");
                                rvFriendsFoundList.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setList(friendFoundList);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                            //如果friendFoundList大小为0，设置提示信息可见
                            tvTipSearchFriend.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(friendFoundList.size() == 0)
                                        tvTipSearchFriend.setVisibility(View.VISIBLE);
                                    else
                                        tvTipSearchFriend.setVisibility(View.GONE);
                                }
                            });
                        }
                    } else if (status.equals("404")) {
                        Log.i(TAG, "查询用户404");
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "查询好友，Json异常");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i(TAG, "查询好友，其他异常");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void scanQRCode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "请到权限中心打开相机访问权限", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }

        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_PERM_CAMERA);
    }

    @OnClick({R.id.btn_back_search_friend, R.id.iv_scan_search_friend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back_search_friend:
                finish();
                break;
            case R.id.iv_scan_search_friend:
                Log.i("SCANQRCODE","点击扫一扫");
                scanQRCode();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQ_PERM_CAMERA && resultCode == RESULT_OK){
            Log.i("SCANQRCODE","scanqrcode");
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            Log.i("SCANQRCODE","扫描结果是："+scanResult);
            String QRCodeMessage = scanResult;
            String[] s = QRCodeMessage.split(" ");
            String[] t = QRCodeMessage.split(" ");
            Log.i("SCANQRCODE", s[s.length-1]+ t[t.length-1]);
            searchID = s[s.length-1];
            Intent intent = new Intent(SearchFriendActivity.this, FriendInfoActivity.class);
            intent.putExtra("id",s[s.length-1]);
            startActivity(intent);
            edtSearchFriend.setText(searchID);
        }
    }
}