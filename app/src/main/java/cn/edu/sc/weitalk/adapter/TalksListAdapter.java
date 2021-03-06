package cn.edu.sc.weitalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.MomentsMessage;
import cn.edu.sc.weitalk.javabean.Talks;

public class TalksListAdapter extends BaseAdapter {
    private List<Talks> list;
    private Context context;

    public TalksListAdapter(List<Talks> list, Context context){
        this.list=list;
        this.context=context;
        Collections.sort(list,comparator);
    }

    Comparator<Talks> comparator = new Comparator<Talks>() {
        public int compare(Talks o1, Talks o2) {
            return (int)(o2.getLastMessageDate()-o1.getLastMessageDate()); //升序
//return Integer.valueOf(s2) - Integer.valueOf(s1); //降序
        }
    };

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView==null){
            view= LayoutInflater.from(context).inflate(R.layout.message_list_item, parent, false);
            holder = new ViewHolder();
            holder.talks_img = view.findViewById(R.id.talks_img);
            holder.talksObj = view.findViewById(R.id.talksObj);
            holder.lastMessage = view.findViewById(R.id.lastMessage);
            view.setTag(holder);
        }else {
            view=convertView;
            holder=(ViewHolder)view.getTag();
        }

        holder.talks_img.setImageURI(context.getString(R.string.IPAddress)+list.get(position).getFriendHeaderURL());
        holder.talksObj.setText(list.get(position).getTalksName());
        holder.lastMessage.setText(list.get(position).getLastMessage());
        return view;
    }

    class ViewHolder{
        public SimpleDraweeView talks_img;
        public TextView talksObj,lastMessage;
    }
}
