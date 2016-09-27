package com.yuanyinguoji.livekit.Lisener;

import com.yuanyinguoji.livekit.Bean.AllMsgBean;

import java.util.List;

/**
 * Created by chenyabing on 16/9/10.
 */

public interface IListener {

    void notifyAllActivity(String str);
    void notifyUpdatePage(AllMsgBean allMsgBean);

}
