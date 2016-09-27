package com.yuanyinguoji.livekit.Bean;

/**
 * Created by chenyabing on 16/8/29.
 */

public class GiftList {

    public String name;
    public String giftid;
    public String price;
    public String photoname;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGiftid() {
        return giftid;
    }

    public void setGiftid(String giftid) {
        this.giftid = giftid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhotoname() {
        return photoname;
    }

    public void setPhotoname(String photoname) {
        this.photoname = photoname;
    }

    @Override
    public String toString() {
        return "GiftList{" +
                "name='" + name + '\'' +
                ", giftid='" + giftid + '\'' +
                ", price='" + price + '\'' +
                ", photoname='" + photoname + '\'' +
                '}';
    }
}
