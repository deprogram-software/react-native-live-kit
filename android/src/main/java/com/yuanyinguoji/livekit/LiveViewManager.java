package com.yuanyinguoji.livekit;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.pili.pldroid.player.widget.PLVideoView;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;
import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.Bean.Constants;
import com.yuanyinguoji.livekit.Bean.LiveBeanStream;
import com.yuanyinguoji.livekit.Bean.UserBean;
import com.yuanyinguoji.livekit.Bean.WatchUserListBean;
import com.yuanyinguoji.livekit.Lisener.IListener;
import com.yuanyinguoji.livekit.Lisener.ListenerManager;
import com.yuanyinguoji.livekit.adapter.ChatAdapter;
import com.yuanyinguoji.livekit.adapter.ChatListAdapter;
import com.yuanyinguoji.livekit.net.request.Request;
import com.yuanyinguoji.livekit.ui.FocusIndicatorRotateLayout;
import com.yuanyinguoji.livekit.ui.RotateLayout;
import com.yuanyinguoji.livekit.util.DensityUtil;
import com.yuanyinguoji.livekit.util.LogUtil;
import com.yuanyinguoji.livekit.util.StringUtil;
import com.yuanyinguoji.livekit.view.GiftPopwindow;
import com.yuanyinguoji.livekit.view.ListViewBullet;
import com.yuanyinguoji.livekit.view.PriListPopwindow;
import com.yuanyinguoji.livekit.view.SendChatMsgPopwindow;
import com.yuanyinguoji.livekit.view.SendMsgPopwindow;
import com.yuanyinguoji.livekit.view.UserPopwindow;
import com.yuanyinguoji.livekit.websocket.ChatWebSocket;

import org.java_websocket.drafts.Draft_17;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ltjin on 16/9/7.
 */
