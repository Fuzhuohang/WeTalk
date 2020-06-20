package cn.edu.sc.weitalk.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mbg.library.DefaultPositiveRefreshers.PositiveRefresherWithText;
import com.mbg.library.ISingleRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.adapter.TalksListAdapter;
import cn.edu.sc.weitalk.adapter.momentsMessageAdapter;
import cn.edu.sc.weitalk.javabean.Comments;
import cn.edu.sc.weitalk.javabean.MomentsMessage;
import cn.edu.sc.weitalk.javabean.Talks;
import cn.edu.sc.weitalk.service.MainService;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.os.SystemClock.sleep;

/**我是fcy
 * A simple {@link Fragment} subclass.
 * Use the {@link CircleOfFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CircleOfFriendsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;
    public momentsMessageAdapter adapter;
    public RecyclerView recyclerView;
    private NestedScrollView scrollView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String userID;
    private String headUrl;
    private String lastTime;
    private String name;
    public Thread thread;
    public CircleOfFriendsFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CircleOfFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CircleOfFriendsFragment newInstance(String param1, String param2) {
        CircleOfFriendsFragment fragment = new CircleOfFriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_circle_of_friends, container, false);
        SimpleDraweeView temp2 = view.findViewById(R.id.iconself);
        TextView nameText=view.findViewById(R.id.nameView);
        recyclerView=view.findViewById(R.id.messageRecy);
        scrollView=view.findViewById(R.id.scrollview);
        adapter = new momentsMessageAdapter(getContext());
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        SharedPreferences config=getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        userID=config.getString("userID","");
        headUrl=getString(R.string.IPAddress)+config.getString("headURL","");
        name=config.getString("name","");
        Log.i("TOU",headUrl);
        temp2.setImageURI(headUrl);
        nameText.setText(name);
        lastTime="2016-01-01 01:01:01";
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(10);
        thread=null;
        new Thread(new Runnable() {//开线程，进行耗时操作，等到页面加载成功，才可以调用getTop方法，获得与顶部的距离，之后对ScrollView的滑动进行监听，若移动位置超过一定距离，可以滚动recyclerview
            @Override
            public void run() {
                sleep(1000);
                int dis = recyclerView.getTop();
                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()

                {
                    @Override
                    public void onScrollChange (NestedScrollView v,int scrollX, int scrollY,
                                                int oldScrollX, int oldScrollY){
                        //判断是否滑到的底部
                        if (scrollY >= dis) {
                            recyclerView.setNestedScrollingEnabled(true);
                        } else {
                            recyclerView.setNestedScrollingEnabled(false);
                        }
                    }
                });
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()

                {
                    @Override
                    public void onScrolled (@NonNull RecyclerView recyclerView,int dx, int dy){
                        if (dy == 0 && scrollView.getScrollY() <= dis)
                            recyclerView.setNestedScrollingEnabled(false);
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
            }
        }).start();


        RefreshRelativeLayout refresh_message = (RefreshRelativeLayout)view.findViewById(R.id.refresh_Momentmessage);
        refresh_message.setPositiveRefresher(new PositiveRefresherWithText(true));
        refresh_message.setPositiveEnable(true);
        refresh_message.setNegativeEnable(false);
        refresh_message.setPositiveOverlayUsed(true);
        refresh_message.setPositiveDragEnable(true);

        refresh_message.addPositiveRefreshListener(new ISingleRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(),"朋友圈刷新成功！",Toast.LENGTH_LONG).show();
                if(thread!=null)
                    thread.interrupt();
                updateMoments();
                refresh_message.positiveRefreshComplete();
            }
        });
        return view;
    }

    public void updateMoments() {
        Log.i("MMMM","haha");
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String time = "2016-01-01 01:01:01";
                    try {
                        OkHttpClient okHttpClient = new OkHttpClient();
                        String info = "?recipient=" + userID + "&time=" + time;
                        Request request = new Request.Builder()
                                .url(getString(R.string.IPAddress) + "/get-api/getShare" + info)
                                .build();
//                    time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        //等待回复
                        Response response = okHttpClient.newCall(request).execute();
                        final String responseData = response.body().string();
                        //获取数据
                        JSONObject jsonObject = new JSONObject(responseData);
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
                                    Log.i("FLY1", sharedID);
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
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.refreshData();
                                }
                            });
                            //BroadCastMethod(true, "cn.edu.sc.weitalk.fragment.moment");
                        } else {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String msg = data.getString("msg");
                            //Toast.makeText(MainService.this,msg,Toast.LENGTH_SHORT).show();
                        }
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


        });
        thread.start();
    }

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean ifRefresh = intent.getExtras().getBoolean("ifrefresh");
            if(ifRefresh){

            }
        }
    }

    @Override
    public void onAttach(@NonNull Activity activity) {

        RefreshReceiver receiver = new RefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("package cn.edu.sc.weitalk.fragment.moment");
        activity.registerReceiver(receiver,filter);
        super.onAttach(activity);
    }
}