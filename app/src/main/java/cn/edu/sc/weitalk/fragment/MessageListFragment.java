package cn.edu.sc.weitalk.fragment;

import android.content.Intent;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.activity.TalksActivity;
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
    private ArrayList list;

    private boolean isTwoPane;

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
        initArrayList();
        Button btnSearch = view.findViewById(R.id.btnSearch);
        ListView messageList = view.findViewById(R.id.MessageList);
        SimpleAdapter adapter = new SimpleAdapter(getContext(),list,R.layout.message_list_item,
                new String[]{"image","talksObj","lastMessage"},new int[]{R.id.talks_img,R.id.talksObj,R.id.lastMessage});
        messageList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(messageList);

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("TAUCH",""+position);
                HashMap map = (HashMap)list.get(position);
                Talks talks = new Talks();
                talks.setTalksName((String) map.get("talksObj"));
                talks.setUserName("aaa");
                talks.setFriendHeaderURL((String) map.get("image"));
                talks.setMessage((String)map.get("lastMessage"));
                talks.setMyHeaderURL("res://drawable/" + R.drawable.dragon);
                if (isTwoPane){
                    Log.i("TALKS",""+isTwoPane);
                }else {
                    Log.i("TALKS",""+isTwoPane);
                    Intent intent = new Intent(getActivity(), TalksActivity.class);
                    intent.putExtra("TalksName",talks.getTalksName());
                    intent.putExtra("UserName",talks.getUserName());
                    intent.putExtra("FriendHeaderURL",talks.getFriendHeaderURL());
                    intent.putExtra("Message",talks.getMessage());
                    intent.putExtra("MyHeaderURL",talks.getMyHeaderURL());
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
        list = new ArrayList();
        for(int i=0;i<12;i++){
            HashMap map = new HashMap();
            map.put("image","res://drawable/" + R.drawable.dragon);
            map.put("talksObj","会话名称"+(i+1));
            map.put("lastMessage","这是最近的一条消息。");
            list.add(map);
        }
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
}