public class LiveViewManager extends SimpleViewManager<AspectFrameLayout> implements
        LifecycleEventListener,
        CameraPreviewFrameView.Listener,
        StreamingSessionListener,
        StreamingStateChangedListener,IListener {

    private static final String TAG = "<<<<<< YYLive >>>>>>";

    private TextView tvChat;
    private TextView tvPrivateChat;
    private LinearLayout rl;
    private List<AllMsgBean> mChatlist =new ArrayList<>();
    private LinearLayout llChatList;
    private RecyclerView rvChatList;
    private ChatAdapter chatAdapter;
    private LinearLayout llGiftNumLayout;
    private ImageView ivUserHeader;
    private LinearLayout llMiddle;
    private TextView tvUserName,tvGiftName,tvGiftNameNo,tvSymbol;

    //添加
    private SendMsgPopwindow sendMsgPopwindow;
    private SendChatMsgPopwindow sendChatMsgPopwindow;

    @Override
    public void notifyAllActivity(String str) {

    }

    @Override
    public void notifyUpdatePage(AllMsgBean allMsgBean) {
        if (allMsgBean!=null) {
            if (allMsgBean.getAhchorPri()) {
                showPrivateChatPop(allMsgBean);
            }else if(allMsgBean.getType().equals("online_chat")){
                Message msg = mHandler.obtainMessage();
                msg.what = Constants.MSG_CHAT_REFRESH;
                msg.obj = allMsgBean;
                mHandler.sendMessage(msg);
                LogUtil.log("online_chat===ttttt进入更新方法=="+allMsgBean.getConttext());
            }else if (allMsgBean.getType().equals("gift_send")){
                Message msg = mHandler.obtainMessage();
                msg.what = Constants.MSG_GIFT_SEND;
                msg.obj = allMsgBean;
                mHandler.sendMessage(msg);
            }
        }
    }


    public enum Events {
        READY("onReady"),
        CONNECTING("onConnecting"),
        STREAMING("onStreaming"),
        SHUTDOWN("onShutdown"),
        IOERROR("onIOError"),
        DISCONNECTED("onDisconnected");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    protected static final int MSG_START_STREAMING = 0;
    protected static final int MSG_STOP_STREAMING = 1;
    private static final int MSG_SET_ZOOM = 2;
    private static final int MSG_MUTE = 3;
    private static final int ZOOM_MINIMUM_WAIT_MILLIS = 33; //ms

    protected MediaStreamingManager mMediaStreamingManager;
    protected boolean mIsReady = false;

    private int mCurrentZoom = 0;
    private int mMaxZoom = 0;
    private StreamingProfile mProfile;
    private CameraStreamingSetting setting;
    private MicrophoneStreamingSetting microphoneSetting;
    private ThemedReactContext context;
    private RotateLayout mRotateLayout;
    private CameraPreviewFrameView previewFrameView;
    private AspectFrameLayout piliStreamPreview;
    private boolean focus = false;
    private boolean started = true;//default start attach on parent view
    private RCTEventEmitter mEventEmitter;
    private JSONObject mLiveInfo;


    private void initializeStreamingSessionIfNeeded(AspectFrameLayout afl, CameraPreviewFrameView previewFrameView) {
        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>");
        if (mMediaStreamingManager == null) {
            mMediaStreamingManager = new MediaStreamingManager(
                    context,
                    afl,
                    previewFrameView,
                    AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
            mProfile = new StreamingProfile();
            StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 96 * 1024); //audio sample rate, audio bitrate
            StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(30, 1000 * 1024, 48);//fps bps maxFrameInterval
            StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
            mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH3)
                    .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
//                .setPreferredVideoEncodingSize(960, 544)
                    .setEncodingSizeLevel(Config.ENCODING_LEVEL)
                    .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
//                    .setStream(stream)   //set Stream
                    .setAVProfile(avProfile)
                    .setDnsManager(getMyDnsManager())
                    .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
//                .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT)
                    .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));

            setting = new CameraStreamingSetting();
            setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                    .setContinuousFocusModeEnabled(true)
                    .setRecordingHint(false)
                    .setResetTouchFocusDelayInMs(3000)
                    .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_PICTURE)
                    .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                    .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);

            microphoneSetting = new MicrophoneStreamingSetting();
            microphoneSetting.setBluetoothSCOEnabled(false);

            mMediaStreamingManager.prepare(setting, microphoneSetting, mProfile);
            mMediaStreamingManager.setStreamingStateListener(this);
            mMediaStreamingManager.setStreamingSessionListener(this);
            context.addLifecycleEventListener(this);

        }
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder builder = MapBuilder.builder();
        for (Events event : Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

    @Override
    public AspectFrameLayout createViewInstance(ThemedReactContext context) {
        this.context = context;
        ListenerManager.getInstance().registerListtener(this);

        mEventEmitter = context.getJSModule(RCTEventEmitter.class);

        piliStreamPreview = new AspectFrameLayout(context);

        piliStreamPreview.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);

        previewFrameView = new CameraPreviewFrameView(context);
        previewFrameView.setListener(this);
        previewFrameView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        piliStreamPreview.addView(previewFrameView);
        initializeStreamingSessionIfNeeded(piliStreamPreview, previewFrameView);

        piliStreamPreview.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mMediaStreamingManager.resume();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mMediaStreamingManager.destroy();
            }
        });

        initChatView();
        return piliStreamPreview;
    }

    private void initChatView(){
        rvChatList = new RecyclerView(context);
        rvChatList.addItemDecoration(new SpaceItemDecoration(5));
        FrameLayout.LayoutParams lvPrams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lvPrams.bottomMargin = 200;
        lvPrams.height = 600;
        lvPrams.gravity = Gravity.BOTTOM;


        LinearLayout.LayoutParams rvPrams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rvPrams.setMargins(20,20,0,0);
        rvChatList.setLayoutParams(rvPrams);
        rvChatList.setMinimumHeight(500);
        rvChatList.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));


        llMiddle = new LinearLayout(context);
        llMiddle.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams middleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        middleParams.setMargins(5,0,0,0);
        middleParams.gravity=Gravity.CENTER;
        llMiddle.setLayoutParams(middleParams);


        LinearLayout.LayoutParams comParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        comParams.gravity=Gravity.CENTER;
        tvUserName = new TextView(context);
        tvUserName.setTextColor(context.getResources().getColor(R.color.c_5c_f));
        tvUserName.setTextSize(context.getResources().getDimension(R.dimen.f_5));
        tvUserName.setEms(4);
        tvUserName.setGravity(Gravity.CENTER);
        tvGiftName = new TextView(context);
        tvGiftName.setTextColor(context.getResources().getColor(R.color.c_ff));
        tvGiftName.setTextSize(context.getResources().getDimension(R.dimen.f_6));
        tvGiftName.setEms(3);
        tvGiftName.setGravity(Gravity.CENTER);

        tvSymbol = new TextView(context);
        tvUserName.setLayoutParams(comParams);
        tvGiftName.setLayoutParams(comParams);
        tvSymbol.setLayoutParams(comParams);

        llMiddle.addView(tvUserName);
        llMiddle.addView(tvGiftName);

        String no = "00";
        tvSymbol.setText("X");
        tvSymbol.setTextColor(context.getResources().getColor(R.color.c_ff));
        tvSymbol.setTextSize(context.getResources().getDimension(R.dimen.f_6));
        LinearLayout.LayoutParams tvSymbolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        tvSymbolParam.setMargins(5,0,0,0);
        tvSymbol.setLayoutParams(tvSymbolParam);
        tvGiftNameNo = new TextView(context);
        tvGiftNameNo.setText(no);
        tvGiftNameNo.setTextColor(context.getResources().getColor(R.color.c_ff));
        tvGiftNameNo.setTextSize(context.getResources().getDimension(R.dimen.f_8));
        tvGiftNameNo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvGiftNameNo.setPadding(2,0,0,0);
        tvGiftNameNo.setLayoutParams(comParams);

        llGiftNumLayout = new LinearLayout(context);
        LinearLayout.LayoutParams llGift = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        llGiftNumLayout.setGravity(Gravity.CENTER_VERTICAL);
        llGiftNumLayout.setOrientation(LinearLayout.HORIZONTAL);
        llGiftNumLayout.setPadding(5,5,10,5);
        llGiftNumLayout.setBackgroundResource(R.drawable.bg_gift_area);
        llGiftNumLayout.setLayoutParams(llGift);
        ivUserHeader = new CircleImageView(context);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivParams.width = DensityUtil.dip2px(context,40);
        ivParams.height = DensityUtil.dip2px(context,40);
        ivUserHeader.setLayoutParams(ivParams);
        String url = "http://www.qq745.com/uploads/allimg/141012/1-141012103344.jpg";
        Glide.with(context).load(url).centerCrop().into(ivUserHeader);
        llGiftNumLayout.addView(ivUserHeader);
        llGiftNumLayout.addView(llMiddle);
        llGiftNumLayout.addView(tvSymbol);
        llGiftNumLayout.addView(tvGiftNameNo);
        llGiftNumLayout.setVisibility(View.INVISIBLE);

        llChatList = new LinearLayout(context);
        llChatList.setOrientation(LinearLayout.VERTICAL);
        llChatList.addView(llGiftNumLayout);
        llChatList.addView(rvChatList);


        rl = new LinearLayout(context);
        rl.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.height = 55;
        rl.setPadding(0,10,0,10);
        rl.setBackgroundColor(context.getResources().getColor(R.color.c_a8_0));

        tvChat = new TextView(context);
        tvChat.setText("私信");
        tvChat.setGravity(Gravity.CENTER);
        tvChat.setTextColor(context.getResources().getColor(R.color.c_f));
        tvChat.setTextSize(context.getResources().getDimension(R.dimen.f_6));
        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_chat);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        tvChat.setCompoundDrawables(null,drawable,null,null);


        tvPrivateChat = new TextView(context);
        tvPrivateChat.setText("踢人");
        tvPrivateChat.setGravity(Gravity.CENTER);
        tvPrivateChat.setTextColor(context.getResources().getColor(R.color.c_f));
        tvPrivateChat.setTextSize(context.getResources().getDimension(R.dimen.f_6));
        Drawable drawableR = context.getResources().getDrawable(R.drawable.ic_remove_user);
        drawableR.setBounds(0, 0, drawableR.getMinimumWidth(), drawableR.getMinimumHeight()); //设置边界
        tvPrivateChat.setCompoundDrawables(null,drawableR,null,null);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        // getTextView02设置权重是2
        tvChat.setLayoutParams(lp);
