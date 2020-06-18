package cn.edu.sc.weitalk.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.edu.sc.weitalk.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private String IPaddress="http://10.133.30.160:8081";

    private ImageButton btnLogin;
    private EditText edtUserID;
    private EditText edtPassword;
    private TextView btnRegister;
    private TextView btnForget;

    private RadioButton remember;
    private RadioButton autoLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin=findViewById(R.id.btnLogin);
        edtUserID=findViewById(R.id.edtLUserID);
        edtPassword=findViewById(R.id.edtLPassword);
        btnRegister=findViewById(R.id.txtRegister);
        btnForget=findViewById(R.id.txtForget);
        edtUserID=findViewById(R.id.edtLUserID);
        edtPassword=findViewById(R.id.edtLPassword);
        btnLogin=findViewById(R.id.btnLogin);

        //注册跳转
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_reg=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent_reg);
            }
        });
        //从注册页面跳转回来
        Intent intent=getIntent();
        if(intent!=null){
            String UserID=intent.getStringExtra("userID");
            edtUserID.setText(UserID);
        }else{
            //先查询本地登录信息，有的话直接主界面
            SharedPreferences prefs = getSharedPreferences("USER_INFO", MODE_PRIVATE); //获取对象，读取data文件
            boolean remember_xml=prefs.getBoolean("remember",false);
            boolean autoLogin_xml=prefs.getBoolean("autoLogin",false);
            String userID=prefs.getString("userID","");
            if(remember_xml){
                edtUserID.setText(prefs.getString("userID", ""));
                edtPassword.setText(prefs.getString("password", ""));
            }
            if(autoLogin_xml&&!userID.equals("")){
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        }

        //记住密码和自动登录单选框按钮响应
        remember=findViewById(R.id.remember);
        autoLogin=findViewById(R.id.autoLogin);
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();

                if(isChecked){
                    editor.putBoolean("remenber",true);
                    System.out.println("记住密码成功");
                }else{
                    editor.putBoolean("remenber",false);
                    System.out.println("记住密码失败");
                }
                editor.commit();
                editor.clear();
            }
        });
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();

                if(isChecked){
                    editor.putBoolean("autoLogin",true);
                    System.out.println("自动登录成功");
                }else{
                    editor.putBoolean("autoLogin",false);
                    System.out.println("自动登录失败");
                }
                editor.commit();
                editor.clear();
            }
        });

        //登录按钮 判断正确错误，获取用户信息，存入本地
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID=edtUserID.getText().toString();
                String password=edtPassword.getText().toString();
                if(TextUtils.isEmpty(userID)){
                    edtUserID.setError("不能为空");
                }else if(password.equals("")) {
                    edtPassword.setError("不能为空");
                }else{
                    System.out.println("useriD:------------------"+userID);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                OkHttpClient client=new OkHttpClient();
                                RequestBody requestBody=new FormBody.Builder()
                                        .add("userID",userID)
                                        .add("password",password)
                                        .build();
                                Request request=new Request.Builder()
                                        .url(IPaddress+"/post-api/login")
                                        .post(requestBody)
                                        .build();

                                Response response=client.newCall(request).execute();
                                final String responseData=response.body().string();
                                Gson gson=new Gson();
                                JSONObject jsonObject=new JSONObject(responseData);
                                String status=jsonObject.getString("status");
                                JSONObject data=jsonObject.getJSONObject("data");
                                if(status.equals("200")){
                                    //日期转换
                                    String birthday=data.getString("birthday");
                                    String registerTime=data.getString("registerTime");
                                    birthday = birthday.replace("Z", " UTC");//是空格+UTC
                                    registerTime = registerTime.replace("Z", " UTC");//是空格+UTC

                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    Date birthdayDate = df.parse(birthday);
                                    Date registerTimeDate=df.parse(registerTime);

                                    //个人信息写入配置文件
                                    SharedPreferences.Editor editor = getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
                                    editor.putString("userID",data.getString("userID"));
                                    editor.putString("password",data.getString("password"));
                                    editor.putString("name",data.getString("name"));
                                    editor.putString("headURL",data.getString("headURL"));
                                    editor.putString("birthday",birthdayDate.toString());
                                    editor.putString("location",data.getString("location"));
                                    editor.putString("phone",data.getString("phone"));
                                    editor.putString("eMail",data.getString("eMail"));
                                    editor.putString("registerTime",registerTimeDate.toString());
                                    editor.putString("lastTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                    editor.commit();
                                    editor.clear();


                                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    String msg=data.getString("msg");
                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        //忘记密码跳转
    }
}