package cn.edu.sc.weitalk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.edu.sc.weitalk.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private String IPaddress="http://localhost:8083";

    private ImageButton btnLogin;
    private EditText edtUserID;
    private EditText edtPassword;
    private TextView btnRegister;
    private TextView btnForget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin=findViewById(R.id.btnLogin);
        edtUserID=findViewById(R.id.edtLUserID);
        edtPassword=findViewById(R.id.edtLPassword);
        btnRegister=findViewById(R.id.txtRegister);

        Intent intent=getIntent();
        if(intent!=null){
            String UserID=intent.getStringExtra("userID");
            edtUserID.setText(UserID);
        }

        btnForget=findViewById(R.id.txtForget);
        edtUserID=findViewById(R.id.edtLUserID);
        edtPassword=findViewById(R.id.edtLPassword);
        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID=edtUserID.getText().toString();
                String password=edtPassword.getText().toString();
                if(userID==null){
                    edtUserID.setError("不能为空");
                }else if(password==null) {
                    edtPassword.setError("不能为空");
                }else{
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
                                if(status=="200"){
                                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                    intent.putExtra("userID",userID);
                                    startActivity(intent);
                                }else{
                                    JSONObject data=jsonObject.getJSONObject("data");
                                    String msg=data.getString("msg");
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        //先查询本地登录信息，有的话直接主界面

        //登录 判断正确错误，获取用户信息，存入本地

        //注册跳转
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_reg=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent_reg);
            }
        });


        //忘记密码跳转
    }
}