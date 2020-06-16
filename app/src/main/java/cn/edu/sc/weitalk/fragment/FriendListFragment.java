package cn.edu.sc.weitalk.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import butterknife.BindView;
import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.adapter.FriendListAdapter;
import cn.edu.sc.weitalk.javabean.User;
import cn.edu.sc.weitalk.widget.LetterIndexView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<User> list;
    @BindView(R.id.btn_search_friend_list)
    Button btnSearch;
    @BindView(R.id.lv_fiend_list)
    ListView lvFiendList;
    LetterIndexView letterIndexView;
    TextView bigLetter;

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

        list = new ArrayList<>();
        for(int i = 0;i < 10;i++){
            User user = new User("A", R.drawable.dragon,"long", "龙");
            list.add(user);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        //给控件赋值
        lvFiendList = view.findViewById(R.id.lv_fiend_list);
        btnSearch = view.findViewById(R.id.btn_search_friend_list);
        letterIndexView = view.findViewById(R.id.letterIndexView);
        bigLetter = view.findViewById(R.id.tv_big_letter);
        //设置ListView
        FriendListAdapter adapter = new FriendListAdapter(getContext(), list);
        lvFiendList.setAdapter(adapter);
        lvFiendList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int sectionForPosition = adapter.getSectionForPosition(firstVisibleItem);
                letterIndexView.updateLetterIndexView(sectionForPosition);
            }
        });
        //设置LetterIndexView
        letterIndexView.setTextViewDialog(bigLetter);
        letterIndexView.setOnLetterSelectedListener(new LetterIndexView.OnLetterSelectedListener() {
            @Override
            public void onLetterSelected(String currentChar) {
                int positionForSection = adapter.getPositionForSection(currentChar.charAt(0));
                lvFiendList.setSelection(positionForSection);
            }
        });
        return view;
    }
}