package com.yuanyinguoji.livekit.Bean;

import java.util.List;

/**
 * Created by chenyabing on 16/9/1.
 */

public class MsgBean extends BaseBean {


    private int totalnumber;
    /**
     * conttext : 测试
     * senderid : 1
     * receiveid : 5
     * photoname : http://ocih2wfo8.bkt.clouddn.com/1472467313271
     */

    private List<ListBean> list;

    public int getTotalnumber() {
        return totalnumber;
    }

    public void setTotalnumber(int totalnumber) {
        this.totalnumber = totalnumber;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String conttext;
        private String senderid;
        private String receiveid;
        private String photoname;

        public String getConttext() {
            return conttext;
        }

        public void setConttext(String conttext) {
            this.conttext = conttext;
        }

        public String getSenderid() {
            return senderid;
        }

        public void setSenderid(String senderid) {
            this.senderid = senderid;
        }

        public String getReceiveid() {
            return receiveid;
        }

        public void setReceiveid(String receiveid) {
            this.receiveid = receiveid;
        }

        public String getPhotoname() {
            return photoname;
        }

        public void setPhotoname(String photoname) {
            this.photoname = photoname;
        }
    }
}
