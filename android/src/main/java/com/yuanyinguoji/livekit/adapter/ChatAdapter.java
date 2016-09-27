package com.yuanyinguoji.livekit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.Bean.MsgBean;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by chenyabing on 16/8/26.
 */

public class ChatAdapter extends RecyclerView.Adapter <ChatAdapter.LeftViewHolder> implements View.OnClickListener{

    private Context context;
    List<AllMsgBean> msgList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;


    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, MsgBean.ListBean data);
    }

    public ChatAdapter(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public ChatAdapter(Context context, List<AllMsgBean> msgList) {
        this.context = context;
        this.msgList = msgList;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public LeftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.log("onCreateViewHolder===tttt===");

        return new LeftViewHolder(mLayoutInflater.inflate(R.layout.chat_item_msg,parent,false));
    }

    @Override
    public void onBindViewHolder(LeftViewHolder holder, int position) {
        //将数据保存在itemview的tag中  点击的时候存取
        holder.itemView.setTag(msgList.get(position));
        LogUtil.log("onBindViewHolder===tttt===" + msgList.toString());

        holder.tvMSg.setText(msgList.get(position).getConttext());
        holder.tvUserName.setText(msgList.get(position).getUsername()+":");
    }


    /**
     * 这是一个添加一条数据并刷新界面的方法
     *
     * @param
     */
    public void addData(AllMsgBean msg) {
        msgList.add(msgList.size(), msg);
        if (msgList.size()>20) {
            int a = msgList.size()-20;
            for (int i = 0; i < a; i++) {
                msgList.remove(0);
            }
        }
        notifyItemInserted(msgList.size());
        LogUtil.log("addData==ttt");
    }


//    public int getItemViewType(int position) {
//        if (msgList.get(position).getSendAndReId().equals("0")) {
//            return ITEM_TYPE.ITEM1.ordinal();
//        }else {
//            return ITEM_TYPE.ITEM2.ordinal();
//        }
//    }

    @Override
    public int getItemCount() {
        LogUtil.log("msgList.size()===ttt=="+msgList.size());
        return msgList.size()>0? msgList.size():0;
//        return giftLists.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(MsgBean.ListBean)v.getTag());
        }

    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMSg;
        private TextView tvUserName;
        public LeftViewHolder(View itemView) {
            super(itemView);
            tvMSg = (TextView) itemView.findViewById(R.id.tv_chat_msg);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_mame);
        }
    }

}
