package com.yuanyinguoji.livekit.net.request;

public interface RequestURL {
	
//	String BASE_URL = "http://manage.paywing.com";// 正式服务器器地址
//	String BASE_URL = "http://192.168.1.3:9001";// 测试服务器地址
	String BASE_URL = "http://139.129.233.39:9001";// 测试服务器地址
	String gift_list=BASE_URL+"/appUser/queryGiftList";  //礼物列表
	String send_gift=BASE_URL+"/appUser/senderGift";  //发送礼物
	String msg_list=BASE_URL+"/appUser/sendPrviteMail";  //发送私信
	String chat_online_list=BASE_URL+"/appUser/chatOnline";  //在线聊天
	String kick_liveinfo=BASE_URL+"/appUser/lookLiveinfoTiren";  //踢出直播
	String kick_lookuser_list=BASE_URL+"/appUser/lookLiveinfoOnlineList";  //查询踢人列表
	String kick_add_lookuser=BASE_URL+"/appUser/lookLiveinfo";  //添加观看记录

}
