package cn.edu.sc.weitalk.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.activity.FriendInfoActivity;
import cn.edu.sc.weitalk.activity.SearchFriendActivity;
import cn.edu.sc.weitalk.adapter.FriendListAdapter;
import cn.edu.sc.weitalk.adapter.FriendReqResAdapter;
import cn.edu.sc.weitalk.javabean.Friend;
import cn.edu.sc.weitalk.javabean.FriendReqRes;
import cn.edu.sc.weitalk.widget.LetterIndexView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment {
    private final String TAG = "FriendListFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<Friend> friendList = new ArrayList<Friend>();
    private List<FriendReqRes> friendReqResList = new ArrayList<FriendReqRes>();
    private String MyID;
    @BindView(R.id.btn_search_friend_list)
    Button btnSearch;
    @BindView(R.id.lv_fiend_list)
    ListView lvFiendList;
    LetterIndexView letterIndexView;
    TextView bigLetter;
    RecyclerView rvFriendReqRes;
    FriendListAdapter friendListAdapter;
    FriendReqResAdapter friendReqResAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendListFragment newInstance(String param1, String param2) {
        FriendListFragment fragment = new FriendListFragment();
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

        //test, 生产user数据
//        list = new ArrayList<>();
//        for(int i = 0;i < 50;i++){
//            Friend friend = new Friend("A", R.drawable.dragon,"long", "龙");
//            list.add(friend);
//        }

        //test
//        friendReqResList = new ArrayList<>();
//        FriendReqRes reqRes = new FriendReqRes();
//        reqRes.setUsername("李可");
//        reqRes.setType(0);
//        friendReqResList.add(reqRes);
//        reqRes = new FriendReqRes();
//        reqRes.setUsername("范重阳");
//        reqRes.setType(FriendReqRes.NEW_FRIEND_RESPONSE);
//        reqRes.setAgreed(false);
//        friendReqResList.add(reqRes);
//        reqRes = new FriendReqRes();
//        reqRes.setUsername("付卓航");
//        reqRes.setType(1);
//        reqRes.setType(FriendReqRes.NEW_FRIEND_RESPONSE);
//        reqRes.setAgreed(true);
//        friendReqResList.add(reqRes);
//        reqRes = new FriendReqRes();
//        reqRes.setUsername("何老实");
//        reqRes.setType(FriendReqRes.DELETE_BY_FRIEND);
//        friendReqResList.add(reqRes);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        SharedPreferences config=getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        MyID=config.getString("userID","");
        //给控件赋值
        lvFiendList = view.findViewById(R.id.lv_fiend_list);
        btnSearch = view.findViewById(R.id.btn_search_friend_list);
        letterIndexView = view.findViewById(R.id.letterIndexView);
        bigLetter = view.findViewById(R.id.tv_big_letter);
        rvFriendReqRes = view.findViewById(R.id.rv_friend_req_res);
        //设置ListView
        friendListAdapter = new FriendListAdapter(getContext(), friendList);
        lvFiendList.setAdapter(friendListAdapter);
            //给list item添加点击事件，进入好友信息Activity
        lvFiendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), FriendInfoActivity.class);
                Friend friend = friendList.get(position);
                intent.putExtra("id", friend.getUserID());
                startActivity(intent);
            }
        });
        lvFiendList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int sectionForPosition = friendListAdapter.getSectionForPosition(firstVisibleItem);
                letterIndexView.updateLetterIndexView(sectionForPosition);
            }
        });

        //设置LetterIndexView
        letterIndexView.setTextViewDialog(bigLetter);
        letterIndexView.setOnLetterSelectedListener(new LetterIndexView.OnLetterSelectedListener() {
            @Override
            public void onLetterSelected(String currentChar) {
                int positionForSection = friendListAdapter.getPositionForSection(currentChar.charAt(0));
                lvFiendList.setSelection(positionForSection);
            }
        });

        //设置添加好友请求和回复的RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        friendReqResAdapter = new FriendReqResAdapter(getContext(), friendReqResList);
        rvFriendReqRes.setAdapter(friendReqResAdapter);
        rvFriendReqRes.setLayoutManager(layoutManager);

        //设置搜索按钮的点击事件，跳转到搜索好友页面
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchFriendActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void initFriendList(){
        friendList = DataSupport.select("*").where("MyID=?",MyID).find(Friend.class);
        Log.i(TAG, "本地朋友列表大小：" + friendList.size());
    }

    private void initFriendReqRes(){
        friendReqResList = DataSupport.select("*").where("MyID=?", MyID).find(FriendReqRes.class);
        Log.i(TAG, "好友ReqRes数目：" + friendReqResList.size());
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) rvFriendReqRes.getLayoutParams();
        rvFriendReqRes.setLayoutParams(linearParams);
        if(friendReqResList.size() <= 2){
            linearParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            rvFriendReqRes.setLayoutParams(linearParams);
        }else{
            linearParams.height=((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
            rvFriendReqRes.setLayoutParams(linearParams);
        }
    }

    //每次恢复页面（页面可见时）重新从本地数据库获取好友列表，以及好友请求和回复等信息
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume invoked");
        updateListData();
    }

    public void updateListData(){
        initFriendList();
        friendListAdapter.setList(friendList);
        friendListAdapter.notifyDataSetChanged();

        initFriendReqRes();
        friendReqResAdapter.setList(friendReqResList);
        friendReqResAdapter.notifyDataSetChanged();
    }
}