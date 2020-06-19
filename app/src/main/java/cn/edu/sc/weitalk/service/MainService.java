package cn.edu.sc.weitalk.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.textclassifier.TextLinks;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.activity.TalksActivity;
import cn.edu.sc.weitalk.javabean.FriendReqRes;
import cn.edu.sc.weitalk.javabean.Comments;
import cn.edu.sc.weitalk.javabean.Friend;
import cn.edu.sc.weitalk.javabean.Message;
import cn.edu.sc.weitalk.javabean.MomentsMessage;
import cn.edu.sc.weitalk.javabean.Talks;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainService extends Service {
    private final String IPaddress="http://10.132.162.182:8081";
    private String UserID;
    private String LastDate;
//服务构造函数，从sharedpreference获取用户基本信息（ID）和上次结束时的时间

    public MainService() {

    }

    class GetMessageThread extends Thread{
        public void run(){
            String time = LastDate;
            while (true){
                try{
                    Thread.sleep(5000);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String info="?recipient="+UserID+"&time="+time;
                    Request request = new Request.Builder()
                            .url(getString(R.string.IPAddress)+"/get-api/getMessage"+info)
                            .build();
                    time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());;
                    Response response = okHttpClient.newCall(request).execute();
                    final String responseData = response.body().string();
                    Log.i("GETMESSAGE",responseData);
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status.equals("200")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            cn.edu.sc.weitalk.javabean.Message message = new Message();
                            message.setReceiveName(UserID);
                            message.setSendName(jsonData.getString("sender"));
                            message.setDate(jsonData.getString("time"));
                            message.setMsgText(jsonData.getString("content"));
                            message.setMsgType(true);
                            message.setRead(false);
                            message.save();
                            Talks talks;
                            List<Talks> list = DataSupport.select("*").where("FriendID=?",jsonData.getString("sender")).find(Talks.class);
                            if (list.size()==0){
                                talks = new Talks();
//                                talks.setTalksName(tvNickFriendInfo.getText().toString());
                                talks.setFriendID(jsonData.getString("sender"));
//                                talks.setFriendHeaderURL();
                                List<Friend> fl = DataSupport.select("*").where("userId=?",jsonData.getString("sender")).find(Friend.class);
                                if(fl.size()!=0){
                                    Friend friend = fl.get(0);
                                    if(friend.getNote().length()!=0){
                                        talks.setTalksName(friend.getNote());
                                    }else {
                                        talks.setTalksName(friend.getUsername());
                                    }
                                }else {
                                    talks.setTalksName(jsonData.getString("sender"));
                                }
                                talks.setMyID(UserID);
                                talks.setUnReadNum(0);
                                talks.save();
                            }else {
                                talks = list.get(0);
                            }
                            talks.setLastMessage(jsonData.getString("content"));
                            talks.setLastMessageDate(jsonData.getString("time"));
                            talks.setUnReadNum(talks.getUnReadNum()+1);
                            talks.updateAll("FriendID=? and MyID=?",jsonData.getString("sender"),UserID);
                        }
                        BroadCastMethod(true,"cn.edu.sc.weitalk.fragment.message");
                        BroadCastMethod(true,"cn.edu.sc.weitalk.activity.talks");
                    }else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String msg = data.getString("msg");
                        //Toast.makeText(MainService.this,msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainService.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainService.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

//监听朋友圈消息与评论
    class GetMomentsThread extends Thread{
        public void run(){
            String time = LastDate;
            while (true){
                try{
                    Thread.sleep(1000);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String info="?recipient="+UserID+"&time="+time;
                    Request request = new Request.Builder()
                            .url(getString(R.string.IPAddress)+"/get-api/getShare"+info)
                            .build();
                    time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    //等待回复
                    Response response = okHttpClient.newCall(request).execute();
                    final String responseData = response.body().string();
                    Log.i("GETMOMENTS",responseData);
                    //获取数据
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    //判断状态是否正常
                    if (status.equals("200")){
                        //，写入两个json数组中
                        JSONArray jsonDataArray = jsonObject.getJSONArray("data");
                        for(int i=0;i<jsonDataArray.length();i++) {
                            JSONObject jsonData = jsonDataArray.getJSONObject(i);
                            String sharedID=jsonData.getString("shareID");
                            cn.edu.sc.weitalk.javabean.MomentsMessage momentsMessage = new MomentsMessage();
                            List<MomentsMessage> list=DataSupport.select("*").where("MomentID=?",sharedID).find(MomentsMessage.class);
                            if(list.isEmpty()) {
                                momentsMessage.setMomentID(sharedID);
                                momentsMessage.setPublisherID(jsonData.getString("senderID"));
                                //momentsMessage.setPublisherName(jsonData.getString("time"));
                                momentsMessage.setContent(jsonData.getString("content"));
                                momentsMessage.setDate(jsonData.getString("time"));
                                momentsMessage.setLikeCounter(jsonData.getInt("likeNum"));
                                momentsMessage.setPublisherName(jsonData.getString("sendername"));
                                momentsMessage.setMomentImage(jsonData.getString("imgURL"));
                                momentsMessage.save();
                            }
                            else{
                                momentsMessage=list.get(0);
                                momentsMessage.setLikeCounter(jsonData.getInt("likeNum"));
                                momentsMessage.setMomentImage(jsonData.getString("imgURL"));
                                momentsMessage.updateAll("MomentID=?",sharedID);
                            }
                            JSONArray jsonCommentsArray = jsonData.getJSONArray("comment");
                            for(int j=0;j<jsonCommentsArray.length();j++) {
                                JSONObject jsonComments = jsonCommentsArray.getJSONObject(j);
                                if(DataSupport.select("*").where("senderID=? and content=?",jsonComments.getString("senderID"),jsonComments.getString("content")).find(Comments.class).size()==0) {
                                    Comments comments = new Comments();
                                    comments.setMomentID(sharedID);
                                    comments.setContent(jsonComments.getString("content"));
                                    comments.setCommentPerName(jsonComments.getString("sendername"));
                                    comments.setCommentPerID(jsonComments.getString("senderID"));
                                    comments.save();
                                }
                            }
                        }
                        BroadCastMethod(true,"cn.edu.sc.weitalk.fragment.moment");
                    }else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String msg = data.getString("msg");
                        //Toast.makeText(MainService.this,msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainService.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainService.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean BroadCastMethod(boolean st, String ReceiveAction){
        Intent intentReceiver = new Intent();
        intentReceiver.setAction(ReceiveAction);

        intentReceiver.putExtra("ifrefresh",st);

        MainService.this.sendBroadcast(intentReceiver);

        return true;
    }


    class FriendThread extends Thread{
        public void run(){
            while (true){
                try {
                    /******************************
                     *  1. 收到对方想要添加好友的消息
                     ******************************/
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String arg = "?userID=" + UserID;
//                    RequestBody requestBody = new FormBody.Builder()
//                            .add("userId",UserID)
//                            .build();
                    Request request = new Request.Builder()
                            .url(IPaddress + "/get-api/getRAddFriend" + arg)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if(status.equals("200")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i = 0;i < jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            FriendReqRes newFriendReq = new FriendReqRes();
                            newFriendReq.setType(FriendReqRes.NEW_FRIEND_REQUEST);
                            newFriendReq.setUserId(object.getString("userID"));
                            newFriendReq.setUsername(object.getString("name"));
                            newFriendReq.setHeadUrl(object.getString("headURL"));
                            newFriendReq.setMyID(UserID);
                            newFriendReq.save();
                        }
                    }
                    /***********************************
                     *  2. 收到想要添加对方为好友的回复消息
                     ***********************************/
                    okHttpClient = new OkHttpClient();
                    request = new Request.Builder()
                            .url(IPaddress + "/get-api/getSAddFriend" + arg)
                            .build();
                    response = okHttpClient.newCall(request).execute();
                    responseData = response.body().string();
                    gson = new Gson();
                    jsonObject = new JSONObject(responseData);
                    status = jsonObject.getString("status");
                    if(status.equals("200")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i = 0;i < jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            FriendReqRes friendRes = new FriendReqRes();
                            friendRes.setType(FriendReqRes.NEW_FRIEND_RESPONSE);
                            friendRes.setUserId(object.getString("userID"));
                            friendRes.setUsername(object.getString("name"));
                            friendRes.setHeadUrl(object.getString("headURL"));
                            friendRes.setAgreed(object.getBoolean("agree"));
                            friendRes.setMyID(UserID);
                            friendRes.save();
                        }
                    }

                    /******************************
                     *  3. 收到对方已删除你好友的消息
                     ******************************/
                    okHttpClient = new OkHttpClient();
                    request = new Request.Builder()
                            .url(IPaddress + "/get-api/getDelFriend" + arg)
                            .build();
                    response = okHttpClient.newCall(request).execute();
                    responseData = response.body().string();
                    gson = new Gson();
                    jsonObject = new JSONObject(responseData);
                    status = jsonObject.getString("status");
                    if(status.equals("200")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i = 0;i < jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            FriendReqRes friendDel = new FriendReqRes();
                            friendDel.setType(FriendReqRes.DELETE_BY_FRIEND);
                            friendDel.setUserId(object.getString("userID"));
                            friendDel.setUsername(object.getString("name"));
                            friendDel.setHeadUrl(object.getString("headURL"));
                            friendDel.setAgreed(object.getBoolean("agree"));
                            friendDel.setMyID(UserID);
                            friendDel.save();
                        }
                    }

                    sleep(2000);
                } catch (InterruptedException | IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        SharedPreferences config=getSharedPreferences("USER_INFO",MODE_PRIVATE);
        UserID=config.getString("userID","");
        //LastDate=config.getString("lastTime","");
        LastDate="2016-01-01 01:01:01";
        new Thread(new GetMessageThread()).start();
        new Thread(new GetMomentsThread()).start();
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

    @Override
    //服务结束前，保留最后请求消息时间于config 的preference中
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor=getSharedPreferences("USER_INFO",MODE_PRIVATE).edit();
        editor.putString("lastTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        editor.commit();
        editor.clear();
        loginOut();
    }
    //登出
    public void loginOut(){
                try{
                    OkHttpClient okHttpClient=new OkHttpClient();
                    String info="?userID="+UserID;
                    Request request = new Request.Builder()
                            .url(getString(R.string.IPAddress)+"/get-api/loginOut"+info)
                            .build();

                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status=="200"){
                        //Toast.makeText(getContext(),"退出登录啦！",Toast.LENGTH_SHORT).show();
                        //getActivity().finish();
                    }else {
                        JSONObject returnData = jsonObject.getJSONObject("data");
                        String msg = returnData.getString("msg");
                        //Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext(), "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext(), "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                }
            }

    }

