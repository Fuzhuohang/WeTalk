package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.Talks;

public class UserInfoActivity extends AppCompatActivity {

    private SharedPreferences config;

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
    @BindView(R.id.tv_2_friend_info)
    TextView tv2FriendInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(UserInfoActivity.this);
        setContentView(R.layout.activity_friend_info);

        ButterKnife.bind(this);

        config=getSharedPreferences("USER_INFO", MODE_PRIVATE);
        tv2FriendInfo.setText("姓    名：");
        tv2FriendInfo.setVisibility(View.GONE);
        setUI();

    }

    private void setUI(){
        headIcFriendInfo.setImageURI(config.getString("headURL",""));
        tvNickFriendInfo.setText(config.getString("name",""));
        tvIdNickFriendInfo.setText(config.getString("userID",""));
        tvNicknameFriendInfo.setText(config.getString("name",""));
        //tvNoteFriendInfo.setText(config.getString("realName",""));
        tvNoteFriendInfo.setVisibility(View.GONE);
        tvLocationFriendInfo.setText(config.getString("location",""));
        tvBirthdayFriendInfo.setText(config.getString("birthday",""));
        tvPhoneFriendInfo.setText(config.getString("phone",""));
        tvEmailFriendInfo.setText(config.getString("eMail",""));
        ivStatusFriendInfo.setImageDrawable(getResources().getDrawable(R.drawable.green_point));
        btnAddFriend.setVisibility(View.GONE);
        btnSendMsgFriendInfo.setVisibility(View.GONE);
    }

    @OnClick({R.id.btn_back_friend_info, R.id.tv_modify_friend_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back_friend_info:
                finish();
                break;
            case R.id.tv_modify_friend_info:

                break;
        }
    }
}