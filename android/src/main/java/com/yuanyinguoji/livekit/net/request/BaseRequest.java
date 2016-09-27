package com.yuanyinguoji.livekit.net.request;


import com.yuanyinguoji.livekit.util.MD5Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BaseRequest {
	
	
	
	/**
	 * 获取签名
	 * 
	 * @param params
	 * @return
	 */
	public static Map<String, String> getSignStr(Map<String, String> params) {
		
		String sign = null;
		Set<String> keys = params.keySet();
		List<String> list = new ArrayList<String>();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		Collections.sort(list);

		String signStr = "";

		for (String i : list) {
			if ("signType".equals(i)) {
				continue;
			} else {
				signStr += (i + "=" + params.get(i) + "&");
			}
		}
		sign += "key=F81D34A4A965B1EAEEA6C94CF0471C11";

		sign = MD5Utils.md5Encrypt(sign);

		params.put("sign", sign);

		return params;

	}
	
}
