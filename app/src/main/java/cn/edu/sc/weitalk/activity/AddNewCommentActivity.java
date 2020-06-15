package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.sc.weitalk.R;

public class AddNewCommentActivity extends AppCompatActivity {
    private EditText editView;
    private Context context;
    private ConstraintLayout constraintLayout;
    private ImageView returnImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_comment);
        editView=findViewById(R.id.editTxt);
        returnImage=findViewById(R.id.returnimageview);
        constraintLayout=findViewById(R.id.addimageinmoment);
        context=AddNewCommentActivity.this;
        editView.setFocusable(true);
        editView.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }, 200);//这里的时间大概是自己测试的
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, 1);
            }
        });
        returnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data) {

            //判断事件完成，就是选择完图片

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            Uri uri = data.getData();

//文件指针

            Cursor cursor = this.getContentResolver().query(uri, null, null,

                    null, null);

            cursor.moveToFirst();

            String path = cursor.getString(1);

            //path就是用户选择文件的路径啦，至于参数为什么是1，这是我尝试的经验，拿到路径后你就可以调用那张图片显示给用户看或者做别的事


            // }

        }

    }

}