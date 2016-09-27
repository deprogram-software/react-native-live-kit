package com.yuanyinguoji.livekit.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yuanyinguoji.livekit.Bean.AllMsgBean;
import com.yuanyinguoji.livekit.Bean.GiftBean;
import com.yuanyinguoji.livekit.Lisener.ListenerManager;
import com.yuanyinguoji.livekit.util.LogUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyabing on 16/9/2.
 */

public class ChatWebSocket extends WebSocketClient {

    public ChatWebSocket(URI serverUri , Draft draft ) {
        super( serverUri, draft );
    }

    public ChatWebSocket(URI serverURI ) {
        super( serverURI );
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println( "opened connection" );
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage( String message ) {
        System.out.println( "received: " + message );
        parseMsgData(message);
    }

    List<AllMsgBean> priMsgList = new ArrayList<>();
    AllMsgBean allMsgBean = null;
    private void parseMsgData(String msg){
        if (msg != null) {
            try {
                allMsgBean = JSON.parseObject(msg, AllMsgBean.class);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

//        LogUtil.log("allMsgBean===ttt=="+allMsgBean.toString());
        if (allMsgBean.getType().equals("private_mail")){
            allMsgBean.setSendAndReId("0");
//            ListenerManager.getInstance().sendBroadCast(allMsgBean);
        }
//        else if(allMsgBean.getType().equals("online_chat")){
//            LogUtil.log("online_chat===ttt=="+allMsgBean.getConttext());
//            ListenerManager.getInstance().sendBroadCast(allMsgBean);
//        }else if(allMsgBean.getType().equals("user_join")){
//
//        }else if(allMsgBean.getType().equals("user_leave")){
//
//        }else if(allMsgBean.getType().equals("gift_send")){
//            ListenerManager.getInstance().sendBroadCast(allMsgBean);
//        }
        ListenerManager.getInstance().sendBroadCast(allMsgBean);

    }

    @Override
    public void onClose(int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) );
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        System.out.println( "received: " + "链接错误!!!");

        // if the error is fatal then onClose will be called additionally
    }

}
