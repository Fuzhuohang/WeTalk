package cn.edu.sc.weitalk.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.textclassifier.TextLinks;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.edu.sc.weitalk.activity.TalksActivity;
import cn.edu.sc.weitalk.javabean.Message;
import cn.edu.sc.weitalk.javabean.Talks;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainService extends Service {
    private String IPaddress="http://localhost:8081";
    private String UserID;

    public MainService() {
    }

    class GetMessageThread extends Thread{
        public void run(){
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            while (true){
                try{
                    Thread.sleep(1000);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("recipient",UserID)
                            .add("Time",time)
                            .build();
                    Request request = new Request.Builder()
                            .url(IPaddress+"/get-api/getMessage")
                            .post(requestBody)
                            .build();
                    time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());;
                    Response response = okHttpClient.newCall(request).execute();
                    final String responseData = response.body().string();
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status=="200"){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            cn.edu.sc.weitalk.javabean.Message message = new Message();
                            message.setReceiveName(UserID);
                            message.setSendName(jsonData.getString("Sender"));
                            message.setDate(jsonData.getString("time"));
                            message.setMsgText(jsonData.getString("content"));
                            message.setMsgType(true);
                            message.setRead(false);
                            message.save();
                            Talks talks;
                            List<Talks> list = DataSupport.select("*").where("FriendID=?",jsonData.getString("Sender")).find(Talks.class);
                            if (list.size()==0){
                                talks = new Talks();
//                                talks.setTalksName(tvNickFriendInfo.getText().toString());
                                talks.setFriendID(jsonData.getString("Sender"));
//                                talks.setFriendHeaderURL();
                                talks.setUnReadNum(0);
                                talks.save();
                            }else {
                                talks = list.get(0);
                            }
                            talks.setLastMessage(jsonData.getString("content"));
//                            talks.setLastMessageDate(jsonData.getString("time"));
                            talks.setUnReadNum(talks.getUnReadNum()+1);
                            talks.save();
                        }

                    }else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String msg = data.getString("msg");
                        Toast.makeText(MainService.this,msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainService.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainService.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        new Thread(new GetMessageThread()).start();
        return new MainBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MainBinder extends Binder{
        public MainService getService(){
            return MainService.this;
        }
    }
}
