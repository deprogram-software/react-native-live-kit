package com.yuanyinguoji.livekit.net.request;


import com.yuanyinguoji.livekit.Bean.KickData;
import com.yuanyinguoji.livekit.net.APIHttpClient;
import com.yuanyinguoji.livekit.util.LogUtil;
import com.yuanyinguoji.livekit.util.MD5Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Request implements RequestURL {


	public static String getGiftList(String start, String pageSize) {

		JSONObject param = new JSONObject();
		try {
			param.put("pageNo", start);
			param.put("pageSize", pageSize);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		param = getSignStr(param);
//		LogUtils.i("签名是++++++++="+param);
		String params = param.toString();

		String giftData = new APIHttpClient(gift_list).postJson(params);
		return giftData;
	}

	public static String sendGift(String senderid, String receiveid, String giftid, String totalfee, String price, String liveid, String quantity) {

		JSONObject param = new JSONObject();
		try {
			param.put("senderid", senderid);
			param.put("receiveid", receiveid);
			param.put("giftid", giftid);
			param.put("totalfee", totalfee);
			param.put("price", price);
			param.put("liveid", liveid);
			param.put("quantity", quantity);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		param = getSignStr(param);
		LogUtil.log("签名是++++++++="+param);
		String params = param.toString();

		String giftData = new APIHttpClient(send_gift).postJson(params);

		return giftData;
	}

	public static String sendPriMsg(String senderid, String receiveid, String content, String liveid) {

		JSONObject param = new JSONObject();
		try {
			param.put("senderid", senderid);
			param.put("receiveid", receiveid);
			param.put("conttext", content);
			param.put("liveid", liveid);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		param = getSignStr(param);
		LogUtil.log("签名是++++++++="+param);
		String params = param.toString();

		String giftData = new APIHttpClient(msg_list).postJson(params);

		return giftData;
	}

	public static String sendChatMsg(String accountid, String content, String liveid) {

		JSONObject param = new JSONObject();
		try {
			param.put("accountid", accountid);
			param.put("conttext", content);
			param.put("liveid", liveid);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		param = getSignStr(param);
		LogUtil.log("签名是++++++++="+param);
		String params = param.toString();

		String giftData = new APIHttpClient(chat_online_list).postJson(params);

		return giftData;
	}


	public static String querUserList(String accountid, String liveid) {

		JSONObject param = new JSONObject();
		try {
			param.put("accountid", accountid);
			param.put("liveid", liveid);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		param = getSignStr(param);
		LogUtil.log("签名是++++++++="+param);
		String params = param.toString();

		String userData = new APIHttpClient(kick_lookuser_list).postJson(params);

		return userData;
	}


	public static String addUserRecord(String accountid, String liveid,String lookliveid,String status) {

		JSONObject param = new JSONObject();
		try {
			param.put("accountid", accountid);
			param.put("liveid", liveid);
			param.put("lookliveid", lookliveid);
			param.put("status", status);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		param = getSignStr(param);
		LogUtil.log("签名是++++++++="+param);
		String params = param.toString();

		String getAddState = new APIHttpClient(kick_add_lookuser).postJson(params);

		return getAddState;
	}


	/**
	 * 获取签名
	 *
	 * @param params
	 * @return
	 */
	public static JSONObject getSignStr(JSONObject params) {

		String sign = null;

		List<String> list = new ArrayList<String>();
		Iterator<String> it = params.keys();
		while (it.hasNext()) {
			list.add(it.next());
		}
		Collections.sort(list);

		String signStr = "";

		for (String i : list) {
			try {
				signStr += (i + "=" + params.get(i) + "&");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		signStr += "key=F81D34A4A965B1EAEEA6C94CF0471C11";
		LogUtil.log(signStr);
		sign = MD5Utils.md5Encrypt(signStr);
		LogUtil.log(sign);
		try {
			params.put("clientSign", sign);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return params;

	}

	/*
       * 踢人(liveid直播唯一标识, accountid用户登录id,status(2踢人),clientSign:验签,
       * list踢人列表(不参与签名 accountid观看用户编号lookliveid用户观看直播唯一标识))
       *
       * */
	public static String kickUuserFormLive(String accountid, String status, String liveid, List<KickData> kickDataList) {

		JSONObject param = new JSONObject();
		try {
			param.put("accountid", accountid);
			param.put("status", status);
			param.put("liveid", liveid);
			param = getSignStr(param);

			JSONArray json = new JSONArray();
			for(KickData kickData : kickDataList){
				JSONObject jo = new JSONObject();
				jo.put("accountid", kickData.getAccountid());
				jo.put("lookliveid", kickData.getLookliveid());
				json.put(jo);
			}
			param.put("list",json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LogUtil.log("签名是++++++++="+param);
		String params = param.toString();

		String kickUserData = new APIHttpClient(kick_liveinfo).postJson(params);
		LogUtil.log("kickData==tt=="+kick_liveinfo);

		return kickUserData;
	}


	//liveid=11&user=5&accounttype=1"+"&username="
	public static String getChatUrl(String liveid,String user,String accounttype,String username,String photoPath){
		JSONObject params = new JSONObject();
		try {
			params.put("photo",photoPath);
			params.put("liveid",liveid);
			params.put("user",user);
			params.put("accounttype",accounttype);
			params.put("username",username);
		}catch (JSONException e){
			e.printStackTrace();
		}

		String baseUrl = "ws://139.129.233.39:8110/WebSocket/message?";
		String webUrl;
		webUrl = baseUrl+"liveid="+liveid+"&user="+user+"&accounttype="+accounttype+"&username="+username+"&photo="+photoPath+"&clientSign="+getChatSignStr(params);
		return webUrl;
	}

	public static String getChatSignStr(JSONObject params) {

		String sign = null;

		List<String> list = new ArrayList<String>();
		Iterator<String> it = params.keys();
		while (it.hasNext()) {
			list.add(it.next());
		}
		Collections.sort(list);

		String signStr = "";

		for (String i : list) {
			try {
				signStr += (i + "=" + params.get(i) + "&");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		signStr += "key=ECE893EB7CB170CA5DEF54309485C0AF";
		LogUtil.log(signStr);
		sign = MD5Utils.md5Encrypt(signStr);
		LogUtil.log(sign);

		return sign;
	}
}
