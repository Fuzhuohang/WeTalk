package cn.edu.sc.weitalk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.facebook.drawee.view.SimpleDraweeView;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.adapter.momentsMessageAdapter;

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
    private momentsMessageAdapter adapter;
    private RecyclerView recyclerView;
    private NestedScrollView scrollView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        temp2.setImageURI("res://drawable/" + R.drawable.dragon);
        recyclerView=view.findViewById(R.id.messageRecy);
        scrollView=view.findViewById(R.id.scrollview);
        adapter = new momentsMessageAdapter(getContext());
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
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


        return view;
    }
}