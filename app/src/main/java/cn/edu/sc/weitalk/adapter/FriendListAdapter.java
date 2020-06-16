package cn.edu.sc.weitalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.UserA;

/**
 * Created by wangsong on 2016/4/24.
 */
public class FriendListAdapter extends BaseAdapter implements SectionIndexer {
    private List<UserA> list;
    private Context context;
    private LayoutInflater inflater;

    public FriendListAdapter(Context context, List<UserA> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

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
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_friend, null);
            holder = new ViewHolder();
            holder.showLetter = (TextView) convertView.findViewById(R.id.tv_index_friend_list);
            holder.username = (TextView) convertView.findViewById(R.id.tv_username_friend_list);
            holder.headIcon = (SimpleDraweeView)convertView.findViewById(R.id.head_ic_friend_list);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserA userA = list.get(position);
        holder.username.setText(userA.getUsername());
        holder.headIcon.setImageURI("res://drawable/" + userA.getImg());
        //获得当前position是属于哪个分组
        int sectionForPosition = getSectionForPosition(position);
        //获得该分组第一项的position
        int positionForSection = getPositionForSection(sectionForPosition);
        //查看当前position是不是当前item所在分组的第一个item
        //如果是，则显示showLetter，否则隐藏
        if (position == positionForSection) {
            holder.showLetter.setVisibility(View.VISIBLE);
            holder.showLetter.setText(userA.getFirstLetter());
        } else {
            holder.showLetter.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    //传入一个分组值[A....Z],获得该分组的第一项的position
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFirstLetter().charAt(0) == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    //传入一个position，获得该position所在的分组
    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getFirstLetter().charAt(0);
    }

    static class ViewHolder {
        TextView username, showLetter;
        SimpleDraweeView headIcon;
    }
}