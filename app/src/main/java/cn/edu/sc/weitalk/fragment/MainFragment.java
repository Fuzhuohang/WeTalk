package cn.edu.sc.weitalk.fragment;

import android.Manifest;
import android.content.Context;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.activity.AddNewCommentActivity;
import cn.edu.sc.weitalk.activity.FriendInfoActivity;
import cn.edu.sc.weitalk.activity.LoginActivity;
import cn.edu.sc.weitalk.activity.SearchFriendActivity;
import cn.edu.sc.weitalk.activity.TalksActivity;
import cn.edu.sc.weitalk.activity.UserInfoActivity;
import cn.edu.sc.weitalk.adapter.ViewPagerAdapter;
import cn.edu.sc.weitalk.javabean.MomentsMessage;
import cn.edu.sc.weitalk.util.Constant;
import cn.edu.sc.weitalk.util.ZXingUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

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
    private AppBarConfiguration mAppBarConfiguration, bAppBarConfiguration;
    private TextView pagename;
    private String userID, userName, headURL;
    private Toolbar toolbar;
    private String QRCodeMessage;
    MessageListFragment messageListFragment;
    FriendListFragment friendListFragment;
    CircleOfFriendsFragment circleOfFriendsFragment;
    //private LinearLayout btnVector;
    private ImageView btn;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SharedPreferences config;
    private NavigationView navigationView;

    public MainFragment() {
//        config =
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
    public MainFragment newInstance(String param1, String param2) {
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
        SharedPreferences config = getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        userID = config.getString("userID", "");
        userName = config.getString("name", "");
        headURL = config.getString("headURL", "");
        Log.d("USER", userID);

        navigationView = view.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        SimpleDraweeView temp = headerView.findViewById(R.id.icon);
//        temp.setImageURI("res://drawable/" + R.drawable.dragon);
        temp.setImageURI(getString(R.string.IPAddress)+headURL);
        TextView name = headerView.findViewById(R.id.Username);
        name.setText(userName);
        TextView userid = headerView.findViewById(R.id.UserID);
        userid.setText("ID: " + userID);
        navigationView.setItemIconTintList(null);   //设置icon为原本图片的颜色

        DrawerLayout drawerLayout = view.findViewById(R.id.drawerlayout);
        toolbar = view.findViewById(R.id.toolbar);
        SimpleDraweeView temp2 = view.findViewById(R.id.toolbar_img);
//        temp2.setImageURI("res://drawable/" + R.drawable.dragon);
        temp2.setImageURI(getString(R.string.IPAddress)+headURL);
        pagename = view.findViewById(R.id.page_name);
        //btnVector=view.findViewById(R.id.btnVector);
        btn = view.findViewById(R.id.buttonview);
        btn.setVisibility(View.GONE);
        DrawerLayout drawerLayout1 = view.findViewById(R.id.drawerlayout);
        temp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout1.openDrawer(navigationView);
            }
        });



        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ViewPager viewPager = getView().findViewById(R.id.main_viewpager);
        final BottomNavigationView bottomNavigationView = getView().findViewById(R.id.bottom_view);
        final ArrayList viewList = new ArrayList<Fragment>();

        messageListFragment = new MessageListFragment();
        friendListFragment = new FriendListFragment();
        circleOfFriendsFragment = new CircleOfFriendsFragment();
        viewList.add(messageListFragment);
        viewList.add(friendListFragment);
        viewList.add(circleOfFriendsFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), viewList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        switch (viewPager.getCurrentItem()) {
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
                //滑动到好友列表界面时，从本地数据库跟新好友数据，读取是否有FriendReqRes信息
                if(position == 1)
                    friendListFragment.updateListData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.message_b:
                        viewPager.setCurrentItem(0, true);
                        pagename.setText("消息");
                        btn.setVisibility(View.GONE);
                        break;
                    case R.id.lxr_b:
                        viewPager.setCurrentItem(1, true);
                        pagename.setText("联系人");
                        btn.setVisibility(View.VISIBLE);
                        btn.setClickable(false);
                        btn.setImageResource(R.drawable.tianjiajiahaoyoutianjiapengyou);
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) btn.getLayoutParams();
                        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
                        params.rightMargin = 15;
                        params.topMargin = 30;
                        params.bottomMargin = 30;
                        btn.setLayoutParams(params);
                        break;
                    case R.id.pyq_b:
                        viewPager.setCurrentItem(2, true);
                        pagename.setText("朋友圈");
                        btn.setVisibility(View.VISIBLE);
                        btn.setImageResource(R.drawable.tianjia);
                        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) btn.getLayoutParams();
                        params1.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                        params1.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
                        params1.rightMargin = 15;
                        params1.topMargin = 30;
                        params1.bottomMargin = 30;
                        btn.setLayoutParams(params1);
                        if(circleOfFriendsFragment.thread!=null)
                            circleOfFriendsFragment.thread.interrupt();
                        circleOfFriendsFragment.updateMoments();
                        //circleOfFriendsFragment.adapter.refreshData();
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), AddNewCommentActivity.class);
                                startActivityForResult(intent, 200);
                            }
                        });
                        break;
                }
                return true;
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.myinfo:
                        Intent intent=new Intent(getActivity(), UserInfoActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.myqrcode:
                        createMyQRCode();
                        break;
                    case R.id.scanqrcode:
                        scanQRCode();
                        break;
                    case R.id.exit:
                        //Toast.makeText(getContext(),"1111111",Toast.LENGTH_SHORT).show();
                        loginOut();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

    }

    //登出
    public void loginOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String info = "?userID=" + userID;
                    Request request = new Request.Builder()
                            .url(getString(R.string.IPAddress) + "/get-api/loginOut" + info)
                            .build();

                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status.equals("200")) {
                        //Toast.makeText(getContext(), "退出登录啦！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        JSONObject returnData = jsonObject.getJSONObject("data");
                        String msg = returnData.getString("msg");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    //生成我的二维码信息
    private void createMyQRCode() {
        String MyMessage = "userName: " + userName + " userID: " + userID;
        Bitmap bitmap = ZXingUtils.createQRImage(MyMessage, 400, 400);
        View QRCodeView = LayoutInflater.from(getContext()).inflate(R.layout.qrcode_display, null);
        ImageView qrCodeView = QRCodeView.findViewById(R.id.QRCode);
        qrCodeView.setImageBitmap(bitmap);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(QRCodeView);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //扫描二维码
    private void scanQRCode() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                Toast.makeText(getContext(), "请到权限中心打开相机访问权限", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }

        Intent intent = new Intent(getContext(), CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_PERM_CAMERA);
    }

    //新建朋友圈消息的本地存储与实时发送
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == 200) {
            //生成新的朋友圈消息，存入数据库中
            MomentsMessage temp = new MomentsMessage();
            temp.setContent(data.getStringExtra("content"));
            Date time=new Date();
            temp.setTimeStamp(time.getTime());
            temp.setDate((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(time));
            temp.setMomentImage(data.getStringExtra("imagePath"));
            temp.setMomentImage2(data.getStringExtra("imagePath2"));
            temp.setMomentImage3(data.getStringExtra("imagePath3"));
//            temp.setPublisherID("123456");
            temp.setPublisherName(userName);
            temp.setPublisherID(userID);
            //temp.setMomentID(0+"");
            temp.setHeadshot(getString(R.string.IPAddress)+headURL);
            temp.setLikeCounter(0);
            temp.setImageCounter(data.getIntExtra("imageNumber", 0));
            File file = new File(temp.getMomentImage());
            File file2 = new File(temp.getMomentImage2());
            File file3 = new File(temp.getMomentImage3());
//            Log.i("FLLL", file.length()+"");
//            Log.i("FLLL", temp.getMomentImage2());
//            Log.i("FLLL", temp.getMomentImage3());
//            temp.save();
//           circleOfFriendsFragment.adapter.refreshData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //发送数据到服务器，发送成功则存入本地数据库，并提示，否则不存并提示
                    try {
                        OkHttpClient okHttpClient = new OkHttpClient();
                        RequestBody filebody = RequestBody.create(file,MediaType.parse("image/jpg"));
                        RequestBody filebody2 = RequestBody.create( file2,MediaType.parse("image/jpg"));
                        RequestBody filebody3 = RequestBody.create(file3,MediaType.parse("image/jpg"));
                        MultipartBody.Builder builder = new MultipartBody.Builder();
                        builder.setType(MultipartBody.FORM);
                        builder.addFormDataPart("userID", userID);
                        builder.addFormDataPart("userName",userName);
                        builder.addFormDataPart("content", temp.getContent());
                        ArrayList<RequestBody> list=new ArrayList<RequestBody>();
                        list.add(filebody);
                        list.add(filebody2);
                        list.add(filebody3);
                        ArrayList<File> a=new ArrayList<File>();
                        a.add(file);
                        a.add(file2);
                        a.add(file3);
                        for(int i=0;i<temp.getImageCounter();i++){
                                builder.addFormDataPart("img"+(i+1), a.get(i).getName(), list.get(i));
                        }
                        RequestBody multipartBody = builder.build();
                        Request request = new Request.Builder()
                                .url(getString(R.string.IPAddress) + "/post-api/sendShare")
                                .post(multipartBody)
                                .build();
                        Response response = okHttpClient.newCall(request).execute();
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        String status = jsonObject.getString("status");
                        if (status.equals("200")) {
                            JSONObject returnData = jsonObject.getJSONObject("data");
                            temp.setMomentID(returnData.getString("shareID"));
                            temp.save();
                            circleOfFriendsFragment.updateMoments();
                            circleOfFriendsFragment.recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    circleOfFriendsFragment.adapter.refreshData();
                                }
                            });
                            //Toast.makeText(getContext(),"朋友圈发送成功啦！",Toast.LENGTH_SHORT).show();
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
        }else if (requestCode == Constant.REQ_PERM_CAMERA && resultCode == RESULT_OK){
            Log.i("SCANQRCODE","scanqrcode");
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            Log.i("SCANQRCODE","扫描结果是："+scanResult);
            QRCodeMessage = scanResult;
            String[] s = QRCodeMessage.split(" ");
            Intent intent = new Intent(getContext(), FriendInfoActivity.class);
            intent.putExtra("id",s[s.length-1]);
            startActivity(intent);

        }
//            Toast.makeText(getContext(),data.getStringExtra("txt"),Toast.LENGTH_SHORT).show();
        }

    }
