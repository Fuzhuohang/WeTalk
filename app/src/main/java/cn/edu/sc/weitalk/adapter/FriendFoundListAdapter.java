package cn.edu.sc.weitalk.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.Friend;

public class FriendFoundListAdapter extends RecyclerView.Adapter<FriendFoundListAdapter.ViewHolder> {

    List<Friend> list;
    String MyID;

    public FriendFoundListAdapter(List<Friend> list, String MyID) {
        this.list = list;
        this.MyID = MyID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_found_friend, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend friend = list.get(position);
        //note(username)
        if(friend.getNote() == null || friend.getNote().isEmpty())
            holder.tvUsernameFoundFriend.setText(friend.getUsername());
        else
            holder.tvUsernameFoundFriend.setText(friend.getNote() + "（" + friend.getUsername() + "）");
        //如果不是本地好友，设置提示添加好友，可见
        if( !friend.getMyID().equals(MyID))
            holder.tvAddFriendTipFoundFiend.setVisibility(View.VISIBLE);
        else
            holder.tvAddFriendTipFoundFiend.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.head_ic_found_friend)
        SimpleDraweeView headIcFoundFriend;
        @BindView(R.id.tv_username_found_friend)
        TextView tvUsernameFoundFriend;
        @BindView(R.id.tv_add_friend_tip_found_fiend)
        TextView tvAddFriendTipFoundFiend;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setList(List<Friend> list) {
        this.list = list;
    }
}
