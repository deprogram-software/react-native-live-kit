package com.yuanyinguoji.livekit;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;
import com.yuanyinguoji.livekit.Bean.AddWatchRecordBean;
import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.Bean.Constants;
import com.yuanyinguoji.livekit.Bean.GiftBean;
import com.yuanyinguoji.livekit.Lisener.IListener;
import com.yuanyinguoji.livekit.Lisener.ListenerManager;
import com.yuanyinguoji.livekit.adapter.ChatAdapter;
import com.yuanyinguoji.livekit.adapter.ChatListAdapter;
import com.yuanyinguoji.livekit.adapter.MyAdapter;
import com.yuanyinguoji.livekit.net.request.Request;
import com.yuanyinguoji.livekit.util.DensityUtil;
import com.yuanyinguoji.livekit.util.LogUtil;
import com.yuanyinguoji.livekit.util.StringUtil;
import com.yuanyinguoji.livekit.view.GiftPopwindow;
import com.yuanyinguoji.livekit.view.ListViewBullet;
import com.yuanyinguoji.livekit.view.MyListView;
import com.yuanyinguoji.livekit.view.SendChatMsgPopwindow;
import com.yuanyinguoji.livekit.view.SendMsgPopwindow;
import com.yuanyinguoji.livekit.websocket.ChatWebSocket;

import org.java_websocket.drafts.Draft_17;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ltjin on 16/9/7.
 */
public class PlayerViewManager extends SimpleViewManager<PLVideoView> implements LifecycleEventListener,IListener {
    private static final String TAG = "<<<<<< YYPlayer >>>>>>";
    private PLVideoView mVideoView;
    private TextView tvChat;
    private TextView tvPrivateChat;
    private LinearLayout rl;
    private List<AllMsgBean> mChatlist =new ArrayList<>();
    private LinearLayout llChatList;
    private RecyclerView rvChatList;
    private ChatAdapter chatAdapter;
    private ImageView ivSendPkt;
    private LinearLayout llGiftNumLayout;
    private ImageView ivUserHeader;
    private LinearLayout llMiddle;
    private TextView tvUserName,tvGiftName,tvGiftNameNo,tvSymbol;

    //添加
    private ListView lvChatList;
    private ChatListAdapter chatListAdapter;
    private GiftPopwindow giftPopwindow;
    private SendMsgPopwindow sendMsgPopwindow;
    private SendChatMsgPopwindow sendChatMsgPopwindow;
    private ThemedReactContext mreactContext;
    private JSONObject mLiveInfo;

    private MyAdapter myAdapter;
    @Override
    public String getName() {
        return "RCTPlayerView";
    }

