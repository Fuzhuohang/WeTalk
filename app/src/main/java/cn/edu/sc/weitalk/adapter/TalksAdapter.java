package cn.edu.sc.weitalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.edu.sc.weitalk.R;
import cn.edu.sc.weitalk.activity.TalksActivity;
import cn.edu.sc.weitalk.javabean.Message;

public class TalksAdapter extends BaseAdapter {

    public static interface IMsgViewType{
        //收到的信息
        int IMVT_COM_MSG=0;
        //发出的信息
        int IMVT_TO_MSG=1;
    }

    private static final String TAG = TalksAdapter.class.getSimpleName();
    private ArrayList data;
    private Context context;
    private LayoutInflater mInflater;

    public TalksAdapter(Context context, ArrayList data){
        this.context= context;
        this.data = data;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position){
        Message message = (Message) data.get(position);

        if (message.getMsgType()){
            return IMsgViewType.IMVT_COM_MSG;
        }else {
            return IMsgViewType.IMVT_TO_MSG;
        }
    }

    public int getViewTypeCount(){
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = (Message)data.get(position);
        boolean isComMsg=message.getMsgType();

        ViewHolder viewHolder=null;
        if (convertView == null){
            if (isComMsg){
                convertView=mInflater.inflate(R.layout.get_message_item,null);
            }else {
                convertView=mInflater.inflate(R.layout.send_message_item,null);
            }

            viewHolder=new ViewHolder();
            viewHolder.header_img = convertView.findViewById(R.id.header_img);
            viewHolder.talks_message = convertView.findViewById(R.id.talks_message);
            viewHolder.isComMsg=isComMsg;

            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.header_img.setImageURI(message.getHeader_img());
        viewHolder.talks_message.setText(message.getMsgText());

        return convertView;
    }

    static class ViewHolder{
        public TextView talks_message;
        public SimpleDraweeView header_img;
        public boolean isComMsg=true;
    }
}
