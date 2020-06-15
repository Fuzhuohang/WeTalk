package cn.edu.sc.weitalk.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.adapter.ViewPagerAdapter;

/**kgjhkhjjkhbhfghfghghhfghfggfgrgr
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private AppBarConfiguration mAppBarConfiguration,bAppBarConfiguration;
    private TextView pagename;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        setSupporActionBar(toolbar);
//        ViewPager viewPager = view.findViewById(R.id.main_viewpager);
        //View view1=view.findViewById(R.id.nav_view);
        //Fresco.initialize(getContext());
//        SimpleDraweeView temp=view.findViewById(R.id.drawee_img);
//        temp.setImageURI("res://drawable/" + R.drawable.dragon);
        NavigationView navigationView=view.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        SimpleDraweeView temp=headerView.findViewById(R.id.drawee_img);
        temp.setImageURI("res://drawable/" + R.drawable.dragon);
//        TextView name=headerView.findViewById(R.id.Username);
//        name.setText("111111");

//        Toolbar toolbar = view.findViewById(R.id.toolbar);
        SimpleDraweeView temp2 = view.findViewById(R.id.toolbar_img);
        temp2.setImageURI("res://drawable/" + R.drawable.dragon);
        pagename = view.findViewById(R.id.page_name);
        temp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ViewPager viewPager = getView().findViewById(R.id.main_viewpager);
        final BottomNavigationView bottomNavigationView = getView().findViewById(R.id.bottom_view);
        final ArrayList viewList = new ArrayList<Fragment>();
        MessageListFragment messageListFragment = new MessageListFragment();
        FriendListFragment friendListFragment = new FriendListFragment();
        CircleOfFriendsFragment circleOfFriendsFragment = new CircleOfFriendsFragment();
        viewList.add(messageListFragment);
        viewList.add(friendListFragment);
        viewList.add(circleOfFriendsFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),viewList);
        viewPager.setAdapter(viewPagerAdapter);

        switch (viewPager.getCurrentItem()){
            case 0:
                pagename.setText("消息");
                break;
            case 1:
                pagename.setText("联系人");
                break;
            case 2:
                pagename.setText("朋友圈");
                break;
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(position).getItemId());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.message_b:
                        viewPager.setCurrentItem(0,true);
                        pagename.setText("消息");
                        break;
                    case R.id.lxr_b:
                        viewPager.setCurrentItem(1,true);
                        pagename.setText("联系人");
                        break;
                    case R.id.pyq_b:
                        viewPager.setCurrentItem(2,true);
                        pagename.setText("朋友圈");
                        break;
                }
                return true;
            }
        });


    }
}