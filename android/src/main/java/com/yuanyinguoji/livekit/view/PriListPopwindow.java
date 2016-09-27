package com.yuanyinguoji.livekit.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.Bean.Constants;
import com.yuanyinguoji.livekit.Bean.PriMsgBean;
import com.yuanyinguoji.livekit.Lisener.IListener;
import com.yuanyinguoji.livekit.Lisener.ListenerManager;
import com.yuanyinguoji.livekit.LiveViewManager;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.adapter.PriMsgAdapter;
import com.yuanyinguoji.livekit.util.DensityUtil;
import com.yuanyinguoji.livekit.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chenyabing on 16/8/26.
 */

public class PriListPopwindow extends PopupWindow implements View.OnClickListener,IListener {

    private View mPriMsgpView;
    private OnItemClickListener mListener;
    private RecyclerView mPriMsgRecycleview;

    private Context context;
    private ImageView ivClose;
    private PriMsgAdapter priMsgListAdapter;
//    List<MsgBean> list = new ArrayList<>();
    public PriListPopwindow(Context context) {
        super(context);
        this.context = context;
        ListenerManager.getInstance().registerListtener(this);
        init(context);
        setPopupWindow();
        getSendMsgBean();

    }


    //初始化
    private void init(Context context){

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mPriMsgpView = layoutInflater.inflate(R.layout.priv_msg_list,null);
        mPriMsgRecycleview = (RecyclerView) mPriMsgpView.findViewById(R.id.send_msg_recycleview);
        mPriMsgRecycleview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
        mPriMsgRecycleview.addItemDecoration(new SpaceItemDecoration(3));

        priMsgListAdapter = new PriMsgAdapter(context,priMsgList);
        mPriMsgRecycleview.setAdapter(priMsgListAdapter);
        ivClose = (ImageView) mPriMsgpView.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);

        priMsgListAdapter.setOnItemClickListener(new PriMsgAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, AllMsgBean data) {
                dismiss();
                data.setAhchorPri(true);
                ListenerManager.getInstance().sendBroadCast(data);
            }
        });
//        mSendMsgRecycleview.getLayoutParams().height = (int)(DensityUtil.getHeight(context)*0.4f);

    }
    //设置窗口相关属性
    private void setPopupWindow(){
        this.setContentView(mPriMsgpView);// 设置View
        this.setWidth(LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
//        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明
        mPriMsgpView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = mPriMsgpView.findViewById(R.id.id_pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void notifyAllActivity(String str) {

    }
    private List<AllMsgBean> priMsgList = new ArrayList<>();
    @Override
    public void notifyUpdatePage(AllMsgBean allMsgBean) {

        Message msg  = refreshMsgHandler.obtainMessage();
        msg.obj = allMsgBean;
        msg.what = Constants.MSG_PRI_GIFT_SEND;
        refreshMsgHandler.sendMessage(msg);
        LogUtil.log("received==私信列表信息===ttt==="+allMsgBean.getType());
    }


    /**
     * 定义一个接口，公布出去 在Activity中操作按钮的单击事件
     */
    public interface OnItemClickListener {
        void setOnItemClick(View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
//    private MsgAdapter msgAdapter;
    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.setOnItemClick(v);
        }

//        switch (v.getId()){
//            case R.id.iv_close:
//                if (mPriMsgpView.isShown()) {
////                    msgBeanList.clear();
//                    dismiss();
//                }
//                break;
//
//            default:
//                break;
//
//        }

    }


    private void getReceivedMsgBean() {

    }
    private Handler refreshMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                switch (msg.what){
                    case Constants.MSG_PRI_GIFT_SEND:
                        if (priMsgList.size() > 0) {
                            priMsgListAdapter.addData((AllMsgBean)msg.obj);
                            priMsgListAdapter.notifyDataSetChanged();
                            mPriMsgRecycleview.smoothScrollToPosition(priMsgList.size()-1);
                        }else {
                            priMsgList.add((AllMsgBean)msg.obj);
                        }
                    break;
                    default:
                        break;

                }

        }
    };


    private List<PriMsgBean.ListBean> msgBeanList = new ArrayList<>();
    private void getSendMsgBean(){
        for (int i = 0; i < 5; i++) {
            PriMsgBean.ListBean listBean = new PriMsgBean.ListBean();
            listBean.setConttext("12121212");
            listBean.setSenderid("1");
            msgBeanList.add(listBean);
        }

    }

    //分割线
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace ;

        /**
         * @param space 传入的值，其单位视为dp
         */
        public SpaceItemDecoration(int space) {
            this.mSpace = DensityUtil.dip2px(context,space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int itemCount = priMsgListAdapter.getItemCount();
            int pos = parent.getChildAdapterPosition(view);

            outRect.left = 0;
            outRect.top = 0;
            outRect.right = 0;


            if (pos != (itemCount -1)) {
                outRect.bottom = mSpace;
            } else {
                outRect.bottom = 0;
            }
        }
    }

}