//        lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        tvPrivateChat.setLayoutParams(lp);


        piliStreamPreview.addView(llChatList,lvPrams);
//        mVideoView.addView(rvChatList);
        rl.addView(tvChat);
        rl.addView(tvPrivateChat);
        piliStreamPreview.addView(rl,layoutParams);

        FrameLayout.LayoutParams rlPrams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlPrams.gravity = Gravity.BOTTOM;
        rl.setLayoutParams(rlPrams);


        chatAdapter = new ChatAdapter(context,mChatlist);
        rvChatList.setAdapter(chatAdapter);

        tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.log("私信===ttt==="+12121);
                showPrivateListPop();
            }
        });

        tvPrivateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.log("踢人===ttt==="+12121);
//                getUserData();
                queryOnlineList();
                showUserListPop();
            }
        });

        //启动websocket
//        startWebSocket();

    }

    @Override
    /**
     * <Streaming />
     */
    public String getName() {
        return "RCTLiveView";
    }

    @ReactProp(name = "stream")
    public void setStream(AspectFrameLayout view, @Nullable ReadableMap stream) {
        try {
            LogUtil.log("stream==tttt==="+stream.toString());
            JSONObject json = ReadableMapUtil.readableMap2JSON(stream);
            JSONObject streamJson = json.getJSONObject("livejosn");
            liveid = String.valueOf(new Double(json.getDouble("liveid")).longValue());
            LogUtil.log("stream==tttt=liveid=="+liveid);
            LogUtil.log("stream==tttt=liveid=="+streamJson.toString());

            mProfile.setStream(new StreamingProfile.Stream(streamJson));
            mMediaStreamingManager.setStreamingProfile(mProfile);
            LogUtil.log("启动 websocket开始 =="+streamJson.toString());
            startWebSocket();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @ReactProp(name = "stop")
    public void setStop(AspectFrameLayout view, boolean stop) {
        if(stop){
            stopStreaming();
        }
    }

    @ReactProp(name = "liveInfo")
    public void setLiveInfo(AspectFrameLayout view, @Nullable ReadableMap liveInfo) {
        mLiveInfo = ReadableMapUtil.readableMap2JSON(liveInfo);
    }

    @ReactProp(name = "profile")
    public void setProfile(AspectFrameLayout view, @Nullable ReadableMap profile) {
        ReadableMap video = profile.getMap("video");
        ReadableMap audio = profile.getMap("audio");

        StreamingProfile.AudioProfile aProfile =
                new StreamingProfile.AudioProfile(audio.getInt("rate"), audio.getInt("bitrate")); //audio sample rate, audio bitrate
        StreamingProfile.VideoProfile vProfile =
                new StreamingProfile.VideoProfile(video.getInt("fps"), video.getInt("bps"), video.getInt("maxFrameInterval"));//fps bps maxFrameInterval
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
        mProfile.setAVProfile(avProfile);
        mMediaStreamingManager.setStreamingProfile(mProfile);

    }

    @ReactProp(name = "muted")
    public void setMuted(AspectFrameLayout view, boolean muted) {
        mMediaStreamingManager.mute(muted);
    }

    @ReactProp(name = "zoom")
    public void setZoom(AspectFrameLayout view, int zoom) {
        mCurrentZoom = zoom;
        mCurrentZoom = Math.min(mCurrentZoom, mMaxZoom);
        mCurrentZoom = Math.max(0, mCurrentZoom);
        mMediaStreamingManager.setZoomValue(zoom);
    }

    @ReactProp(name = "focus")
    public void setFocus(AspectFrameLayout view, boolean focus) {
        this.focus = focus;
    }

    @ReactProp(name = "started")
    public void setStarted(AspectFrameLayout view, boolean started) {
        if(this.started == started){
            //ignore
            return;
        }
        this.started = started;
        if (mIsReady) {  //没有准备好则只赋值,等待onStateChanged 唤起
            if (started) {
                startStreaming();
            } else {
                stopStreaming();
            }
        }
    }

    protected void setFocusAreaIndicator() {
        if (mRotateLayout == null) {
            mRotateLayout = new FocusIndicatorRotateLayout(context, null);
            mRotateLayout
                    .setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER
                    ));
            View indicator = new View(context);
            indicator.setLayoutParams(new ViewGroup.LayoutParams(120, 120));
            mRotateLayout.addView(indicator);
            piliStreamPreview.addView(mRotateLayout);
            mMediaStreamingManager.setFocusAreaIndicator(mRotateLayout,indicator);
        }
    }

    public int getTargetId() {
        return piliStreamPreview.getId();
    }

    @Override
    public void onStateChanged(StreamingState state, Object extra) {
        switch (state) {
            case PREPARING:
                break;
            case READY:
                mIsReady = true;
                mMaxZoom = mMediaStreamingManager.getMaxZoom();
                if (started) {
                    startStreaming();
                }
                mEventEmitter.receiveEvent(getTargetId(), Events.READY.toString(), Arguments.createMap());
                break;
            case CONNECTING:
                mEventEmitter.receiveEvent(getTargetId(), Events.CONNECTING.toString(), Arguments.createMap());
                break;
            case STREAMING:
                mEventEmitter.receiveEvent(getTargetId(), Events.STREAMING.toString(), Arguments.createMap());
                break;
            case SHUTDOWN:
                mEventEmitter.receiveEvent(getTargetId(), Events.SHUTDOWN.toString(), Arguments.createMap());
                break;
            case IOERROR:
                mEventEmitter.receiveEvent(getTargetId(), Events.IOERROR.toString(), Arguments.createMap());
                break;
            case UNKNOWN:
                break;
            case SENDING_BUFFER_EMPTY:
                break;
            case SENDING_BUFFER_FULL:
                break;
            case AUDIO_RECORDING_FAIL:
                break;
            case OPEN_CAMERA_FAIL:
                break;
            case DISCONNECTED:
                mEventEmitter.receiveEvent(getTargetId(), Events.DISCONNECTED.toString(), Arguments.createMap());
                break;
            case CAMERA_SWITCHED:
                if (extra != null) {
                    Log.i(TAG, "current camera id:" + (Integer) extra);
                }
                Log.i(TAG, "camera switched");
                break;
            case TORCH_INFO:
                if (extra != null) {
                    final boolean isSupportedTorch = (Boolean) extra;
                    Log.i(TAG, "isSupportedTorch=" + isSupportedTorch);
                }
                break;
        }
    }


    @Override
    public boolean onRecordAudioFailedHandled(int err) {
        mMediaStreamingManager.updateEncodingType(AVCodecType.SW_VIDEO_CODEC);
        mMediaStreamingManager.startStreaming();
        return true;
    }

    @Override
    public boolean onRestartStreamingHandled(int err) {
        Log.i(TAG, "onRestartStreamingHandled");
        return mMediaStreamingManager.startStreaming();
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        Camera.Size size = null;
        if (list != null) {
            for (Camera.Size s : list) {
                if (s.height >= 480) {
                    size = s;
                    break;
                }
            }
        }
        Log.e(TAG, "selected size :" + size.width + "x" + size.height);
        return size;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp X:" + e.getX() + ",Y:" + e.getY());

        if (mIsReady) {
            setFocusAreaIndicator();
            mMediaStreamingManager.doSingleTapUp((int) e.getX(), (int) e.getY());
            return true;
        }
        return false;
    }

    @Override
    public boolean onZoomValueChanged(float factor) {
        if (mIsReady && mMediaStreamingManager.isZoomSupported()) {
            mCurrentZoom = (int) (mMaxZoom * factor);
            mCurrentZoom = Math.min(mCurrentZoom, mMaxZoom);
            mCurrentZoom = Math.max(0, mCurrentZoom);

            Log.d(TAG, "zoom ongoing, scale: " + mCurrentZoom + ",factor:" + factor + ",maxZoom:" + mMaxZoom);
            if (!mHandler.hasMessages(MSG_SET_ZOOM)) {
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ZOOM), ZOOM_MINIMUM_WAIT_MILLIS);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onHostResume() {
        mMediaStreamingManager.resume();
    }

    @Override
    public void onHostPause() {
        mMediaStreamingManager.pause();
    }

    @Override
    public void onHostDestroy() {
        mMediaStreamingManager.destroy();
    }


    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == Constants.MSG_CHAT_REFRESH){
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
                Glide.with(context).load(giftMsg.getPhoto()).centerCrop().into(ivUserHeader);
                tvGiftNameNo.setText(giftMsg.getQuantity());
                tvGiftName.setText(giftMsg.getGiftname());
                tvUserName.setText(giftMsg.getUsername());
            }



            switch (msg.what) {
                case MSG_START_STREAMING:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean res = mMediaStreamingManager.startStreaming();
                            Log.i(TAG, "res:");
                        }
                    }).start();
                    break;
                case MSG_STOP_STREAMING:
                    boolean res = mMediaStreamingManager.stopStreaming();
                    break;
                case MSG_SET_ZOOM:
                    mMediaStreamingManager.setZoomValue(mCurrentZoom);
                    break;
                default:
                    Log.e(TAG, "Invalid message" + android.R.drawable.stat_sys_phone_call_forward);
            }
        }
    };

    private void startStreaming() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_STREAMING), 50);
    }

    private void stopStreaming() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_STOP_STREAMING), 50);
    }

    private DnsManager getMyDnsManager() {
        IResolver r0 = new DnspodFree();
        IResolver r1 = AndroidDnsServer.defaultResolver();
        IResolver r2 = null;
        try {
            r2 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
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

    //websocket 参数
    private String liveid;
    private String user = "11";
    private String receivedId = "27";
    private String accounttype = "1";
    private String username = "我是主播";
    private String photoPath = "687474703A2F2F6F6369683277666F382E626B742E636C6F7564646E2E636F6D2F31343732323935343638373236";
    private void startWebSocket(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                URI uri=null;

                try {
                    String resUrl = Request.getChatUrl(liveid,user,accounttype, StringUtil.convertStringToHex(username),photoPath);
                    uri = new URI(resUrl);
                    LogUtil.log("received"+resUrl);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ChatWebSocket webSocketWorker=new ChatWebSocket(uri, new Draft_17());
                try {
                    webSocketWorker.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                webSocketWorker.send("你好");
            }
        }).start();
    }



    private PriListPopwindow priListPopwindow;
    private void showPrivateListPop(){
        if (priListPopwindow == null){
            priListPopwindow = new PriListPopwindow(context);
            priListPopwindow.setHeight((int)(DensityUtil.getHeight(context)*(0.4f)));
            priListPopwindow.showAtLocation(rl, Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
        }else {
            priListPopwindow.showAtLocation(rl,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
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

    private UserPopwindow userPopwindow;
    private void showUserListPop(){
//        if (userPopwindow == null){
//            userPopwindow = new UserPopwindow(context,userListBeen);
//            userPopwindow.setHeight((int)(DensityUtil.getHeight(context)*(0.4f)));
//            userPopwindow.showAtLocation(rl, Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
//        }else {
//            priListPopwindow.showAtLocation(rl,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
//        }
        userPopwindow = new UserPopwindow(context,watchUserList,user,liveid);
        userPopwindow.setHeight((int)(DensityUtil.getHeight(context)*(0.4f)));
        userPopwindow.showAtLocation(rl, Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
    }

    private void showPrivateChatPop(AllMsgBean allMsgBean){
        if (sendMsgPopwindow == null){
            sendMsgPopwindow = new SendMsgPopwindow(context,user,receivedId,allMsgBean,liveid);
            sendMsgPopwindow.setHeight((int)(DensityUtil.getHeight(context)*(0.4f)));
            sendMsgPopwindow.showAtLocation(rl,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
        }else {
            sendMsgPopwindow.showAtLocation(rl,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,0);
        }
    }


    List<UserBean.UserListBean> userListBeen = new ArrayList<>();

    private void getUserData(){
        if (userListBeen!=null){
            userListBeen.clear();
        }
        for (int i = 0; i < 10; i++) {
            UserBean.UserListBean listBean = new UserBean.UserListBean();
            listBean.setLookliveid("27");
            listBean.setPhotoname("http://pic.4j4j.cn/upload/pic/20130815/31e652fe2d.jpg");
            userListBeen.add(listBean);
        }

    }

    private List<WatchUserListBean.ListBean> watchUserList = new ArrayList<>();
    private void queryOnlineList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String LookUserData = Request.querUserList(user,liveid);
                LogUtil.log("LookUserData==tt=="+LookUserData.toString());
                WatchUserListBean watchUserListBean = JSON.parseObject(LookUserData,WatchUserListBean.class);
                if (watchUserListBean != null) {
                    watchUserList = watchUserListBean.getList();
                }
            }
        }).start();
    }

}
