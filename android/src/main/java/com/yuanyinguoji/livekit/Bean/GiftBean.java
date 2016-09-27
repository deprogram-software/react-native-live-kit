package com.yuanyinguoji.livekit.Bean;

import java.util.List;

/**
 * Created by chenyabing on 16/8/29.
 */

public class GiftBean extends BaseBean{



    private String totalnumber;
    private List<GiftList> list;

    public String getTotalnumber() {
        return totalnumber;
    }

    public void setTotalnumber(String totalnumber) {
        this.totalnumber = totalnumber;
    }

    public List<GiftList> getList() {
        return list;
    }

    public void setList(List<GiftList> list) {
        this.list = list;
    }

    public static class GiftList {

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
}
