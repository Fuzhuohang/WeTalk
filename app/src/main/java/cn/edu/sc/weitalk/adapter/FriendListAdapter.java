package cn.edu.sc.weitalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.Friend;

/**
 * Created by wangsong on 2016/4/24.
 */
public class FriendListAdapter extends BaseAdapter implements SectionIndexer {
    private List<Friend> list;
    private Context context;
    private LayoutInflater inflater;

    public FriendListAdapter(Context context, List<Friend> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        listOrderedByPinyin();
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
        Friend friend = list.get(position);
        //给好友有备注，设置文字为备注，否则设置文字为username
        if(friend.getNote() == null || friend.getNote().isEmpty())
            holder.username.setText(friend.getUsername());
        else
            holder.username.setText(friend.getNote());
        /********************************************************************************头像图片******************/
        holder.headIcon.setImageURI(context.getString(R.string.IPAddress) + friend.getImg());//holder.headIcon.setImageURI("res://drawable/" + R.drawable.tu);
        //获得当前position是属于哪个分组
        int sectionForPosition = getSectionForPosition(position);
        //获得该分组第一项的position
        int positionForSection = getPositionForSection(sectionForPosition);
        //查看当前position是不是当前item所在分组的第一个item
        //如果是，则显示showLetter，否则隐藏
        if (position == positionForSection) {
            holder.showLetter.setVisibility(View.VISIBLE);
            holder.showLetter.setText(friend.getFirstLetter());
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
        //如果sectionIndex == -1，说明为空list；或者Friend中没有firstLetter的数据
        if(sectionIndex == -1)
            return -1;
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
        if(list.isEmpty())
            return -1;
        String firstLetter = list.get(position).getFirstLetter();
        if(firstLetter == null || firstLetter.isEmpty())
            return -1;
        return firstLetter.charAt(0);
    }

    static class ViewHolder {
        TextView username, showLetter;
        SimpleDraweeView headIcon;
    }

    public void setList(List<Friend> list) {
        this.list = list;
        listOrderedByPinyin();
    }

    //初始化每一个Friend的拼音，并更具字母顺序排序friend list
    private void listOrderedByPinyin() {
        //对于每一个Friend，如果有好友备注，用备注转换得到拼音，否则用用户名转换得到
        for(int i = 0;i < list.size();i++){
            Friend friend = list.get(i);
            String convert;
            if(friend.getNote() == null || friend.getNote().isEmpty())
                convert = Pinyin.toPinyin(friend.getUsername(), " ");
            else
                convert = Pinyin.toPinyin(friend.getNote(), " ");
            friend.setPinyin(convert);
            String firstLetter = convert.substring(0, 1);
            if(firstLetter.matches("[A-Z]"))
                friend.setFirstLetter(firstLetter);
            else
                friend.setFirstLetter("#");
        }
        Collections.sort(list, new Comparator<Friend>() {
            @Override
            public int compare(Friend lhs, Friend rhs) {
                if (lhs.getFirstLetter().contains("#")) {
                    return 1;
                } else if (rhs.getFirstLetter().contains("#")) {
                    return -1;
                }else{
                    return lhs.getFirstLetter().compareTo(rhs.getFirstLetter());
                }
            }
        });
    }
}