package com.yuanyinguoji.livekit.Bean;

import java.util.List;

/**
 * Created by chenyabing on 16/9/1.
 */

public class UserBean extends BaseBean {

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    private int totalnumber;
    private String accountid;
    /**
     * conttext : 测试
     * senderid : 1
     * receiveid : 5
     * photoname : http://ocih2wfo8.bkt.clouddn.com/1472467313271
     */

    private List<UserListBean> list;

    public int getTotalnumber() {
        return totalnumber;
    }

    public void setTotalnumber(int totalnumber) {
        this.totalnumber = totalnumber;
    }

    public List<UserListBean> getList() {
        return list;
    }

    public void setList(List<UserListBean> list) {
        this.list = list;
    }

    public static class UserListBean {
        private String conttext;
        private String senderid;
        private String receiveid;
        private String photoname;
        private String photo;
        private String nickname;
        private String accountid;
        private String lookliveid;
        private String liveid;

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAccountid() {
            return accountid;
        }

        public void setAccountid(String accountid) {
            this.accountid = accountid;
        }

        public String getLookliveid() {
            return lookliveid;
        }

        public void setLookliveid(String lookliveid) {
            this.lookliveid = lookliveid;
        }

        public String getLiveid() {
            return liveid;
        }

        public void setLiveid(String liveid) {
            this.liveid = liveid;
        }

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
