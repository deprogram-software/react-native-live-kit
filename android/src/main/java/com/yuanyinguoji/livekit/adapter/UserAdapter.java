package com.yuanyinguoji.livekit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.yuanyinguoji.livekit.Bean.UserBean;
import com.yuanyinguoji.livekit.Bean.WatchUserListBean;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.util.LogUtil;


import java.util.List;

/**
 * Created by chenyabing on 16/8/26.
 */

public class UserAdapter extends RecyclerView.Adapter <UserAdapter.UserViewHolder> implements View.OnClickListener{
    private Context context;
    private List<String> giftNameList ;
    private List<String> giftPriceList ;
    private List<WatchUserListBean.ListBean> userListBean;
    public UserAdapter(Context context, List<String> giftNameList, List<String> giftPriceList) {
        this.context = context;
        this.giftNameList = giftNameList;
        this.giftPriceList = giftPriceList;
    }



    public UserAdapter(Context context, List<WatchUserListBean.ListBean> userListBean) {
        this.context = context;
        this.userListBean = userListBean;
    }
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                context).inflate(R.layout.user_list_item, parent,
                false);
        UserViewHolder userViewHolder = new UserViewHolder(view);
        //给创建的view创建点击事件
        view.setOnClickListener(this);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        if (userListBean != null) {
            //将数据保存在itemview的tag中  点击的时候存取
            holder.itemView.setTag(userListBean.get(position));
            Glide.with(context)
                    .load(userListBean.get(position).getPhoto())
                    .centerCrop()
                    .into(holder.ivUserHeader);
        }
    }



    @Override
    public int getItemCount() {

        return (userListBean.size()>0)?userListBean.size():0;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v,(WatchUserListBean.ListBean)v.getTag(),p);
        }

    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, WatchUserListBean.ListBean data,int position);
    }

    int p;
    public class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivUserHeader;
//        private CheckBox rbSelect;
        private RadioButton rbSelect;

        public UserViewHolder(View itemView) {
            super(itemView);
            p = getAdapterPosition();
            ivUserHeader = (ImageView) itemView.findViewById(R.id.iv_user_header);
            rbSelect = (RadioButton) itemView.findViewById(R.id.rb_select);

        }
    }
}
