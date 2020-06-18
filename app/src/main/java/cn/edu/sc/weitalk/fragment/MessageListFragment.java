package cn.edu.sc.weitalk.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.mbg.library.DefaultPositiveRefreshers.PositiveRefresherWithText;
import com.mbg.library.ISingleRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

import org.litepal.LitePal;
import org.litepal.LitePalBase;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.activity.MainActivity;
import cn.edu.sc.weitalk.activity.TalksActivity;
import cn.edu.sc.weitalk.adapter.TalksListAdapter;
import cn.edu.sc.weitalk.javabean.Talks;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Talks> list;

    private boolean isTwoPane;
    private String MyID;

    private TalksListAdapter adapter;
    private ListView messageList;

    public MessageListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageListFragment newInstance(String param1, String param2) {
        MessageListFragment fragment = new MessageListFragment();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if(getActivity().findViewById(R.id.fragment)!=null){
//            isTwoPane=true;
//        }else {
//            isTwoPane=false;
//        }
        isTwoPane=false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        SharedPreferences config=getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        MyID=config.getString("userID","");
        initArrayList();
        Button btnSearch = view.findViewById(R.id.btnSearch);
        messageList = view.findViewById(R.id.MessageList);
        adapter = new TalksListAdapter(list, getContext());
        messageList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(messageList);

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("TAUCH",""+position);
                if (isTwoPane){
                    Log.i("TALKS",""+isTwoPane);
                }else {
                    Log.i("TALKS",""+isTwoPane);
                    Intent intent = new Intent(getActivity(), TalksActivity.class);
                    intent.putExtra("TalksName",list.get(position).getTalksName());
                    intent.putExtra("FriendsID",list.get(position).getFriendID());
                    intent.putExtra("FriendHeaderURL",list.get(position).getFriendHeaderURL());
                    startActivity(intent);
                }
            }
        });

        RefreshRelativeLayout refresh_message = (RefreshRelativeLayout)view.findViewById(R.id.refresh_message);
        refresh_message.setPositiveRefresher(new PositiveRefresherWithText(true));
        refresh_message.setPositiveEnable(true);
        refresh_message.setNegativeEnable(false);
        refresh_message.setPositiveOverlayUsed(true);
        refresh_message.setPositiveDragEnable(true);

        refresh_message.addPositiveRefreshListener(new ISingleRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(),"刷新成功！",Toast.LENGTH_LONG).show();
                refresh_message.positiveRefreshComplete();
            }
        });




        return view;
    }

    public void initArrayList(){
//        DataSupport.deleteAll(Talks.class);
//        for(int i=0;i<12;i++){
//            Talks talks = new Talks();
//            talks.setFriendHeaderURL("res://drawable/" + R.drawable.dragon);
//            talks.setTalksName("会话名称"+(i+1));
//            talks.setLastMessage("这是最近的一条消息。");
//            talks.setFriendID("111");
//            talks.save();
//        }
//        list = DataSupport.findAll(Talks.class);
        list = DataSupport.select("*").where("MyID = ?",MyID).order("LastMessageDate").find(Talks.class);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter==null){
            return;
        }
        int totalHeight = 0;
        for(int i=0;i<listAdapter.getCount();i++){
            View listItem=listAdapter.getView(i,null,listView);
            listItem.measure(0,0);
            totalHeight+=listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight+(listView.getDividerHeight()*(listAdapter.getCount()-1));
        listView.setLayoutParams(params);
    }

    private class RefreshReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean ifRefresh = intent.getExtras().getBoolean("ifrefresh");
            if(ifRefresh){
                list = DataSupport.select("*").where("MyID = ?",MyID).order("LastMessageDate").find(Talks.class);
                adapter = new TalksListAdapter(list, getContext());
                messageList.setAdapter(adapter);
                setListViewHeightBasedOnChildren(messageList);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Activity activity) {

        RefreshReceiver receiver = new RefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("package cn.edu.sc.weitalk.fragment.message");
        activity.registerReceiver(receiver,filter);
        super.onAttach(activity);
    }
}