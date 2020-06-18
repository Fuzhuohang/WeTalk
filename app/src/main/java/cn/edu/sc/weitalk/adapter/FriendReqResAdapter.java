package cn.edu.sc.weitalk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.javabean.Friend;
import cn.edu.sc.weitalk.javabean.FriendReqRes;
import cn.edu.sc.weitalk.widget.MyDialog;

public class FriendReqResAdapter extends RecyclerView.Adapter<FriendReqResAdapter.ViewHolder> {

    private final String TAG = "FriendReqResAdapter";
    private Context context;
    private List<FriendReqRes> list;

    public FriendReqResAdapter(Context context, List<FriendReqRes> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_friend_req_res, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendReqRes reqRes = list.get(position);
        holder.tvAddFriendReqUsername.setText(reqRes.getUsername());
        //根据FriendReqRes类型的不同来更改点的颜色和文字描述
        switch(reqRes.getType()){
            case FriendReqRes.NEW_FRIEND_REQUEST:
                holder.littlePoint.setBackground(context.getResources().getDrawable(R.drawable.big_letter_dialog));
                holder.tvReqResDesc.setText("请求加你为好友");
                break;
            case FriendReqRes.NEW_FRIEND_RESPONSE:
                if(reqRes.isAgreed()) {
                    holder.littlePoint.setBackground(context.getResources().getDrawable(R.drawable.green_point));
                    holder.tvReqResDesc.setText("同意了你的好友申请");
                }
                else {
                    holder.littlePoint.setBackground(context.getResources().getDrawable(R.drawable.red_point));
                    holder.tvReqResDesc.setText("拒绝了你的好友申请");
                }
                break;
            case FriendReqRes.DELETE_BY_FRIEND:
                holder.littlePoint.setBackground(context.getResources().getDrawable(R.drawable.red_point));
                holder.tvReqResDesc.setText("将你从好友列表中删除了");
                break;
        }
        //给列表项添加点击事件，打开对应对话框
        holder.tvReqResDesc.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendReqRes reqRes = list.get(position);
                switch(reqRes.getType()){
                    //回复好友请求，打开对话框，可设置备注，拒绝or同意
                    case FriendReqRes.NEW_FRIEND_REQUEST:
                        MyDialog dialog = new MyDialog(context, 900, 600);
                        View view = LayoutInflater.from(context).inflate(R.layout.dialog_response_req, null);
                        dialog.setContentView(view);
//                        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//                        params.width = 680;
//                        params.height = 440;
//                        dialog.getWindow().setAttributes(params);
                        dialog.show();
                        SimpleDraweeView headIc = view.findViewById(R.id.head_ic_res_add_friend);
                        TextView tvUsername = view.findViewById(R.id.tv_username_res_add_friend);
                        TextView tvId = view.findViewById(R.id.tv_id_res_add_friend);
                        EditText edtNote = view.findViewById(R.id.edt_note_res_add_friend);
                        Button btnAccept = view.findViewById(R.id.btn_accept_friend);
                        Button btnReject = view.findViewById(R.id.btn_reject_friend);
                        ImageView btnCancel = view.findViewById(R.id.btn_cancel_res_add_friend);
                        //headIc.setImageURI(Uri.parse(reqRes.getHeadUrl()));
                        tvUsername.setText(reqRes.getUsername());
                        tvId.setText(reqRes.getUserId());
                        //取消，关闭dialog
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
                        //同意，将好友数据添加到Friend数据库，向服务器发送同意好友申请的消息，并删除掉FriendReqRes数据库中的数据
                        btnAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //添加数据到Friend数据库
                                Friend friend = new Friend();
                                /**
                                 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                 * friend.setImg();
                                 */
                                friend.setUserID(reqRes.getUserId());
                                friend.setUsername(reqRes.getUsername());
                                friend.setNote(edtNote.getText().toString());
                                friend.save();
                                /**
                                 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                 * 向服务器发送同意好友申请的请求
                                 */
                                //将数据从FriendReqRes中删除
                                if(reqRes.isSaved())
                                    reqRes.delete();
                                else
                                    Log.i(TAG, "reqRes is not saved");
                                //test
                                list.remove(position);
                                notifyDataSetChanged();

                                dialog.cancel();
                                Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //拒绝，删除掉FriendReqRes数据库中的数据，并向服务器发送拒绝好友申请的消息
                        btnReject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /**
                                 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                 * 向服务器发送拒绝好友申请的请求
                                 */
                                //将数据从FriendReqRes中删除
                                if(reqRes.isSaved())
                                    reqRes.delete();
                                else
                                    Log.i(TAG, "reqRes is not saved");
                                //test
                                list.remove(position);
                                notifyDataSetChanged();

                                dialog.cancel();
                            }
                        });
                        break;
                    //对方的回复
                    case FriendReqRes.NEW_FRIEND_RESPONSE:
                        if(reqRes.isAgreed()) {
                            Friend friend = new Friend();
                            friend.setUsername(reqRes.getUsername());
                            friend.setUserID(reqRes.getUserId());
                            /**
                             * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                             * friend.setImg();
                             */
                            friend.save();
                        }
                        else {
                            //do nothing
                        }
                        //将数据从FriendReqRes中删除
                        if(reqRes.isSaved())
                            reqRes.delete();
                        else
                            Log.i(TAG, "reqRes is not saved");
                        //test
                        list.remove(position);
                        notifyDataSetChanged();
                        break;
                    //收到被对方删除好友的消息
                    case FriendReqRes.DELETE_BY_FRIEND:
                        //删除Friend数据库中数据
                        DataSupport.deleteAll(Friend.class, "userId=?", reqRes.getUserId());
                        //将数据从FriendReqRes中删除
                        if(reqRes.isSaved())
                            reqRes.delete();
                        else
                            Log.i(TAG, "reqRes is not saved");
                        //test
                        list.remove(position);
                        notifyDataSetChanged();
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_add_friend_req_username)
        TextView tvAddFriendReqUsername;
        @BindView(R.id.little_point)
        TextView littlePoint;
        @BindView(R.id.tv_req_res_desc)
        TextView tvReqResDesc;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setList(List<FriendReqRes> list) {
        this.list = list;
    }
}