package cn.edu.sc.weitalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.Friend;

public class FriendInfoActivity extends BaseActivity {

    private Friend friend;
    private boolean isYourself = false;

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
        setContentView(R.layout.activity_friend_info);
        ButterKnife.bind(this);

        //从intent读取好友的id
        Intent intent = getIntent();
        String friendId = intent.getStringExtra("id");

        headIcFriendInfo.setImageURI("res://drawable/" + R.drawable.tu);
    }

    @OnClick({R.id.btn_back_friend_info, R.id.btn_send_msg_friend_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back_friend_info:
                finish();
                break;
            /**
             * 点击按钮发消息
             */
            case R.id.btn_send_msg_friend_info:

                break;
        }
    }

    @OnClick(R.id.btn_add_friend)
    public void onViewClicked() {

    }
}