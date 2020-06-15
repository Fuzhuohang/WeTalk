package cn.edu.sc.weitalk.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.activity.AddNewCommentActivity;
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
    //private LinearLayout btnVector;
   private ImageView btn;
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
        SimpleDraweeView temp=headerView.findViewById(R.id.icon);
        temp.setImageURI("res://drawable/" + R.drawable.dragon);
//        TextView name=headerView.findViewById(R.id.Username);
//        name.setText("111111");

//        Toolbar toolbar = view.findViewById(R.id.toolbar);
        SimpleDraweeView temp2 = view.findViewById(R.id.toolbar_img);
        temp2.setImageURI("res://drawable/" + R.drawable.dragon);
        pagename = view.findViewById(R.id.page_name);
        //btnVector=view.findViewById(R.id.btnVector);
        btn=view.findViewById(R.id.buttonview);
        btn.setVisibility(View.GONE);
        DrawerLayout drawerLayout=view.findViewById(R.id.drawerlayout);
        temp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(navigationView);
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
                        btn.setVisibility(View.GONE);
                        break;
                    case R.id.lxr_b:
                        viewPager.setCurrentItem(1,true);
                        pagename.setText("联系人");
                        btn.setVisibility(View.VISIBLE);
                        btn.setClickable(false);
                        btn.setImageResource(R.drawable.tianjiajiahaoyoutianjiapengyou);
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) btn.getLayoutParams();
                        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
                        params.rightMargin = 15;
                        params.topMargin=30;
                        params.bottomMargin=30;
                        btn.setLayoutParams(params);
                        break;
                    case R.id.pyq_b:
                        viewPager.setCurrentItem(2,true);
                        pagename.setText("朋友圈");
                        btn.setVisibility(View.VISIBLE);
                        btn.setImageResource(R.drawable.tianjia);
                        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) btn.getLayoutParams();
                        params1.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                        params1.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
                        params1.rightMargin = 15;
                        params1.topMargin=30;
                        params1.bottomMargin=30;
                        btn.setLayoutParams(params1);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getContext(), AddNewCommentActivity.class);
                                startActivityForResult(intent,0);
                            }
                        });
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&requestCode==1)
            Toast.makeText(getContext(),data.getStringExtra("txt"),Toast.LENGTH_SHORT).show();
    }
}