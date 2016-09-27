package com.yuanyinguoji.livekit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.Bean.PriMsgBean;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by chenyabing on 16/8/26.
 */

public class PriMsgAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> implements View.OnClickListener{
    private Context context;
    List<AllMsgBean> msgList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    //建立枚举 2个item 类型
    public enum ITEM_TYPE {
        ITEM1,
        ITEM2
    }


    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, AllMsgBean data);
    }

    public PriMsgAdapter(Context context, List<AllMsgBean> msgList) {
        this.context = context;
        this.msgList = msgList;
        mLayoutInflater = LayoutInflater.from(context);
        LogUtil.log("msgList===tttt===" + msgList.toString());
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.msg_pri_gift_layout, parent,
                false);
        PriViewHolder priViewHolder = new PriViewHolder(view);
        //给创建的view创建点击事件
        view.setOnClickListener(this);
        return priViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //将数据保存在itemview的tag中  点击的时候存取
        holder.itemView.setTag(msgList.get(position));

        if (msgList.get(position).getType().equals("gift_send")){
            ((PriViewHolder)holder).llGiftMsgLayot.setVisibility(View.VISIBLE);
            ((PriViewHolder)holder).tvMSg.setVisibility(View.GONE);
            ((PriViewHolder)holder).tvGiftMsg.setText(msgList.get(position).getGiftname());
        }else if(msgList.get(position).getType().equals("private_mail")){
            ((PriViewHolder)holder).llGiftMsgLayot.setVisibility(View.GONE);
            ((PriViewHolder)holder).tvMSg.setVisibility(View.VISIBLE);
            ((PriViewHolder)holder).tvMSg.setText(msgList.get(position).getConttext());
        }
        ((PriViewHolder)holder).tvNickName.setText(msgList.get(position).getUsername());
        Glide.with(context)
                .load(msgList.get(position).getPhoto())
                .centerCrop()
                .placeholder(R.drawable.qiniu_logo)
                .crossFade()
                .into(((PriViewHolder)holder).userHeader);
    }


    /**
     * 这是一个添加一条数据并刷新界面的方法
     *
     * @param
     */
    public void addData(AllMsgBean msg) {
        msgList.add(msgList.size(), msg);
        notifyItemInserted(msgList.size());
    }


//    public int getItemViewType(int position) {
//        if (msgList.get(position).getSenderid().equals("0")) {
//            return ITEM_TYPE.ITEM1.ordinal();
//        }else {
//            return ITEM_TYPE.ITEM2.ordinal();
//        }
//    }

    @Override
    public int getItemCount() {
        return msgList == null ? 0:msgList.size();
//        return giftLists.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(AllMsgBean) v.getTag());
        }

    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public class PriViewHolder extends RecyclerView.ViewHolder {
        private ImageView userHeader;
        private TextView tvMSg,tvNickName,tvGiftMsg;
        private LinearLayout llGiftMsgLayot;
        public PriViewHolder(View itemView) {
            super(itemView);
            userHeader = (CircleImageView)itemView.findViewById(R.id.iv_left_header);
            tvMSg = (TextView) itemView.findViewById(R.id.tv_left_msg);
            tvNickName = (TextView)itemView.findViewById(R.id.tv_nickname);
            tvGiftMsg = (TextView) itemView.findViewById(R.id.tv_gift_msg);
            llGiftMsgLayot = (LinearLayout) itemView.findViewById(R.id.ll_gift_msg_layout);
        }
    }

}
