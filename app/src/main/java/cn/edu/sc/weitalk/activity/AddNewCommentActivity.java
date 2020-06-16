package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.MomentsMessage;

public class AddNewCommentActivity extends BaseActivity {
    private EditText editView;
    private Context context;
    private ConstraintLayout constraintLayout;
    private ImageView returnImage;
    private TextView btnCommit;
    private ImageView selectedImage;
    //private byte[] image;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_comment);
        editView=findViewById(R.id.editTxt);
        returnImage=findViewById(R.id.returnimageview);
        constraintLayout=findViewById(R.id.addimageinmoment);
        btnCommit=findViewById(R.id.buttoncommit);
        selectedImage=findViewById(R.id.selectedimage);
        context=AddNewCommentActivity.this;
        editView.setFocusable(true);
        editView.requestFocus();
        imageUri=null;
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
        new Thread(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                while(true) {
                    if (TextUtils.isEmpty(editView.getText())) {
                        //editView.setError("不能发表空的朋友圈哦！");
                        btnCommit.setBackgroundResource(R.drawable.fabiaoqian);
                        btnCommit.setTextColor(context.getResources().getColor(R.color.colorFaBiao));

                    } else {
                        btnCommit.setTextColor(context.getResources().getColor(R.color.white));
                        btnCommit.setBackgroundResource(R.drawable.fabiaoqianhou);
                        //btnCommit.setTextSt
                    }
                }
            }
        }).start();
                btnCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(editView.getText())){
                            editView.setError("不能发表空的朋友圈哦！");


                        }
                        else{
                            Intent intent=new Intent();
                            intent.putExtra("content",editView.getText().toString());
                            if(imageUri!=null) {
                                intent.putExtra("uri",imageUri.toString());
                            }
                            Date time=new Date();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            intent.putExtra("time",format.format(time));

                            setResult(200,intent);
                            finish();
                        }
                    }
                });


    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data) {

            //判断事件完成，就是选择完图片

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

                imageUri = data.getData();
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                selectedImage.setImageURI(imageUri);
                //图像转为字节流
                //image = MomentsMessage.img(bitmap);
//


            // }

        }

    }

}