package com.yuanyinguoji.livekit.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.util.LogUtil;

import java.util.List;

/**
 * Created by chenyabing on 16/9/13.
 */

public class MyAdapter extends ArrayAdapter<AllMsgBean> {
    int resource;
    private Context context;
    private List<AllMsgBean> mChatMsg;
    public MyAdapter(Context context, int _resource, List<AllMsgBean> mChatMsg){
        super(context, _resource, mChatMsg);
        this.resource = _resource;
        this.context = context;
        this.mChatMsg = mChatMsg;
    }

    @Override
    public int getCount() {
        LogUtil.log(".........."+mChatMsg.size());
        return mChatMsg.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, resource, null);
            holder = new ViewHolder();
            holder.chatMsg = (TextView) convertView.findViewById(R.id.tv_chat_msg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LogUtil.log("getView()===ttttt555=="+mChatMsg.get(position).getConttext());
        holder.chatMsg.setText(mChatMsg.get(position).getConttext());
        return convertView;

    }

    public static class ViewHolder {
        public TextView chatMsg;
    }
}
