package com.yuanyinguoji.livekit.Lisener;

import com.yuanyinguoji.livekit.Bean.AllMsgBean;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by chenyabing on 16/9/10.
 */

public class ListenerManager {

    /**
     * 单例模式
     */
    public static ListenerManager listenerManager;

    /**
     * 注册的接口集合，发送广播的时候都能收到
     */
    private List<IListener> iListenerList = new CopyOnWriteArrayList<IListener>();

    /**
     * 获得单例对象
     */
    public static ListenerManager getInstance()
    {
        if(listenerManager == null)
        {
            listenerManager = new ListenerManager();
        }
        return listenerManager;
    }

    /**
     * 注册监听
     */
    public void registerListtener(IListener iListener)
    {
        iListenerList.add(iListener);
    }

    /**
     * 注销监听
     */
    public void unRegisterListener(IListener iListener)
    {
        if(iListenerList.contains(iListener))
        {
            iListenerList.remove(iListener);
        }
    }

    /**
     * 发送广播
     */
    public void sendBroadCast(AllMsgBean allMsgBean )
    {
        for (IListener iListener : iListenerList)
        {
            iListener.notifyUpdatePage(allMsgBean);
        }
    }

    public void sendBroadCastMsg(String  msg )
    {
        for (IListener iListener : iListenerList)
        {
            iListener.notifyAllActivity(msg);
        }
    }
}
