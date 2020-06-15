package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mbg.library.DefaultPositiveRefreshers.PositiveRefresherWithText;
import com.mbg.library.ISingleRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.fragment.TalksFragment;
import cn.edu.sc.weitalk.javabean.Talks;

public class TalksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talks);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Intent intent = getIntent();
        Talks talks = new Talks();
        talks.setMyHeaderURL(intent.getStringExtra("MyHeaderURL"));
        talks.setTalksName(intent.getStringExtra("TalksName"));
        talks.setFriendHeaderURL(intent.getStringExtra("FriendHeaderURL"));
        talks.setMessage(intent.getStringExtra("Message"));

        TextView talks_name = findViewById(R.id.talks_name);
        talks_name.setText(talks.getTalksName());
        ImageView back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
}