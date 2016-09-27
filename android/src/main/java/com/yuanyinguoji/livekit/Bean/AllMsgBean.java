package com.yuanyinguoji.livekit.Bean;

/**
 * Created by chenyabing on 16/9/10.
 */

public class AllMsgBean {

    /**
     * type : private_mail
     * user : 8
     * liveid : 11
     * receiveid : 4
     * conttext : 发送到发送到发大水发的说法是否
     * username : 你好
     */

    private String type;
    private String user;
    private String liveid;
    private String receiveid;
    private String conttext;
    private String username;
    private String sendAndReId;
    private Boolean isAhchorPri = false;

    public Boolean getAhchorPri() {
        return isAhchorPri;
    }

    public void setAhchorPri(Boolean ahchorPri) {
        isAhchorPri = ahchorPri;
    }

    /**
     * quantity : 1
     * giftname : 测试
       type
     * giftid : 1
     * photo : http://ocih2wfo8.bkt.clouddn.com/1472295468726
     */

    private String quantity;
    private String giftname;
    private String giftid;
    private String photo;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLiveid() {
        return liveid;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public String getReceiveid() {
        return receiveid;
    }

    public void setReceiveid(String receiveid) {
        this.receiveid = receiveid;
    }

    public String getConttext() {
        return conttext;
    }

    public void setConttext(String conttext) {
        this.conttext = conttext;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSendAndReId() {
        return sendAndReId;
    }

    public void setSendAndReId(String sendAndReId) {
        this.sendAndReId = sendAndReId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }

    public String getGiftid() {
        return giftid;
    }

    public void setGiftid(String giftid) {
        this.giftid = giftid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
