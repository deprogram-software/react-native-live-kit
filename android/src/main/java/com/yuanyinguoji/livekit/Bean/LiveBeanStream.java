package com.yuanyinguoji.livekit.Bean;

/**
 * Created by chenyabing on 16/9/20.
 */

public class LiveBeanStream {


    /**
     * flag : 1000
     * liveid : 14743720401542
     * livejosn : {"hosts":{"play":{"rtmp":"pili-live-rtmp.shanglianinfo.com","http":"pili-live-hls.shanglianinfo.com"},"playback":{"http":"10002zi.playback1.z1.pili.qiniucdn.com","hls":"10002zi.playback1.z1.pili.qiniucdn.com"},"live":{"rtmp":"pili-live-rtmp.shanglianinfo.com","snapshot":"pili-live-snapshot.shanglianinfo.com","http":"pili-live-hls.shanglianinfo.com","hls":"pili-live-hls.shanglianinfo.com","hdl":"pili-live-hdl.shanglianinfo.com"},"publish":{"rtmp":"pili-publish.shanglianinfo.com"}},"publishSecurity":"static","hub":"shanglian-live","title":"14743720401542","disabled":false,"updatedAt":"2016-09-20T19:47:20.183+08:00","disabledTill":0,"createdAt":"2016-09-20T19:47:20.183+08:00","publishKey":"3e31369388a69303","id":"z1.shanglian-live.14743720401542"}
     */

    private NativeMapBean NativeMap;

    public NativeMapBean getNativeMap() {
        return NativeMap;
    }

    public void setNativeMap(NativeMapBean NativeMap) {
        this.NativeMap = NativeMap;
    }

    public static class NativeMapBean {
        private String flag;
        private long liveid;
        /**
         * hosts : {"play":{"rtmp":"pili-live-rtmp.shanglianinfo.com","http":"pili-live-hls.shanglianinfo.com"},"playback":{"http":"10002zi.playback1.z1.pili.qiniucdn.com","hls":"10002zi.playback1.z1.pili.qiniucdn.com"},"live":{"rtmp":"pili-live-rtmp.shanglianinfo.com","snapshot":"pili-live-snapshot.shanglianinfo.com","http":"pili-live-hls.shanglianinfo.com","hls":"pili-live-hls.shanglianinfo.com","hdl":"pili-live-hdl.shanglianinfo.com"},"publish":{"rtmp":"pili-publish.shanglianinfo.com"}}
         * publishSecurity : static
         * hub : shanglian-live
         * title : 14743720401542
         * disabled : false
         * updatedAt : 2016-09-20T19:47:20.183+08:00
         * disabledTill : 0
         * createdAt : 2016-09-20T19:47:20.183+08:00
         * publishKey : 3e31369388a69303
         * id : z1.shanglian-live.14743720401542
         */

        private LivejosnBean livejosn;

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public long getLiveid() {
            return liveid;
        }

        public void setLiveid(long liveid) {
            this.liveid = liveid;
        }

        public LivejosnBean getLivejosn() {
            return livejosn;
        }

        public void setLivejosn(LivejosnBean livejosn) {
            this.livejosn = livejosn;
        }

        public static class LivejosnBean {
            /**
             * play : {"rtmp":"pili-live-rtmp.shanglianinfo.com","http":"pili-live-hls.shanglianinfo.com"}
             * playback : {"http":"10002zi.playback1.z1.pili.qiniucdn.com","hls":"10002zi.playback1.z1.pili.qiniucdn.com"}
             * live : {"rtmp":"pili-live-rtmp.shanglianinfo.com","snapshot":"pili-live-snapshot.shanglianinfo.com","http":"pili-live-hls.shanglianinfo.com","hls":"pili-live-hls.shanglianinfo.com","hdl":"pili-live-hdl.shanglianinfo.com"}
             * publish : {"rtmp":"pili-publish.shanglianinfo.com"}
             */

            private HostsBean hosts;
            private String publishSecurity;
            private String hub;
            private String title;
            private boolean disabled;
            private String updatedAt;
            private int disabledTill;
            private String createdAt;
            private String publishKey;
            private String id;

