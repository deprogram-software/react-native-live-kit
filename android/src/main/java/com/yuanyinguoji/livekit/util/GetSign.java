package com.yuanyinguoji.livekit.util;


import com.yuanyinguoji.livekit.application.SoftApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GetSign {
	// 获取签名
	public static Map<String, String> getSignStr(Map<String, String> params) {
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
		String sign = signStr.substring(0, signStr.length() - 1)
				+ SoftApplication.getKey();

		// sign = MD5Utils.md5Encrypt(sign);

		params.put("clientSign", MD5Utils.md5Encrypt(sign));

		return params;

	}

}
