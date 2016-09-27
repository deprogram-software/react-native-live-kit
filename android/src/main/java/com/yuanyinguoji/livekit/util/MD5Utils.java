package com.yuanyinguoji.livekit.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	private static String YAN = "iuh378s6dgf7238s6rdf7gwsd23";

	/**
	 * MD5 ����
	 * 
	 * @param pwd
	 * @return
	 */
	public static String md5Encrypt(String pwd) {

		MessageDigest digest = null;

		try {
			digest = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// �����ȥ�������������ֽ����飬���صľ��ǻ��ܺ���ֽ�����
		byte[] bytes = digest.digest(pwd.getBytes());

		StringBuffer sb = new StringBuffer();

		for (byte b : bytes) { // 1 byte = 8 bit
			// �� b ת��Ϊ�޷�ŵ�����
			int num = b & 0xff;
			// ��������ʮ����Ʊ�ʾ
			String hexStr = Integer.toHexString(num);
			// ��� �ַ���Ϊ1 ǰ�油0
			if (hexStr.length() == 1) {
				sb.append("0" + hexStr);
			} else {
				sb.append(hexStr);
			}
		}
		return sb.toString();
	}
}