            public HostsBean getHosts() {
                return hosts;
            }

            public void setHosts(HostsBean hosts) {
                this.hosts = hosts;
            }

            public String getPublishSecurity() {
                return publishSecurity;
            }

            public void setPublishSecurity(String publishSecurity) {
                this.publishSecurity = publishSecurity;
            }

            public String getHub() {
                return hub;
            }

            public void setHub(String hub) {
                this.hub = hub;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public boolean isDisabled() {
                return disabled;
            }

            public void setDisabled(boolean disabled) {
                this.disabled = disabled;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public int getDisabledTill() {
                return disabledTill;
            }

            public void setDisabledTill(int disabledTill) {
                this.disabledTill = disabledTill;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getPublishKey() {
                return publishKey;
            }

            public void setPublishKey(String publishKey) {
                this.publishKey = publishKey;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public static class HostsBean {
                /**
                 * rtmp : pili-live-rtmp.shanglianinfo.com
                 * http : pili-live-hls.shanglianinfo.com
                 */

                private PlayBean play;
                /**
                 * http : 10002zi.playback1.z1.pili.qiniucdn.com
                 * hls : 10002zi.playback1.z1.pili.qiniucdn.com
                 */

                private PlaybackBean playback;
                /**
                 * rtmp : pili-live-rtmp.shanglianinfo.com
                 * snapshot : pili-live-snapshot.shanglianinfo.com
                 * http : pili-live-hls.shanglianinfo.com
                 * hls : pili-live-hls.shanglianinfo.com
                 * hdl : pili-live-hdl.shanglianinfo.com
                 */

                private LiveBean live;
                /**
                 * rtmp : pili-publish.shanglianinfo.com
                 */

                private PublishBean publish;

                public PlayBean getPlay() {
                    return play;
                }

                public void setPlay(PlayBean play) {
                    this.play = play;
                }

                public PlaybackBean getPlayback() {
                    return playback;
                }

                public void setPlayback(PlaybackBean playback) {
                    this.playback = playback;
                }

                public LiveBean getLive() {
                    return live;
                }

                public void setLive(LiveBean live) {
                    this.live = live;
                }

                public PublishBean getPublish() {
                    return publish;
                }

                public void setPublish(PublishBean publish) {
                    this.publish = publish;
                }

                public static class PlayBean {
                    private String rtmp;
                    private String http;

                    public String getRtmp() {
                        return rtmp;
                    }

                    public void setRtmp(String rtmp) {
                        this.rtmp = rtmp;
                    }

                    public String getHttp() {
                        return http;
                    }

                    public void setHttp(String http) {
                        this.http = http;
                    }
                }

                public static class PlaybackBean {
                    private String http;
                    private String hls;

                    public String getHttp() {
                        return http;
                    }

                    public void setHttp(String http) {
                        this.http = http;
                    }

                    public String getHls() {
                        return hls;
                    }

                    public void setHls(String hls) {
                        this.hls = hls;
                    }
                }

                public static class LiveBean {
                    private String rtmp;
                    private String snapshot;
                    private String http;
                    private String hls;
                    private String hdl;

                    public String getRtmp() {
                        return rtmp;
                    }

                    public void setRtmp(String rtmp) {
                        this.rtmp = rtmp;
                    }

                    public String getSnapshot() {
                        return snapshot;
                    }

                    public void setSnapshot(String snapshot) {
                        this.snapshot = snapshot;
                    }

                    public String getHttp() {
                        return http;
                    }

                    public void setHttp(String http) {
                        this.http = http;
                    }

                    public String getHls() {
                        return hls;
                    }

                    public void setHls(String hls) {
                        this.hls = hls;
                    }

                    public String getHdl() {
                        return hdl;
                    }

                    public void setHdl(String hdl) {
                        this.hdl = hdl;
                    }
                }

                public static class PublishBean {
                    private String rtmp;

                    public String getRtmp() {
                        return rtmp;
                    }

                    public void setRtmp(String rtmp) {
                        this.rtmp = rtmp;
                    }
                }
            }
        }
    }
}
