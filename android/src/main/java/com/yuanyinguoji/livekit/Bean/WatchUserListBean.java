package com.yuanyinguoji.livekit.Bean;

import java.util.List;

/**
 * Created by chenyabing on 16/9/22.
 */

public class WatchUserListBean extends BaseBean {


    /**
     * list : [{"photo":"http://ocih2wfo8.bkt.clouddn.com/1473156962","nickname":"乔巴","accountid":"27","lookliveid":"59","liveid":"14745325140750"}]
     * accountid : 11
     */

    private String accountid;
    /**
     * photo : http://ocih2wfo8.bkt.clouddn.com/1473156962
     * nickname : 乔巴
     * accountid : 27
     * lookliveid : 59
     * liveid : 14745325140750
     */

    private List<ListBean> list;

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
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
    }
}
