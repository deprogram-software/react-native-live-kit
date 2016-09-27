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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.Bean.BaseBean;
import com.yuanyinguoji.livekit.Bean.Constants;
import com.yuanyinguoji.livekit.Bean.KickData;
import com.yuanyinguoji.livekit.Bean.UserBean;
import com.yuanyinguoji.livekit.Bean.WatchUserListBean;
import com.yuanyinguoji.livekit.Lisener.IListener;
import com.yuanyinguoji.livekit.Lisener.ListenerManager;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.adapter.UserAdapter;
import com.yuanyinguoji.livekit.net.request.Request;
import com.yuanyinguoji.livekit.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyabing on 16/8/26.
 */

public class UserPopwindow extends PopupWindow implements View.OnClickListener,IListener {

    private View mUserPopView;
    private RecyclerView mUserRecycleview;
    private Button btKickUser;
    private UserAdapter mUserAdapter;
    private OnItemClickListener mListener;
    private ImageView ivClose;
    private Context context;
    List<WatchUserListBean.ListBean> userListBeen ;
//    踢人
    private String accountId = "4";
    private String userAccountId;
    private String lookLiveId;
    private String liveid ;
    private String status = "2";//0,1,2  0 进入 1自动退出 2踢出
    /*
    * 送礼物信息
    * */

    private String senderId = "27";//送礼物人
    private String receiveId = "34";//收礼物人
    private String giftId;//礼物ID
    private String totalfee;//总金额
    private String price;//单价
    private String quantity;//数量
    private int countGift=0,totalPrice;

    public UserPopwindow(Context context) {
        super(context);
        this.context = context;
        init(context);
        setPopupWindow();
    }
    public UserPopwindow(Context context,List<WatchUserListBean.ListBean> userListBeen,String accountId,String liveid) {
        super(context);
        this.context = context;
        this.accountId = accountId;
        this.liveid = liveid;
        ListenerManager.getInstance().registerListtener(this);
        this.userListBeen = userListBeen;
        init(context);
        setPopupWindow();
    }


    //初始化
//    private List<String> giftNameList = new ArrayList<>();
//    private List<String> giftPriceList = new ArrayList<>();
    private void init(Context context){

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mUserPopView = layoutInflater.inflate(R.layout.user_list,null);
        btKickUser = (Button) mUserPopView.findViewById(R.id.bt_kick_user);
        ivClose = (ImageView) mUserPopView.findViewById(R.id.iv_close);
        mUserRecycleview = (RecyclerView) mUserPopView.findViewById(R.id.user_recycleview);
        mUserRecycleview.setLayoutManager(new GridLayoutManager(context,5));
        mUserRecycleview.setItemAnimator(new DefaultItemAnimator());
        mUserAdapter = new UserAdapter(context,userListBeen);
        mUserRecycleview.setAdapter(mUserAdapter);
        btKickUser.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        mUserAdapter.setOnItemClickListener(new UserAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, WatchUserListBean.ListBean data,int position) {
                LogUtil.log("data===tttt===="+data.getPhoto());
//                CheckBox checkBox = (CheckBox) view.findViewById(R.id.rb_select);
                RadioButton checkBox = (RadioButton) view.findViewById(R.id.rb_select);
                if (!checkBox.isChecked()){
                    lookLiveId = data.getLookliveid();
                    userAccountId = data.getAccountid();
                    KickData kickData = new KickData();
                    kickData.setAccountid(userAccountId);
                    kickData.setLookliveid(lookLiveId);
                    status = "2";
                    kickDataList.add(kickData);
                    checkBox.setChecked(true);
                    LogUtil.log("选中===ttt===position"+position);
                }else {
                    for (int i = 0; i <kickDataList.size() ; i++) {
                        if(data.getLookliveid().equals(kickDataList.get(i))||data.getAccountid().equals(kickDataList.get(i))){
                            kickDataList.remove(i);
                        }
                    }
                    checkBox.setChecked(false);
                    LogUtil.log("取消===ttt===position"+position);
                }
            }
        });
    }
    private List<KickData> kickDataList = new ArrayList<>();
    //设置窗口相关属性
    private void setPopupWindow(){
        this.setContentView(mUserPopView);// 设置View
        this.setWidth(LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明
        mUserPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = mUserPopView.findViewById(R.id.id_pop_layout).getTop();
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

    @Override
    public void notifyUpdatePage(AllMsgBean allMsgBean) {


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

        if (v.getId()==R.id.iv_close){
            if (mUserPopView.isShown()){
                dismiss();
            }
        }

        if (v.getId() == R.id.bt_kick_user) {
//            if (status.equals("2")){
//                kickUser();
//            }else {
//                Toast.makeText(context,"请选择要踢的人!!!", Toast.LENGTH_SHORT).show();
//            }

            kickUser();

        }

//        switch (v.getId()){
//            case R.id.iv_close:
//                if (mUserPopView.isShown()){
//                    dismiss();
//                }
//                break;
//            case R.id.bt_kick_user:
//
//                break;
//            default:
//                break;
//
//        }

    }
    /*
    * 踢人(liveid直播唯一标识, accountid用户登录id,status(2踢人),clientSign:验签,
    * list踢人列表(不参与签名 accountid观看用户编号lookliveid用户观看直播唯一标识))
    *
    * */

    private void kickUser(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String kickData = Request.kickUuserFormLive(accountId,status,liveid,kickDataList);
                BaseBean baseBean = JSONObject.parseObject(kickData, BaseBean.class);
                Message msg = refreshHandler.obtainMessage();
                msg.what = Constants.MSG_KICK_USER;
                msg.obj = baseBean;
                refreshHandler.sendMessage(msg);
                LogUtil.log("kickData==="+kickData);
            }
        }).start();
    }

    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constants.MSG_KICK_USER) {
                BaseBean basebean = (BaseBean)msg.obj;
                Toast.makeText(context,basebean.getFlag(),Toast.LENGTH_SHORT).show();
            }
        }
    };



}
