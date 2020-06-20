package cn.edu.sc.weitalk.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.activity.MainActivity;
import cn.edu.sc.weitalk.javabean.Comments;
import cn.edu.sc.weitalk.javabean.MomentsMessage;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.co.senab.photoview.PhotoView;
import java.util.Collections;
public class momentsMessageAdapter extends RecyclerView.Adapter<momentsMessageAdapter.ViewHolder>{

Context context;
    public ArrayList<Comments> comments;
    public ArrayList<MomentsMessage> moments;
    public ArrayList<Boolean> isLikedList;
    public String userID;
    private String userName;
    private Activity activity;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public momentsMessageAdapter(Context context){

        this.context=context;
        SharedPreferences config=context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        userID=config.getString("userID","");
        userName=config.getString("name","");
        moments=null;
        comments=null;
        refreshData();
    }

    public  void refreshData(){//更新数据库
        //comments= (ArrayList<Comments>)DataSupport.findAll(Comments.class);
        moments=(ArrayList<MomentsMessage>)DataSupport.findAll(MomentsMessage.class);
        Collections.reverse(moments);
        isLikedList=new ArrayList<Boolean>();
        for(int i=0;i<moments.size();i++){
            isLikedList.add(false);
        }

        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_moments_meaasge, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.imageSelected=new SimpleDraweeView(context);
        holder.imageSelected2=new SimpleDraweeView(context);
        holder.imageSelected3=new SimpleDraweeView(context);
        holder.imagesGroup.addView(holder.imageSelected);
        holder.imagesGroup.addView(holder.imageSelected2);
        holder.imagesGroup.addView(holder.imageSelected3);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 300);
        params.leftMargin = 50;
        holder.imageSelected.setLayoutParams(params);
        holder.imageSelected2.setLayoutParams(params);
        holder.imageSelected3.setLayoutParams(params);
        return holder;
    }

    @Override
