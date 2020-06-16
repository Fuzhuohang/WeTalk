package cn.edu.sc.weitalk.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import cn.edu.sc.weitalk.R;

public class momentsMessageAdapter extends RecyclerView.Adapter<momentsMessageAdapter.ViewHolder>{

Context context;
    public ArrayList<String> messageTextList;
    public momentsMessageAdapter(Context context){
        messageTextList=new ArrayList<String>();
        for(int i=0;i<100;i++){
            messageTextList.add(i+"");
        }
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_moments_meaasge, parent, false);
        final ViewHolder holder=new ViewHolder(view);
        holder.Text.setText("有没有人想玩一把！(✿◠‿◠) ");
        holder.icon.setImageURI("res://drawable/" + R.drawable.dragon);
        for(int i=0;i<3;i++){
            ImageView image=new ImageView(context);
            holder.imagesGroup.addView(image);
            holder.imagesGroup.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) image.getLayoutParams();
            params.width = 300;
            params.height = 300;
            image.setImageResource(R.drawable.dragon);
            image.setLayoutParams(params);
            }
        TextView comment=new TextView(context);
        holder.comments.addView(comment);
        holder.comments.setVisibility(View.VISIBLE);
        holder.comments.setBackgroundResource(R.drawable.fillet);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) comment.getLayoutParams();
        params.width =LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        String str1 = "<font color='#0997F7'>李佳峻:</font>我要上号！我要上号！我要上号！我要上号！我要上号！我要上号！我要上号！我要上号！我要上号！我要上号！";
        comment.setText(Html.fromHtml(str1));
        comment.setLayoutParams(params);
        comment.setTextSize(16);
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
        holder.icon.setImageURI("res://drawable/" + R.drawable.dragon);
        holder.Text.setText("有没有人想玩一把！(✿◠‿◠) ");
        //if(holder.likebutton.get

    }

    @Override
    public int getItemCount() {
        return messageTextList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView icon;
        TextView name;
        TextView time;
        TextView Text;
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

        }
    }
}
