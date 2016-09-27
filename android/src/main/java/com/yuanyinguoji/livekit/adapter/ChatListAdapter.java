package com.yuanyinguoji.livekit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyabing on 16/8/25.
 */

public class ChatListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    public List<AllMsgBean> mChatMsg = new ArrayList<>();


    public ChatListAdapter(Context context, List<AllMsgBean> mChatMsg) {
        this.context = context;
        this.mChatMsg = mChatMsg;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (mChatMsg!=null){
            return mChatMsg.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogUtil.log("getView()===ttttt555=="+mChatMsg.get(position).getConttext());
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.chat_item_msg, null);
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

    public void setNewData(List<AllMsgBean> mChatMsg){
        this.mChatMsg = mChatMsg;
    }

}