    @Override
    protected PLVideoView createViewInstance( ThemedReactContext reactContext) {
        getGiftList();
        ListenerManager.getInstance().registerListtener(this);

        this.mreactContext = reactContext;
        mVideoView = new PLVideoView(reactContext);
        mVideoView.setOnPreparedListener(mOnPreparedListener);
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mVideoView.setOnErrorListener(mOnErrorListener);


        rvChatList = new RecyclerView(reactContext);
        this.lvChatList = new ListViewBullet(reactContext);
        rvChatList.addItemDecoration(new SpaceItemDecoration(5));

        FrameLayout.LayoutParams lvPrams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lvPrams.bottomMargin = 200;
        lvPrams.height = 600;
        lvPrams.gravity = Gravity.BOTTOM;


        LinearLayout.LayoutParams rvPrams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rvPrams.setMargins(20,20,0,0);
        rvChatList.setLayoutParams(rvPrams);
        rvChatList.setMinimumHeight(500);
        //        lvChatList.setMinimumHeight(200);
//        lvChatList.setLayoutParams(lvPrams);
//        rvChatList.setBackgroundColor(reactContext.getResources().getColor(R.color.c_ff));
        rvChatList.setLayoutManager(new LinearLayoutManager(reactContext,LinearLayoutManager.VERTICAL,false));


        llMiddle = new LinearLayout(reactContext);
        llMiddle.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams middleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        middleParams.setMargins(5,0,0,0);
        middleParams.gravity=Gravity.CENTER;
        llMiddle.setLayoutParams(middleParams);


        LinearLayout.LayoutParams comParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        comParams.gravity=Gravity.CENTER;
        tvUserName = new TextView(reactContext);
        tvUserName.setTextColor(reactContext.getResources().getColor(R.color.c_5c_f));
        tvUserName.setTextSize(reactContext.getResources().getDimension(R.dimen.f_5));
        tvUserName.setEms(4);
        tvUserName.setGravity(Gravity.CENTER);
        tvGiftName = new TextView(reactContext);
        tvGiftName.setTextColor(reactContext.getResources().getColor(R.color.c_ff));
        tvGiftName.setTextSize(reactContext.getResources().getDimension(R.dimen.f_6));
        tvGiftName.setEms(3);
        tvGiftName.setGravity(Gravity.CENTER);

        tvSymbol = new TextView(reactContext);
        tvUserName.setLayoutParams(comParams);
        tvGiftName.setLayoutParams(comParams);
        tvSymbol.setLayoutParams(comParams);

        llMiddle.addView(tvUserName);
        llMiddle.addView(tvGiftName);

        String no = "00";
        tvSymbol.setText("X");
        tvSymbol.setTextColor(reactContext.getResources().getColor(R.color.c_ff));
        tvSymbol.setTextSize(reactContext.getResources().getDimension(R.dimen.f_6));
        LinearLayout.LayoutParams tvSymbolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        tvSymbolParam.setMargins(5,0,0,0);
        tvSymbol.setLayoutParams(tvSymbolParam);
        tvGiftNameNo = new TextView(reactContext);
        tvGiftNameNo.setText(no);
        tvGiftNameNo.setTextColor(reactContext.getResources().getColor(R.color.c_ff));
        tvGiftNameNo.setTextSize(reactContext.getResources().getDimension(R.dimen.f_8));
        tvGiftNameNo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvGiftNameNo.setPadding(2,0,0,0);
        tvGiftNameNo.setLayoutParams(comParams);

        llGiftNumLayout = new LinearLayout(reactContext);
        LinearLayout.LayoutParams llGift = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        llGiftNumLayout.setGravity(Gravity.CENTER_VERTICAL);
        llGiftNumLayout.setOrientation(LinearLayout.HORIZONTAL);
        llGiftNumLayout.setPadding(5,5,10,5);
        llGiftNumLayout.setBackgroundResource(R.drawable.bg_gift_area);
        llGiftNumLayout.setLayoutParams(llGift);
        ivUserHeader = new CircleImageView(reactContext);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivParams.width = DensityUtil.dip2px(reactContext,40);
        ivParams.height = DensityUtil.dip2px(reactContext,40);
        ivUserHeader.setLayoutParams(ivParams);
        String url = "http://www.qq745.com/uploads/allimg/141012/1-141012103344.jpg";
        Glide.with(reactContext).load(url).centerCrop().into(ivUserHeader);
        llGiftNumLayout.addView(ivUserHeader);
        llGiftNumLayout.addView(llMiddle);
        llGiftNumLayout.addView(tvSymbol);
        llGiftNumLayout.addView(tvGiftNameNo);
        llGiftNumLayout.setVisibility(View.INVISIBLE);

        llChatList = new LinearLayout(reactContext);
        llChatList.setOrientation(LinearLayout.VERTICAL);
        llChatList.addView(llGiftNumLayout);
        llChatList.addView(rvChatList);


        rl = new LinearLayout(reactContext);
        rl.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        rl.setMinimumHeight(55);
        layoutParams.height = 55;
        rl.setPadding(0,10,0,10);
        rl.setBackgroundColor(reactContext.getResources().getColor(R.color.c_a8_0));

        tvChat = new TextView(reactContext);
        tvChat.setText("聊天");
        tvChat.setGravity(Gravity.CENTER);
        tvChat.setTextColor(reactContext.getResources().getColor(R.color.c_f));
        tvChat.setTextSize(reactContext.getResources().getDimension(R.dimen.f_6));
        Drawable drawable = reactContext.getResources().getDrawable(R.drawable.ic_chat);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        tvChat.setCompoundDrawables(null,drawable,null,null);
        tvChat.setId(View.NO_ID);


        tvPrivateChat = new TextView(reactContext);
        tvPrivateChat.setText("私信");
        tvPrivateChat.setGravity(Gravity.CENTER);
        tvPrivateChat.setTextColor(reactContext.getResources().getColor(R.color.c_f));
        tvPrivateChat.setTextSize(reactContext.getResources().getDimension(R.dimen.f_6));
        Drawable drawableR = reactContext.getResources().getDrawable(R.drawable.ic_private_chat);
        drawableR.setBounds(0, 0, drawableR.getMinimumWidth(), drawableR.getMinimumHeight()); //设置边界
        tvPrivateChat.setCompoundDrawables(null,drawableR,null,null);
        tvPrivateChat.setId(View.NO_ID);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        tvChat.setLayoutParams(lp);
        // getTextView02设置权重是2
        lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        tvPrivateChat.setLayoutParams(lp);


        ivSendPkt = new ImageView(reactContext);
        ivSendPkt.setBackground(reactContext.getResources().getDrawable(R.drawable.ic_gift));
        FrameLayout.LayoutParams ivPrams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivPrams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        ivPrams.bottomMargin = DensityUtil.dip2px(reactContext,35);
//        ivPrams.bottomMargin = 55;
        ivSendPkt.setLayoutParams(ivPrams);
        ivSendPkt.setId(View.NO_ID);

//        mVideoView.addView(lvChatList);
        mVideoView.addView(llChatList,lvPrams);
//        mVideoView.addView(rvChatList);
        rl.addView(tvChat);
        rl.addView(tvPrivateChat);
        mVideoView.addView(rl,layoutParams);
        mVideoView.addView(ivSendPkt);

        FrameLayout.LayoutParams rlPrams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlPrams.gravity = Gravity.BOTTOM;
        rl.setLayoutParams(rlPrams);

        chatListAdapter = new ChatListAdapter(mreactContext,mChatlist);
        lvChatList.setAdapter(chatListAdapter);

        chatAdapter = new ChatAdapter(mreactContext,mChatlist);
        rvChatList.setAdapter(chatAdapter);
//        chatAdapter = new ChatAdapter(mreactContext,mChatlist);
//      rvChatList.setAdapter(chatAdapter);   FrameLayout.LayoutParams lvPrams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        lvPrams.bottomMargin = 200;
//        lvPrams.height = 200;
//        lvChatList.setMinimumHeight(200);
//        lvChatList.setLayoutParams(lvPrams);
//        lvChatList.setBackgroundColor(reactContext.getResources().getColor(R.color.c_0));

        AVOptions options = new AVOptions();

// 解码方式，codec＝1，硬解; codec=0, 软解
// 默认值是：0
        options.setInteger(AVOptions.KEY_MEDIACODEC, 1);

// 准备超时时间，包括创建资源、建立连接、请求码流等，单位是 ms
// 默认值是：无
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);

// 读取视频流超时时间，单位是 ms
// 默认值是：10 * 1000
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);

