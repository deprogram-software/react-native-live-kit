package com.yuanyinguoji.livekit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.Bean.MsgBean;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.util.LogUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by chenyabing on 16/8/26.
 */

public class MsgAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> implements View.OnClickListener{
    private Context context;
    private List<String> giftNameList ;
    private List<String> giftPriceList ;
    List<AllMsgBean> msgList;
    private LayoutInflater mLayoutInflater;

    //建立枚举 2个item 类型
    public enum ITEM_TYPE {
        ITEM1,
        ITEM2
    }

    public MsgAdapter(Context context, List<String> giftNameList, List<String> giftPriceList) {
        this.context = context;
        this.giftNameList = giftNameList;
        this.giftPriceList = giftPriceList;

    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, MsgBean.ListBean data);
    }

    public MsgAdapter(Context context, List<AllMsgBean> msgList) {
        this.context = context;
        this.msgList = msgList;
        mLayoutInflater = LayoutInflater.from(context);
        LogUtil.log("msgList===tttt===" + msgList.toString());
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM1.ordinal()) {
            return new LeftViewHolder(mLayoutInflater.inflate(R.layout.msg_left_layout,parent,false));
        }else {
            return new RightViewHolder(mLayoutInflater.inflate(R.layout.msg_right_layout,parent,false));
        }
//        View view = LayoutInflater.from(
//                context).inflate(R.layout.gift_item, parent,
//                false);
//        GiftViewHolder giftViewHolder = new GiftViewHolder(view);
//        //给创建的view创建点击事件
//        view.setOnClickListener(this);
//        return giftViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //将数据保存在itemview的tag中  点击的时候存取
        holder.itemView.setTag(msgList.get(position));
        if (holder instanceof LeftViewHolder) {
            ((LeftViewHolder)holder).tvMSg.setText(msgList.get(position).getConttext());
            Glide.with(context)
                    .load(msgList.get(position).getPhoto())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(((LeftViewHolder)holder).userHeader);
        }else {
            ((RightViewHolder)holder).tvMSg.setText(msgList.get(position).getConttext());
            Glide.with(context)
                    .load(msgList.get(position).getPhoto())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(((RightViewHolder) holder).userHeader);
        }
//        holder.tvGiftPrice.setText(giftLists.get(position).getPrice());
//        holder.tvGiftName.setText(giftLists.get(position).getName());

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


    public int getItemViewType(int position) {
        if (msgList.get(position).getSendAndReId().equals("0")) {
            return ITEM_TYPE.ITEM1.ordinal();
        }else {
            return ITEM_TYPE.ITEM2.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return msgList == null ? 0:msgList.size();
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
        private ImageView userHeader;
        private TextView tvMSg;
        public LeftViewHolder(View itemView) {
            super(itemView);
            userHeader = (CircleImageView)itemView.findViewById(R.id.iv_left_header);
            tvMSg = (TextView) itemView.findViewById(R.id.tv_left_msg);
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder {
        private ImageView userHeader;
        private TextView tvMSg;
        public RightViewHolder(View itemView) {
            super(itemView);
            userHeader = (CircleImageView)itemView.findViewById(R.id.iv_right_header);
            tvMSg = (TextView) itemView.findViewById(R.id.tv_right_msg);

        }
    }
}
