package cn.edu.sc.weitalk.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFriendActivity extends BaseActivity {

    private final String TAG = "SearchFriendActivity";
    private final String IPaddress = "http://10.132.162.182:8081";
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

        SharedPreferences config = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        MyID = config.getString("userID", "");

        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchFriendActivity.this);
        adapter = new FriendFoundListAdapter(friendFoundList, MyID);
        rvFriendsFoundList.setLayoutManager(layoutManager);
        rvFriendsFoundList.setAdapter(adapter);

        setTextChangeListener();
    }

    /**
     * 在文本改变后，首先查询本地数据库是否包含好友（查找可查note，username，userID）
     * 然后将输入内容作为userID，去服务器查找好友
     */
    private void setTextChangeListener() {
        edtSearchFriend.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                findFriendLocally();
                searchFriendOnServer();
                //合并查找到的本地好友列表，和服务器查找到的列表
                if (personNotFriendList.size() != 0)
                    friendFoundList.add(personNotFriendList.get(0));
                adapter.setList(friendFoundList);
                adapter.notifyDataSetChanged();
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
        friendFoundList = DataSupport.select("*").where("MyID=? and (note like '%?%' or username like '%?%' or userID like '?%')", MyID, txt, txt, txt).find(Friend.class);
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
                String arg = "?";
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

    @OnClick({R.id.btn_back_search_friend, R.id.iv_scan_search_friend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back_search_friend:
                break;
            case R.id.iv_scan_search_friend:
                break;
        }
    }
}