// 当前播放的是否为在线直播，如果是，则底层会有一些播放优化
// 默认值是：0
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);

// 是否开启"延时优化"，只在在线直播流中有效
// 默认值是：0
        options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);

// 默认的缓存大小，单位是 ms
// 默认值是：2000
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 2000);

// 最大的缓存大小，单位是 ms
// 默认值是：4000
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 4000);

// 是否自动启动播放，如果设置为 1，则在调用 `prepareAsync` 或者 `setVideoPath` 之后自动启动播放，无需调用 `start()`
// 默认值是：1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);

// 请在开始播放之前配置
        mVideoView.setAVOptions(options);




        tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.log("聊啊天===ttt==="+12121);
                showChatPop(mreactContext,rl);
            }
        });

        tvPrivateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.log("私聊===ttt==="+12121);
                showPrivateChatPop(mreactContext,rl);

            }
        });

        ivSendPkt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGiftPopwindow(mreactContext,rl);
                LogUtil.log("送礼物===ttt==="+12121);
            }
        });


        addUserRecord();
//启动websocket
        startWebSocket();

        return mVideoView;

    }

    /*
  * 会员直播观看记录(accountid用户登录id,lookliveid用户观看直播唯一标识（status为0时此字段为空），
  * status0进入直播1自动退出直播,liveid直播标识,clientSign:验签
  * */
    private String lookliveid;
    private String status="0";
    private void addUserRecord(){
        status = "0";
        new Thread(new Runnable() {
            @Override
            public void run() {
                String addState = Request.addUserRecord(user,liveid,lookliveid,status);
                parseAddResult(addState);
                LogUtil.log(TAG+"添加记录"+addState.toString());
            }
        }).start();

    }
    private void parseAddResult(String addData){
        AddWatchRecordBean recordBean = JSON.parseObject(addData ,AddWatchRecordBean.class);
        Message msg = refreshMsgHandler.obtainMessage();
        msg.what = Constants.MSG_ADD_USER;
        msg.obj = recordBean;
        refreshMsgHandler.sendMessage(msg);
    }



    @ReactProp(name = "uri")
    public void setUri(PLVideoView mVideoView, String uri) {
        System.out.println("播放地址：》》》》》》" + uri);
        mVideoView.setVideoPath(uri);
    }

    @ReactProp(name = "liveInfo")
    public void setLiveInfo(PLVideoView mVideoView, @Nullable ReadableMap liveInfo) {
        mLiveInfo = ReadableMapUtil.readableMap2JSON(liveInfo);
    }

    private PLMediaPlayer.OnPreparedListener mOnPreparedListener = new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "onPrepared ! ");
        }
    };

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            Log.d(TAG, "onInfo: " + what + ", " + extra);
            return true;
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            return true;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "Play Completed !");
        }
    };

    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int precent) {
            Log.d(TAG, "onBufferingUpdate: " + precent);
        }
    };

    private PLMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new PLMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "onSeekComplete !");
        }

        ;
    };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height) {
            Log.d(TAG, "onVideoSizeChanged: " + width + "," + height);
        }
    };

    @Override
    public void onHostResume() {
        Log.d(TAG, "onHostResume !");
        mVideoView.start();
    }

    @Override
    public void onHostPause() {
        Log.d(TAG, "onHostPause !");
        mVideoView.pause();
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, "onHostDestroy !");
        mVideoView.stopPlayback();
    }

    int start =1;
    String pageSize = "10";
    private  String giftData;
    private void getGiftList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                giftData = Request.getGiftList(String.valueOf(start),pageSize);
                if (giftData!=null){
                    parseGiftData();
                }
                LogUtil.log("giftData====tttttt===="+giftData);
            }
        }).start();


    }

    GiftBean giftBean = new GiftBean();

    List<GiftBean.GiftList> list = new ArrayList<>();
    List<GiftBean.GiftList> giftLists = new ArrayList<>();
    private void parseGiftData(){
        GiftBean giftBean = JSON.parseObject(giftData, GiftBean.class);
        giftLists = giftBean.getList();
    }



    private void showPrivateChatPop(ThemedReactContext reactContext,LinearLayout rl){
        if (sendMsgPopwindow == null){
            sendMsgPopwindow = new SendMsgPopwindow(reactContext,user,receivedId,liveid);
            sendMsgPopwindow.setHeight((int)(DensityUtil.getHeight(reactContext)*(0.4f)));
            sendMsgPopwindow.showAtLocation(rl,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
        }else {
            sendMsgPopwindow.showAtLocation(rl,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
        }
    }


    private void showChatPop(ThemedReactContext reactContext,LinearLayout rl){
        if (sendChatMsgPopwindow == null){
            sendChatMsgPopwindow = new SendChatMsgPopwindow(reactContext,user,liveid);
            sendChatMsgPopwindow.setHeight((int)(DensityUtil.getHeight(reactContext)*(0.15f)));
            sendChatMsgPopwindow.showAtLocation(rl,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
        }else {
            sendChatMsgPopwindow.showAtLocation(rl,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
        }
    }

    private  void showGiftPopwindow(ThemedReactContext reactContext,LinearLayout rl){
        if (giftPopwindow == null) {
            giftPopwindow = new GiftPopwindow(reactContext,giftLists,user,liveid);
            LogUtil.log("giftLists===tt==="+giftLists.toString());
            Toast.makeText(reactContext,"发送礼物",Toast.LENGTH_SHORT).show();
            giftPopwindow.showAtLocation(rl,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
        }else {
            giftPopwindow.showAtLocation(rl,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
        }
    }
    //websocket 参数
    private String liveid = "14746337750809";
    private String user = "27";
    private String receivedId = "11";
    private String accounttype = "2";
    private String username = "七天之约";
    private String photoPath = "687474703A2F2F6F6369683277666F382E626B742E636C6F7564646E2E636F6D2F31343732323935343638373236";
    private void startWebSocket(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                URI uri=null;

                try {
//                    String string = "ws://192.168.1.3:8110/WebSocket/message?liveid=11&user=5&accounttype=1"+"&username="+ StringUtil.convertStringToHex("你好000")+"clientSign"+Request.getChatSignStr(params);
                    String resUrl = Request.getChatUrl(liveid,user,accounttype,StringUtil.convertStringToHex(username),photoPath);
                    uri = new URI(resUrl);
                    LogUtil.log("received"+resUrl);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ChatWebSocket webSocketWorker=new ChatWebSocket(uri, new Draft_17());
                try {
                    webSocketWorker.connectBlocking();
//            webSocketWorker.connect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                webSocketWorker.send("你好");
            }
        }).start();


    }

    @Override
    public void notifyAllActivity(String str) {
        if(str.equals("0")){
            showGiftPopwindow(mreactContext,rl);
        }
    }

    @Override
    public void notifyUpdatePage( AllMsgBean allMsgBean) {

//        mChatlist.add(allMsgBean);

//        for (int i = 0; i <5 ; i++) {
//            AllMsgBean all = new AllMsgBean();
//            all.setConttext("nihaokdja"+i);
//            mChatlist.add(allMsgBean);
//        }

//        if (mChatlist.size()>20) {
//            int a = mChatlist.size()-20;
//            for (int i = 0; i < a; i++) {
//                mChatlist.remove(0);
//            }
//        }
        if(allMsgBean.getType().equals("online_chat")){
            Message msg = refreshMsgHandler.obtainMessage();
            msg.what = Constants.MSG_CHAT_REFRESH;
            msg.obj = allMsgBean;
            refreshMsgHandler.sendMessage(msg);
            LogUtil.log("online_chat===ttttt进入更新方法=="+allMsgBean.getConttext());
        }else if (allMsgBean.getType().equals("gift_send")){
            Message msg = refreshMsgHandler.obtainMessage();
            msg.what = Constants.MSG_GIFT_SEND;
            msg.obj = allMsgBean;
            refreshMsgHandler.sendMessage(msg);
        }else if(allMsgBean.getType().equals("look_tiren")){
            LogUtil.log("look_tiren===ttttt进入踢人成功=="+allMsgBean.getLiveid());
            Message msg = refreshMsgHandler.obtainMessage();
            msg.what = Constants.MSG_KICK_USER;
            msg.obj = allMsgBean;
            refreshMsgHandler.sendMessage(msg);
        }

//        chatListAdapter.mChatMsg = mChatlist;
//        lvChatList.invalidate();
//        chatListAdapter.notifyDataSetChanged();
//        lvChatList.setSelection(mChatlist.size()-1);

//        chatListAdapter = new ChatListAdapter(mreactContext,mChatlist);
//        lvChatList.setAdapter(chatListAdapter);
//        lvChatList.setSelection(mChatlist.size()-1);
    }


    private Handler refreshMsgHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == Constants.MSG_CHAT_REFRESH){
                LogUtil.log("online_chat===ttttt进入更新方法adapter==");
                if (mChatlist.size()>0) {
                    chatAdapter.addData((AllMsgBean)msg.obj);
                    chatAdapter.notifyDataSetChanged();
                    rvChatList.smoothScrollToPosition(mChatlist.size()-1);
                }else {
                    mChatlist.add((AllMsgBean)msg.obj);
                }

            }else if(msg.what == Constants.MSG_GIFT_SEND){
                llGiftNumLayout.setVisibility(View.VISIBLE);
                AllMsgBean giftMsg = (AllMsgBean)msg.obj;
                Glide.with(mreactContext).load(giftMsg.getPhoto()).centerCrop().into(ivUserHeader);
                tvGiftNameNo.setText(giftMsg.getQuantity());
                tvGiftName.setText(giftMsg.getGiftname());
                tvUserName.setText(giftMsg.getUsername());
            }
            else if (msg.what == Constants.MSG_ADD_USER){
                AddWatchRecordBean recordBean = (AddWatchRecordBean)msg.obj;
                if (recordBean != null) {
                    if (recordBean.getFlag().equals("1000")){
                        lookliveid = recordBean.getLookliveid();
                    }else {
                        Toast.makeText(mreactContext,recordBean.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(mreactContext,"请求数据失败",Toast.LENGTH_SHORT).show();
                }
            }else if(msg.what == Constants.MSG_KICK_USER){
                AllMsgBean kickUser = (AllMsgBean)msg.obj;
                LogUtil.log("kickUser==TTT=="+kickUser.getType());
                Toast.makeText(mreactContext,"您已经被踢出主播",Toast.LENGTH_SHORT).show();
            }

        }
    };


    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace ;

        /**
         * @param space 传入的值，其单位视为dp
         */
        public SpaceItemDecoration(int space) {
            this.mSpace = DensityUtil.dip2px(mreactContext,space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int itemCount = chatAdapter.getItemCount();
            int pos = parent.getChildAdapterPosition(view);
            Log.d(TAG, "itemCount>>" +itemCount + ";Position>>" + pos);

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
