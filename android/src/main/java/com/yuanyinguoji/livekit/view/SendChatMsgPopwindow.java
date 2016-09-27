package com.yuanyinguoji.livekit.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.Bean.Constants;
import com.yuanyinguoji.livekit.Bean.MsgBean;
import com.yuanyinguoji.livekit.Lisener.IListener;
import com.yuanyinguoji.livekit.Lisener.ListenerManager;
import com.yuanyinguoji.livekit.R;
import com.yuanyinguoji.livekit.adapter.MsgAdapter;
import com.yuanyinguoji.livekit.net.request.Request;
import com.yuanyinguoji.livekit.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyabing on 16/8/26.
 */

public class SendChatMsgPopwindow extends PopupWindow implements View.OnClickListener,IListener {


    /*
    *在线聊天(accountid发送人,conttext内容，liveid直播标识,clientSign:验签)
    * */
    private String accountid,conttext,liveid;

    private View mSendMsgpView;
    private OnItemClickListener mListener;
    private EditText etMsg;
    private TextView tvSendMsg;
    private Context context;
    List<MsgBean> list = new ArrayList<>();
    public SendChatMsgPopwindow(Context context,String user,String liveid) {
        super(context);
        this.context = context;

        ListenerManager.getInstance().registerListtener(this);
        //websokcet user value
        this.accountid =user;
        this.liveid=liveid;
        init(context);
        setPopupWindow();

    }



    //初始化
    private void init(Context context){

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mSendMsgpView = layoutInflater.inflate(R.layout. send_chatmsg_popwindow,null);

        etMsg = (EditText) mSendMsgpView.findViewById(R.id.et_msg);
        tvSendMsg = (TextView) mSendMsgpView.findViewById(R.id.tv_send_msg);
        msgAdapter = new MsgAdapter(context,allMsgBeanList);

        tvSendMsg.setOnClickListener(this);
//        mSendMsgRecycleview.getLayoutParams().height = (int)(DensityUtil.getHeight(context)*0.4f);

    }
    //设置窗口相关属性
    private void setPopupWindow(){
        this.setContentView(mSendMsgpView);// 设置View
        this.setWidth(LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明
        mSendMsgpView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = mSendMsgpView.findViewById(R.id.id_pop_layout).getTop();
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
//        allMsgBeanList.add(allMsgBean);
        Message msg  = refreshMsgHandler.obtainMessage();
        msg.what = Constants.MSG_REFRESH;
        msg.obj = allMsgBean;
        refreshMsgHandler.sendMessage(msg);
    }

    private List<AllMsgBean> allMsgBeanList = new ArrayList<>();
    /**
     * 定义一个接口，公布出去 在Activity中操作按钮的单击事件
     */
    public interface OnItemClickListener {
        void setOnItemClick(View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    private MsgAdapter msgAdapter;
    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.setOnItemClick(v);
        }

        else if (v.getId() == R.id.tv_send_msg) {
            getSendMsgBean();
            sendMsg();
            etMsg.setText("");
        }

//        switch (v.getId()){
//            case R.id.tv_send_msg:
//                getSendMsgBean();
//                msgAdapter = new MsgAdapter(context,msgBeanList);
//                mSendMsgRecycleview.setAdapter(msgAdapter);
//                etMsg.setText("");
//                break;
//            case R.id.iv_close:
//                if (mSendMsgpView.isShown()) {
//                    msgBeanList.clear();
//                    dismiss();
//                }
//                break;
//
//            case R.id.iv_gift:
//                break;
//            default:
//                break;
//
//        }

    }

    private List<MsgBean.ListBean> msgBeanList = new ArrayList<>();
    private void getSendMsgBean(){
        conttext = etMsg.getText().toString().trim();
        MsgBean.ListBean listBean = new MsgBean.ListBean();
        listBean.setConttext(etMsg.getText().toString().trim());
        listBean.setSenderid("1");
        msgBeanList.add(listBean);


        AllMsgBean allMsgBean = new AllMsgBean();
        allMsgBean.setSendAndReId("1");
        allMsgBean.setConttext(conttext);
        allMsgBeanList.add(allMsgBean);
    }
    private void getReceivedMsgBean() {

    }
    private String msgList;
    private void sendMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                msgList = Request.sendChatMsg(accountid,conttext,liveid);
                LogUtil.log("received==ttt=="+msgList.toString());
            }
        }).start();
    }
    private Handler refreshMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                switch (msg.what){
                    case Constants.MSG_REFRESH:
//                        AllMsgBean allMsgBean =(AllMsgBean)msg.obj;
                        msgAdapter.addData((AllMsgBean)msg.obj);
                    break;
                    default:
                        break;

                }

        }
    };
}
