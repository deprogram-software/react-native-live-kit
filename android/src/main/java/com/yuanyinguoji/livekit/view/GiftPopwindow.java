package com.yuanyinguoji.livekit.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yuanyinguoji.livekit.Bean.BaseBean;
import com.yuanyinguoji.livekit.Bean.Constants;
import com.yuanyinguoji.livekit.Bean.GiftBean;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.adapter.GiftAdapter;
import com.yuanyinguoji.livekit.net.request.Request;
import com.yuanyinguoji.livekit.util.LogUtil;


import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by chenyabing on 16/8/26.
 */

public class GiftPopwindow extends PopupWindow implements View.OnClickListener {

    private View mGiftPopView;
    private RecyclerView mGiftRecycleview;
    private GiftAdapter mGiftAdapter;
    private OnItemClickListener mListener;
    private TextView tvBalance;
    private TextView tvTopUp;
    private TextView tvSend;
    private EditText etGiftNo;
    private Context context;
    List<GiftBean.GiftList> giftLists;

    /*
    * 送礼物信息
    * */

    private String senderId = "27";//送礼物人
    private String receiveId = "34";//收礼物人
    private String giftId;//礼物ID
    private String totalfee;//总金额
    private String price;//单价
    private String quantity;//数量
    private String liveId="3";//直播标识
    private int countGift=0,totalPrice;
    public GiftPopwindow(Context context,List<GiftBean.GiftList> giftLists,String user,String liveId) {
        super(context);
        this.context = context;
        this.giftLists = giftLists;
        this.senderId = user;
        this.liveId = liveId;
        init(context);
        setPopupWindow();
    }

    private void init(Context context){

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mGiftPopView = layoutInflater.inflate(R.layout.gift_popwindow,null);
        mGiftRecycleview = (RecyclerView) mGiftPopView.findViewById(R.id.gift_recycleview);
        mGiftRecycleview.setLayoutManager(new GridLayoutManager(context,4));
        mGiftRecycleview.addItemDecoration(new DividerGridItemDecoration(context));
        mGiftRecycleview.setItemAnimator(new DefaultItemAnimator());
        mGiftAdapter = new GiftAdapter(context,giftLists);
        mGiftRecycleview.setAdapter(mGiftAdapter);



        tvBalance = (TextView) mGiftPopView.findViewById(R.id.tv_balance);
        tvTopUp = (TextView) mGiftPopView.findViewById(R.id.tv_top_up);
        tvSend = (TextView) mGiftPopView.findViewById(R.id.tv_send);
        etGiftNo = (EditText) mGiftPopView.findViewById(R.id.et_gift_no);

        tvTopUp.setOnClickListener(this);
        tvSend.setOnClickListener(this);

        mGiftAdapter.setOnItemClickListener(new GiftAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, GiftBean.GiftList data) {
                LogUtil.log("data===tttt===="+data.getGiftid());
                giftId = data.getGiftid();
                price = data.getPrice();

                etGiftNo.setText("1");
            }
        });
    }
    //设置窗口相关属性
    private void setPopupWindow(){
        this.setContentView(mGiftPopView);// 设置View
        this.setWidth(LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明
        mGiftPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = mGiftPopView.findViewById(R.id.id_pop_layout).getTop();
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


    /**
     * 定义一个接口，公布出去 在Activity中操作按钮的单击事件
     */
    public interface OnItemClickListener {
        void setOnItemClick(View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.setOnItemClick(v);
        }

        else if (v.getId()==R.id.tv_send){
            getSendData();
            if (price!=null&&giftId!=null&&totalfee!=null) {
                sendGift();
            }else {
                showToast("您未选择礼物");
            }
        }else if (v.getId()==R.id.tv_top_up){

        }
    }


    private void getSendData(){
        quantity = etGiftNo.getText().toString().trim();
        LogUtil.log("quantity====ttt==="+quantity);
        if (Integer.parseInt(quantity) > 0) {
            totalfee =String.valueOf(new DecimalFormat("0.00").format(Double.parseDouble(price)*Double.parseDouble(quantity)));
        }else {
            showToast("所选礼物个数必须大于0");
        }
        LogUtil.log("totalfee====tttt===="+totalfee);
    }

    private void showToast(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    BaseBean baseBean;
    private void sendGift(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = Request.sendGift(senderId,receiveId,giftId,totalfee,price,liveId,quantity);
                if (data != null){
                    baseBean =JSON.parseObject(data, BaseBean.class);
                    Message msg = refreshHandler.obtainMessage();
                    msg.what=Constants.MSG_REFRESH;
                    msg.obj = baseBean;
                    refreshHandler.sendMessage(msg);
                }
            }
        }).start();
    }



    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constants.MSG_REFRESH:
                    baseBean = (BaseBean)msg.obj;
                    LogUtil.log("baseBean.getFlag()===="+baseBean.getFlag());
                    if (baseBean.getFlag().equals(1000)) {
                        LogUtil.log("礼物发送成功!!!");
                        Toast.makeText(context,"礼物发送成功!!!",Toast.LENGTH_SHORT).show();
                    }else{
                        LogUtil.log("礼物发送成功!!!!!!!");
                        Toast.makeText(context,baseBean.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;

            }

        }
    };
}
