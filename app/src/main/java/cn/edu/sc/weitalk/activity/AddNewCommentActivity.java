package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.sc.weitalk.javabean.FileUtils;
import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.MomentsMessage;

public class AddNewCommentActivity extends BaseActivity {
    private EditText editView;
    private Context context;
    private ConstraintLayout constraintLayout;
    private ImageView returnImage;
    private TextView btnCommit;
    private ImageView selectedImage;
    private ImageView selectedImage2;
    private ImageView selectedImage3;
    //private byte[] image;
    private Uri imageUri;
    private String imagePath = " ";
    private String imagePath2 = " ";
    private String imagePath3 = " ";

    int imageCounter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_comment);
        editView=findViewById(R.id.editTxt);
        returnImage=findViewById(R.id.returnimageview);
        constraintLayout=findViewById(R.id.addimageinmoment);
        btnCommit=findViewById(R.id.buttoncommit);
        selectedImage=findViewById(R.id.selectedimage);
        selectedImage2=findViewById(R.id.selectedimage2);
        selectedImage3=findViewById(R.id.selectedimage3);
        context=AddNewCommentActivity.this;
        editView.setFocusable(true);
        editView.requestFocus();
        imageUri=null;
        imagePath = " ";
        imagePath2 = " ";
        imagePath3 = " ";
        imageCounter=0;
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
//                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 1);
                if(imageCounter<3)
                    showFileChooser();
                else
                    Toast.makeText(AddNewCommentActivity.this,"最多只能添加三张照片哦！",Toast.LENGTH_SHORT).show();
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
                            intent.putExtra("imagePath",imagePath);
                            intent.putExtra("imagePath2",imagePath2);
                            intent.putExtra("imagePath3",imagePath3);
                            intent.putExtra("imageNumber",imageCounter);
                            Date time=new Date();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            intent.putExtra("time",format.format(time));
                            // 读取本地配置文件，获得用户头像，id
//                            SharedPreferences config= getSharedPreferences("USER_INFO",MODE_PRIVATE);
//                            intent.putExtra("myID",config.getString("userID",""));
//                            intent.putExtra("myName",config.getString("name",""));
//                            intent.putExtra("myHead",config.getString("headURL",""));
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
                switch(imageCounter){
                    case 0:
                        imagePath = FileUtils.getFilePathByUri(getApplicationContext(),imageUri);
                        selectedImage.setImageURI(Uri.parse(imagePath));
                    break;
                    case 1:
                        imagePath2 = FileUtils.getFilePathByUri(getApplicationContext(),imageUri);
                        selectedImage2.setImageURI(Uri.parse(imagePath2));
                    break;
                    case 2:
                        imagePath3 = FileUtils.getFilePathByUri(getApplicationContext(),imageUri);
                        selectedImage3.setImageURI(Uri.parse(imagePath3));
                        break;
                }
                selectedImage.setImageURI(imageUri);
                imageCounter++;
        }

    }

    private static final int FILE_SELECT_CODE = 200;
    //文件选择器
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }


}