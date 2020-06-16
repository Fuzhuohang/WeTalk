package cn.edu.sc.weitalk.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.Comments;
import cn.edu.sc.weitalk.javabean.MomentsMessage;

public class momentsMessageAdapter extends RecyclerView.Adapter<momentsMessageAdapter.ViewHolder>{

Context context;
    public ArrayList<String> messageTextList;
    public ArrayList<Comments> comments;
    public ArrayList<MomentsMessage> moments;
    public ArrayList<Bitmap> icons;
    public ArrayList<Bitmap> contentImages;
    public momentsMessageAdapter(Context context){
        messageTextList=new ArrayList<String>();
        for(int i=0;i<100;i++){
            messageTextList.add(i+"");
        }
        this.context=context;
        moments=null;
        comments=null;
        refreshData();
    }

    public  void refreshData(){//更新数据库
        comments= (ArrayList<Comments>)DataSupport.findAll(Comments.class);
        moments=(ArrayList<MomentsMessage>)DataSupport.findAll(MomentsMessage.class);
//        if(moments.size()>0){
//            icons=new ArrayList<Bitmap>();
//            //加载图标到Bitmap列表中拿过去
//            for(int i=0;i<moments.size();i++){
//                MomentsMessage temp=moments.get(i);
//                byte[] headshot=temp.getHeadshot();
//                Bitmap bitmap= BitmapFactory.decodeByteArray(headshot,0,headshot.length);
//                icons.add(bitmap);
////                //如果消息中有图片，加载图片到bitmap列表
////                if(temp.getMomentImage()!=null){
////
////                }
//            }
//        }
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_moments_meaasge, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        MomentsMessage temp = moments.get(viewType);
        //设置发表人名字
        holder.name.setText(temp.getPublisherName());
        //设置发起时间
        holder.time.setText(temp.getDate());
        //消息文本
        holder.Text.setText(temp.getContent());

//        设置头像，bitmap转为URI，后显示为图片
        holder.icon.setImageURI(temp.getHeadshot());
        //消息图片
            if(!(" ".equals(temp.getMomentImage()))) {
                holder.imageSelected = new ImageView(context);
                holder.imagesGroup.setVisibility(View.VISIBLE);
                holder.imagesGroup.addView(holder.imageSelected);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.imageSelected.getLayoutParams();
                params.width = 300;
                params.height = 300;
                holder.imageSelected.setImageURI(Uri.parse(temp.getMomentImage()));
                holder.imageSelected.setLayoutParams(params);
            }
        //动态生成评论
//        TextView comment=new TextView(context);
//        holder.comments.addView(comment);
//        holder.comments.setVisibility(View.VISIBLE);
//        holder.comments.setBackgroundResource(R.drawable.fillet);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) comment.getLayoutParams();
//        params.width =LinearLayout.LayoutParams.MATCH_PARENT;
//        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        //评论内容显示，html转为字符串保留格式
//        String str1 = "<font color='#0997F7'>"+comments.get(1).getCommentPerName()+":</font>"+comments.get(0).getContent();
//        comment.setText(Html.fromHtml(str1));
//        comment.setLayoutParams(params);
//        comment.setTextSize(16);
        //
        holder.likebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.likebutton.setImageResource(R.drawable.dianzanle);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MomentsMessage temp = moments.get(position);
        holder.name.setText(temp.getPublisherName());
        //设置发起时间
        holder.time.setText(temp.getDate());
        //消息文本
        holder.Text.setText(temp.getContent());
        //设置头像，bitmap转为URI，后显示为图片
        //Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), icons.get(position), null, null));
        //holder.icon.setImageURI(uri);
        holder.icon.setImageURI(temp.getHeadshot());
        //消息图片
        if(!(" ".equals(temp.getMomentImage()))){
            if(holder.imageSelected!=null) {
                holder.imagesGroup.setVisibility(View.VISIBLE);
                holder.imageSelected.setImageURI(Uri.parse(temp.getMomentImage()));
            }
            else {
                holder.imageSelected = new ImageView(context);
                holder.imagesGroup.addView(holder.imageSelected);
                holder.imagesGroup.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.imageSelected.getLayoutParams();
                params.width = 300;
                params.height = 300;
                holder.imageSelected.setImageURI(Uri.parse(temp.getMomentImage()));
                holder.imageSelected.setLayoutParams(params);
            }

        }

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
        ImageView imageSelected;
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
            imageSelected=null;

        }
    }
}
