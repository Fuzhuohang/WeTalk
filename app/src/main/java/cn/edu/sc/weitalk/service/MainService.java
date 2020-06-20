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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private final String TAG = "MainService";
    private String UserID;
    private String LastDate;
//服务构造函数，从sharedpreference获取用户基本信息（ID）和上次结束时的时间

    public MainService() {

    }

    class GetMessageThread extends Thread {
        public void run() {
            String time = LastDate;
            while (true) {
                try {
                    Thread.sleep(5000);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String info = "?recipient=" + UserID + "&time=" + time;
                    Request request = new Request.Builder()
                            .url(getString(R.string.IPAddress) + "/get-api/getMessage" + info)
                            .build();
                    time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());;
                    Response response = okHttpClient.newCall(request).execute();
                    final String responseData = response.body().string();
                    Log.i("GETMESSAGE", responseData);
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status.equals("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            List<Message> ms = DataSupport.select("*").where("sendID=? and date=? and msgText=?",jsonData.getString("sender"),jsonData.getString("time"),jsonData.getString("content")).find(Message.class);
                            Date timeDate;
                            if(ms.size()==0){
                                cn.edu.sc.weitalk.javabean.Message message = new Message();
                                message.setReceiveName(UserID);
                                message.setSendName(jsonData.getString("sender"));

                                String Time=jsonData.getString("time");
                                Log.i("MYTIME","time: "+Time);
                                Time = Time.replace("Z", " UTC");//是空格+UTC
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
                                timeDate = df.parse(Time);
                                Log.i("MYTIME",""+timeDate);
                                Log.i("MYTIME",""+timeDate.getTime());

                                message.setDate( timeDate.getTime());
                                message.setMsgText(jsonData.getString("content"));
                                message.setMsgType(true);
                                message.setRead(false);
                                Log.i("MS","savestart");
                                message.save();
                                Log.i("MS","saveend");
                                Talks talks;
                                List<Talks> list = DataSupport.select("*").where("FriendID=?", jsonData.getString("sender")).find(Talks.class);
                                if (list.size() == 0) {
                                    talks = new Talks();
//                                talks.setTalksName(tvNickFriendInfo.getText().toString());
                                    talks.setFriendID(jsonData.getString("sender"));
//                                talks.setFriendHeaderURL();
                                    List<Friend> fl = DataSupport.select("*").where("userId=?", jsonData.getString("sender")).find(Friend.class);
                                    if (fl.size() != 0) {
                                        Friend friend = fl.get(0);
                                        if (friend.getNote().length() != 0) {
                                            talks.setTalksName(friend.getNote());
                                        } else {
                                            talks.setTalksName(friend.getUsername());
                                        }
                                    } else {
                                        talks.setTalksName(jsonData.getString("sender"));
                                    }
                                    talks.setMyID(UserID);
                                    talks.setUnReadNum(0);
                                    talks.save();
                                } else {
                                    talks = list.get(0);
                                }
                                talks.setLastMessage(jsonData.getString("content"));
                                Log.i("SAVEMASSAGE",talks.getLastMessage());
                                talks.setLastMessageDate(timeDate.getTime());
                                talks.setUnReadNum(talks.getUnReadNum() + 1);
                                Log.i("SAVEMASSAGE",""+talks.getUnReadNum());
                                talks.updateAll("FriendID=? and MyID=?", jsonData.getString("sender"), UserID);
                            }
                        }
                        if (jsonArray.length()!=0){
                            BroadCastMethod(true, "cn.edu.sc.weitalk.fragment.message");
                            BroadCastMethod(true, "cn.edu.sc.weitalk.activity.talks");
                        }

                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String msg = data.getString("msg");
                        Log.e("GETMESSAGE",msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainService.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainService.this, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //监听朋友圈消息与评论
    class GetMomentsThread extends Thread {
        public void run() {
            String time = LastDate;
            while (true) {
                try {
                    Thread.sleep(5000);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String info = "?recipient=" + UserID + "&time=" + time;
                    Request request = new Request.Builder()
                            .url(getString(R.string.IPAddress) + "/get-api/getShare" + info)
                            .build();
//                    time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    //等待回复
                    Log.i("FLY",  "这里");
                    Response response = okHttpClient.newCall(request).execute();
                    final String responseData = response.body().string();
                    Log.i("GETMOMENTS", responseData);
                    //获取数据
                    JSONObject jsonObject = new JSONObject(responseData);
                    Log.i("FLY", jsonObject.toString() + "");
                    String status = jsonObject.getString("status");
                    //判断状态是否正常
                    if (status.equals("200")) {
                        //，写入两个json数组中
                        JSONArray jsonDataArray = jsonObject.getJSONArray("data");
                        Log.i("FLY", jsonDataArray.length() + "");
                        JSONObject jsonData;
                        for (int i = 0; i < jsonDataArray.length(); i++) {
                            Log.i("FLY", i + "start");
                            String sharedID = jsonDataArray.getJSONObject(i).getString("shareID");
                            MomentsMessage momentsMessage = new MomentsMessage();
                            int commentCounter=Integer.parseInt( jsonDataArray.getJSONObject(i).getString("commentNum"));
                            Log.i("FLY", commentCounter + "   commentCounter");
                            List<MomentsMessage> list = DataSupport.select("*").where("MomentID=?", sharedID).find(MomentsMessage.class);

                            if (list.size() == 0) {
                                momentsMessage.setMomentID(sharedID);
                                momentsMessage.setPublisherID(jsonDataArray.getJSONObject(i).getString("senderID"));
                                momentsMessage.setContent(jsonDataArray.getJSONObject(i).getString("content"));
                                String Time=jsonDataArray.getJSONObject(i).getString("time");
                                Log.i("MYTIME","time: "+Time);
                                Time = Time.replace("Z", " UTC");//是空格+UTC
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
                                Date timeDate = df.parse(Time);
                                Log.i("FLY", timeDate.toString()+"vfbfgnbghnfgjnfgngfh");
                                momentsMessage.setDate(timeDate.toString());
                                momentsMessage.setTimeStamp(timeDate.getTime());
                                momentsMessage.setLikeCounter(Integer.parseInt(jsonDataArray.getJSONObject(i).getString("likeNum")));
                                momentsMessage.setPublisherName(jsonDataArray.getJSONObject(i).getString("sendername"));
                                int imageCounter=0;
                                for(int m=0;m<3;m++) {
                                    if(!jsonDataArray.getJSONObject(i).getString("imgURL"+(m+1)).equals("undefined"))
                                        imageCounter++;
                                }
                                momentsMessage.setImageCounter(imageCounter);
                                momentsMessage.setMomentImage(getString(R.string.IPAddress) + jsonDataArray.getJSONObject(i).getString("imgURL1"));
                                momentsMessage.setMomentImage2(getString(R.string.IPAddress) + jsonDataArray.getJSONObject(i).getString("imgURL2"));
                                momentsMessage.setMomentImage3(getString(R.string.IPAddress) + jsonDataArray.getJSONObject(i).getString("imgURL3"));
                                momentsMessage.saveThrows();

                            } else {
                                momentsMessage=list.get(0);
                                momentsMessage.setMomentID(sharedID);
                                momentsMessage.setLikeCounter(Integer.parseInt(jsonDataArray.getJSONObject(i).getString("likeNum")));
                                int imageCounter=0;
                                for(int m=0;m<3;m++) {
                                    if(!jsonDataArray.getJSONObject(i).getString("imgURL"+(m+1)).equals("undefined"))
                                        imageCounter++;
                                }
                                momentsMessage.setImageCounter(imageCounter);
                                momentsMessage.setMomentImage(getString(R.string.IPAddress) + jsonDataArray.getJSONObject(i).getString("imgURL1"));
                                momentsMessage.setMomentImage2(getString(R.string.IPAddress) + jsonDataArray.getJSONObject(i).getString("imgURL2"));
                                momentsMessage.setMomentImage3(getString(R.string.IPAddress) + jsonDataArray.getJSONObject(i).getString("imgURL3"));
                                momentsMessage.updateAll("MomentID=?", sharedID);
                            }
                            Log.i("FLY", i + "middle");
                            if(commentCounter!=0) {
                                JSONArray jsonCommentsArray = jsonDataArray.getJSONObject(i).getJSONArray("comment");
                                Log.i("FLY", i + "middle11");

                                for (int j = 0; j < commentCounter; j++) {
                                    Log.i("FLY", j + "");

                                    JSONObject jsonComments = jsonCommentsArray.getJSONObject(j);
                                    if (DataSupport.select("*").where("content=? and commentPerID=?", jsonComments.getString("content"),jsonComments.getString("senderID")).find(Comments.class).size() == 0) {
                                        Comments comments = new Comments();
                                        comments.setMomentID(sharedID);
                                        comments.setCommentPerID(jsonComments.getString("senderID"));
                                        comments.setContent(jsonComments.getString("content"));
                                        comments.setCommentPerName(jsonComments.getString("sendername"));
                                        comments.saveThrows();
                                    }
                                }
                            }
                            Log.i("FLY", i + "end");
                        }
                        //BroadCastMethod(true, "cn.edu.sc.weitalk.fragment.moment");
                    } else {
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
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean BroadCastMethod(boolean st, String ReceiveAction) {
        Intent intentReceiver = new Intent();
        intentReceiver.setAction(ReceiveAction);

        intentReceiver.putExtra("ifrefresh", st);

        MainService.this.sendBroadcast(intentReceiver);

        return true;
    }


    class FriendThread extends Thread {
        public void run() {
            while (true) {
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
                            .url(getResources().getString(R.string.IPAddress) + "/get-api/getRAddFriend" + arg)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status.equals("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            FriendReqRes newFriendReq = new FriendReqRes();
                            newFriendReq.setType(FriendReqRes.NEW_FRIEND_REQUEST);
                            newFriendReq.setUserId(object.getString("userID"));
                            newFriendReq.setUsername(object.getString("name"));
                            newFriendReq.setHeadUrl(object.getString("headURL"));
                            newFriendReq.setMyID(UserID);
                            //判断ReqRes上是否重复
                            if( !newFriendReq.isSaved()) {
                                newFriendReq.save();
                            }
                            Log.i(TAG, "收到来自对方好友请求的信息[ " + (i + 1) + " ]");
                        }
                    }
                    /***********************************
                     *  2. 收到想要添加对方为好友的回复消息
                     ***********************************/
                    okHttpClient = new OkHttpClient();
                    request = new Request.Builder()
                            .url(getResources().getString(R.string.IPAddress) + "/get-api/getSAddFriend" + arg)
                            .build();
                    response = okHttpClient.newCall(request).execute();
                    responseData = response.body().string();
                    gson = new Gson();
                    jsonObject = new JSONObject(responseData);
                    status = jsonObject.getString("status");
                    if (status.equals("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            //提醒用户查看的消息
                            FriendReqRes friendRes = new FriendReqRes();
                            friendRes.setType(FriendReqRes.NEW_FRIEND_RESPONSE);
                            friendRes.setUserId(object.getString("userID"));
                            friendRes.setUsername(object.getString("name"));
                            friendRes.setHeadUrl(object.getString("headURL"));
                            friendRes.setAgreed(Boolean.parseBoolean(object.getString("agree")));
                            friendRes.setMyID(UserID);
                            //判断ReqRes上是否重复
                            if( !friendRes.isSaved()) {
                                friendRes.save();
                            }
                            friendRes.save();
                            //好友添加成功时，将好友信息存入本地数据库
                            if(friendRes.isAgreed()){
                                Friend friend = new Friend();
                                friend.setUserID(object.getString("userID"));
                                friend.setUsername(object.getString("name"));
                                friend.setImg(object.getString("headURL"));
                                friend.setStatus(true);
                                friend.setMyID(UserID);
                                //判断好友是否已经在数据库中了
                                if( !friend.isSaved()) {
                                    friend.save();
                                }
                                Log.i(TAG, "收到你想添加对方为好友的回复（同意）[ " + (i + 1) + " ]");
                            }else{
                                Log.i(TAG, "收到你想添加对方为好友的回复（拒绝）[ " + (i + 1) + " ]");
                            }
                        }
                    }

                    /******************************
                     *  3. 收到对方已删除你好友的消息
                     ******************************/
                    okHttpClient = new OkHttpClient();
                    request = new Request.Builder()
                            .url(getResources().getString(R.string.IPAddress) + "/get-api/getDelFriend" + arg)
                            .build();
                    response = okHttpClient.newCall(request).execute();
                    responseData = response.body().string();
                    gson = new Gson();
                    jsonObject = new JSONObject(responseData);
                    status = jsonObject.getString("status");
                    if (status.equals("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            FriendReqRes friendDel = new FriendReqRes();
                            friendDel.setType(FriendReqRes.DELETE_BY_FRIEND);
                            friendDel.setUserId(object.getString("userID"));
                            friendDel.setUsername(object.getString("name"));
                            friendDel.setHeadUrl(object.getString("headURL"));
                            friendDel.setMyID(UserID);
                            friendDel.save();
                            //同时从本地数据库中删除对方
                            DataSupport.deleteAll(Friend.class, "userID=? and MyID=?", object.getString("userID"), UserID);
                            Log.i(TAG, "收到对方将你从好友列表中移除的消息[ " + (i + 1) + " ]");
                        }
                    }

                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }catch (IOException e){
                    Log.i(TAG, "其他问题");
                    e.printStackTrace();
                }catch(JSONException e){
                    Log.i(TAG, "Json error");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        SharedPreferences config = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        UserID = config.getString("userID", "");
        //LastDate=config.getString("lastTime","");
        LastDate = "2016-01-01 01:01:01";
        new Thread(new GetMessageThread()).start();
        new Thread(new GetMomentsThread()).start();
        new Thread(new FriendThread()).start();
        return new MainBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MainBinder extends Binder {
        public MainService getService() {
            return MainService.this;
        }
    }

    @Override
    //服务结束前，保留最后请求消息时间于config 的preference中
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("USER_INFO", MODE_PRIVATE).edit();
        editor.putString("lastTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        editor.commit();
        editor.clear();
        loginOut();
    }

    //登出
    public void loginOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String info = "?userID=" + UserID;
                    Request request = new Request.Builder()
                            .url(getString(R.string.IPAddress) + "/get-api/loginOut" + info)
                            .build();

                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status.equals("200")) {
                        //Toast.makeText(getContext(),"退出登录啦！",Toast.LENGTH_SHORT).show();
                        //getActivity().finish();
                    } else {
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

        }).start();

    }
}

