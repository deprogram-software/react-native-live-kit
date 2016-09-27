package com.yuanyinguoji.livekit.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.yuanyinguoji.livekit.Bean.GiftBean;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.util.CustomShapeTransformation;
import com.yuanyinguoji.livekit.util.LogUtil;

import java.util.List;

/**
 * Created by chenyabing on 16/8/26.
 */

public class GiftAdapter extends RecyclerView.Adapter <GiftAdapter.GiftViewHolder> implements View.OnClickListener{
    private Context context;
    private List<String> giftNameList ;
    private List<String> giftPriceList ;
    private List<GiftBean.GiftList> giftLists;
    public GiftAdapter(Context context,List<String> giftNameList,List<String> giftPriceList) {
        this.context = context;
        this.giftNameList = giftNameList;
        this.giftPriceList = giftPriceList;
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , GiftBean.GiftList data);
    }

    public GiftAdapter(Context context,List<GiftBean.GiftList> giftLists) {
        this.context = context;
        this.giftLists = giftLists;
    }
    @Override
    public GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                context).inflate(R.layout.gift_item, parent,
                false);
        GiftViewHolder giftViewHolder = new GiftViewHolder(view);
        //给创建的view创建点击事件
        view.setOnClickListener(this);
        return giftViewHolder;
    }

    @Override
    public void onBindViewHolder(GiftViewHolder holder, int position) {
        //将数据保存在itemview的tag中  点击的时候存取
        holder.itemView.setTag(giftLists.get(position));
        LogUtil.log("giftLists.get(position)==tttt=="+giftLists.get(position).toString());

        holder.tvGiftPrice.setText(giftLists.get(position).getPrice());
        holder.tvGiftName.setText(giftLists.get(position).getName());

        Glide.with(context)
                .load(giftLists.get(position).getPhotoname())
                .override(100,100)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher) //设置占位图
                .error(R.mipmap.ic_launcher) //设置错误图片
                .crossFade() //设置淡入淡出效果，默认300ms，可以传参
                .dontAnimate() //不显示动画效果
                .into(holder.ivGift);

//        Glide.with(context)
//                .load(giftLists.get(position).getPhotoname())
//                .transform(new CustomShapeTransformation(context,R.drawable.ic_gift_default))
//                .centerCrop()
//                .placeholder(R.drawable.ic_gift_default) //设置占位图
//                .error(R.drawable.ic_gift_default) //设置错误图片
//                .crossFade() //设置淡入淡出效果，默认300ms，可以传参
//                .dontAnimate() //不显示动画效果
//                .into(holder.ivGift);
    }



    @Override
    public int getItemCount() {
        LogUtil.log("giftLists.size()====ttt="+giftLists.size());
        return (giftLists.size()>0)?giftLists.size():0;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(GiftBean.GiftList)v.getTag());
        }

    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public class GiftViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivGift;
        private TextView tvGiftName;
        private TextView tvGiftPrice;
        public GiftViewHolder(View itemView) {
            super(itemView);
            ivGift = (ImageView) itemView.findViewById(R.id.iv_gift);
            tvGiftName = (TextView) itemView.findViewById(R.id.tv_gift_name);
            tvGiftPrice = (TextView) itemView.findViewById(R.id.tv_gift_price);

        }
    }
}