//    public void onViewRecycled(@NonNull ViewHolder holder) {
//        super.onViewRecycled(holder);
//        AsyncTask temp=(AsyncTask)holder.imagesGroup.getTag();
//        temp.cancel(true);
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MomentsMessage temp = moments.get(position);
        holder.name.setText(temp.getPublisherName());
        //设置发起时间
        holder.time.setText(temp.getDate());
        //消息文本
        holder.Text.setText(temp.getContent());
        //设置头像，bitmap转为URI，后显示为图片
        //Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), icons.get(position), null, null));
        //holder.icon.setImageURI(uri);
        holder.icon.setImageURI("res://drawable/" + R.drawable.dragon);
        if(temp.getLikeCounter()>0){
            holder.likeCounter.setVisibility(View.VISIBLE);
            holder.likeCounter.setText(temp.getLikeCounter()+"");
        }else
            holder.likeCounter.setVisibility(View.INVISIBLE);

        if(temp.getImageCounter()<=0)
            holder.imagesGroup.setVisibility(View.GONE);
        else
            holder.imagesGroup.setVisibility(View.VISIBLE);
        ArrayList<String> uriList=new ArrayList<String>();
        ArrayList<SimpleDraweeView> imageList=new ArrayList<SimpleDraweeView>();
        uriList.add(temp.getMomentImage());
        uriList.add(temp.getMomentImage2());
        uriList.add(temp.getMomentImage3());
        imageList.add(holder.imageSelected);
        imageList.add(holder.imageSelected2);
        imageList.add(holder.imageSelected3);
        //消息图片
        for(int i=0;i<3;i++) {
            if(i<temp.getImageCounter()) {

                imageList.get(i).setVisibility(View.VISIBLE);
//                if(temp.getPublisherID().equals(userID)){
//                    String uri="file://"+uriList.get(i);
//                    imageList.get(i).setImageURI(uri);
//                }
//                else
                    imageList.get(i).setImageURI(uriList.get(i));

                    }
            else {
                imageList.get(i).setVisibility(View.INVISIBLE);
                }
            }
        //Toast.makeText(context, temp.getImageCounter()+"", Toast.LENGTH_SHORT).show();


        //加载评论
        List<Comments> com=DataSupport.select("*").where("MomentID=?",temp.getMomentID()).find(Comments.class);
        if(com.size()>0){
            //Toast.makeText(context, "com.size = " + com.size(), Toast.LENGTH_SHORT).show();
            holder.comments.removeAllViews();
            holder.comments.setVisibility(View.VISIBLE);
            for(int i=0;i<com.size();i++){
                TextView comment=new TextView(context);
                //Toast.makeText(context, com.get(i).getContent(), Toast.LENGTH_SHORT).show();
                holder.comments.addView(comment);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) comment.getLayoutParams();
                params.width =LinearLayout.LayoutParams.MATCH_PARENT;
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.leftMargin=20;
                params.rightMargin=20;
                params.topMargin=5;
                //评论内容显示，html转为字符串保留格式
                String str1 = "<font color='#0997F7'>"+com.get(i).getCommentPerName()+": </font>"+com.get(i).getContent();
                comment.setText(Html.fromHtml(str1));
                comment.setLayoutParams(params);
                comment.setTextSize(16);
            }
        }
        else{
            holder.comments.removeAllViews();
            holder.comments.setVisibility(View.GONE);
        }


        if(isLikedList.get(position)==false) {
            holder.likebutton.setImageResource(R.drawable.dianzan);
            //Log.i("EEEE",)
        }
        else
            holder.likebutton.setImageResource(R.drawable.dianzanle);
        if(temp.getLikeCounter()>0){
            holder.likeCounter.setVisibility(View.VISIBLE);
            holder.likeCounter.setText(temp.getLikeCounter()+"");
        }else
            holder.likeCounter.setVisibility(View.INVISIBLE);
        //点赞按钮
        holder.likebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLikedList.get(position)==false) {
                    holder.likebutton.setImageResource(R.drawable.dianzanle);
                    isLikedList.set(position,true);
                    temp.setLikeCounter(temp.getLikeCounter()+1);
                    notifyDataSetChanged();
                    temp.updateAll("MomentID=?",temp.getMomentID());
                    like(temp.getMomentID());
                }
                else{
                    holder.likebutton.setImageResource(R.drawable.dianzanle);
                }

            }
        });
        //评论按钮
        holder.commentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view1 = LayoutInflater.from(context).inflate(R.layout.comments_dialog, null);//将布局文件转化为View
                Button btn=view1.findViewById(R.id.cancelbtn);
                Button btnCommit=view1.findViewById(R.id.send_comment_btn);
                EditText input=view1.findViewById(R.id.inputtext);
                builder.setView(view1);
                AlertDialog dialog = builder.create();  //AlertDialog create
                dialog.show();                          //show
                input.setFocusable(true);
                input.requestFocus();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) context
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    }
                }, 200);//这里的时间大概是自己测试的
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                    }
                });
                btnCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content=input.getText().toString();
                        Comment(temp.getMomentID(),content);
                        refreshData();
                        dialog.cancel();
                    }
                });
            }
        });
    }

    public void like(String MomentID){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String info="?shareID="+MomentID;
                    Request request = new Request.Builder()
                            .url(context.getString(R.string.IPAddress)+"/get-api/like"+info)
                            .build();

                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status.equals("200")){
                        JSONObject returnData = jsonObject.getJSONObject("data");
                        //Toast.makeText(context,"点赞成功！",Toast.LENGTH_SHORT).show();
                    }else {
                        JSONObject returnData = jsonObject.getJSONObject("data");
                        String msg = returnData.getString("msg");
                        //Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(context, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(context, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }


    public void Comment(String MomentID,String content){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Comments temp=new Comments();
                temp.setCommentPerID(userID);
                temp.setCommentPerName(userName);
                temp.setContent(content);
                temp.setMomentID(MomentID);
                temp.save();
                try{
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("shareID",MomentID)
                            .add("content",content)
                            .add("userID",userID)
                            .add("name",userName)
                            .build();
                    Request request = new Request.Builder()
                            .url(context.getString(R.string.IPAddress)+"/post-api/comment")
                            .post(requestBody)
                            .build();

                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    if (status.equals("200")){

                        //refreshData();
                        //Toast.makeText(context,"评论成功！",Toast.LENGTH_SHORT).show();

                    }else {
                        JSONObject returnData = jsonObject.getJSONObject("data");
                        String msg = returnData.getString("msg");
                        //Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(context, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(context, "网络连接错误,请检测你的网络连接", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return moments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView icon;
        TextView name;
        TextView time;
        TextView Text;
        TextView likeCounter;
        SimpleDraweeView imageSelected;
        SimpleDraweeView imageSelected2;
        SimpleDraweeView imageSelected3;
        ImageView likebutton;
        ImageView commentbutton;
        LinearLayout imagesGroup;
        LinearLayout comments;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.iconInMoments);
            name=itemView.findViewById(R.id.name);
            time=itemView.findViewById(R.id.time);
            Text=itemView.findViewById(R.id.text);
            likebutton=itemView.findViewById(R.id.likebutton);
            commentbutton=itemView.findViewById(R.id.commentbuttton);
            imagesGroup=itemView.findViewById(R.id.imagegroup);
            comments=itemView.findViewById(R.id.commentsgroup);
            likeCounter=itemView.findViewById(R.id.textView);
            imageSelected=null;
            imageSelected2=null;
            imageSelected3=null;



        }
    }

    private void initImageView(int imageID) {
        final WindowManager windowManager = activity.getWindowManager();
        final RelativeLayout relativeLayout = new RelativeLayout(context);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Display display= windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        layoutParams.width = width;
        layoutParams.height = height;
        //FLAG_LAYOUT_IN_SCREEN
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        layoutParams.format = PixelFormat.RGBA_8888;//让背景透明，放大过程可以看到当前界面
        layoutParams.verticalMargin = 0;
        windowManager.addView(relativeLayout,layoutParams);

        final PhotoView animationIV = new PhotoView(context);
        animationIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.addView(animationIV,params);
        relativeLayout.setFocusableInTouchMode(true);
        Picasso.with(context).load(imageID).into(animationIV);

        animationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(relativeLayout);
            }
        });

        relativeLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (null != windowManager && null != relativeLayout) {
                        windowManager.removeView(relativeLayout);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void returnBitMap(String url,ViewHolder holder,int i,ArrayList<ImageView> imageList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL myFileUrl = null;
                try {
                    myFileUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    imageList.get(i).post(new Runnable() {
                        @Override
                        public void run() {
                            imageList.get(i).setImageBitmap(bitmap);
                        }
                    });

                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
