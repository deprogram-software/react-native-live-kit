package com.yuanyinguoji.livekit.util;


import java.io.UnsupportedEncodingException;

public class StringUtil {

	/**
	 * 判断字符串是否为null或�?空字符串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		boolean result = false;
		if (null == str || "".equals(str.trim())) {
			result = true;
		}
		return result;
	}

	/**
	 * 如果i小于10，添�?后生成string
	 * 
	 * @param i
	 * @return
	 */
	public static String addZreoIfLessThanTen(int i) {

		String string = "";
		int ballNum = i + 1;
		if (ballNum < 10) {
			string = "0" + ballNum;
		} else {
			string = ballNum + "";
		}
		return string;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNotNull(String string) {
		if (null != string && !"".equals(string.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 去掉�?��字符串中的所有的单个空格" "
	 * 
	 * @param string
	 */
	public static String replaceSpaceCharacter(String string) {
		if (null == string || "".equals(string)) {
			return "";
		}
		return string.replace(" ", "");
	}

	/**
	 * 获取小数位为6位的经纬�?
	 * 
	 * @param string
	 * @return
	 */
	public static String getStringLongitudeOrLatitude(String string) {
		if (StringUtil.isNullOrEmpty(string)) {
			return "";
		}
		if (string.contains(".")) {
			String[] splitArray = string.split("\\.");
			if (splitArray[1].length() > 6) {
				String substring = splitArray[1].substring(0, 6);
				return splitArray[0] + "." + substring;
			} else {
				return string;
			}
		} else {
			return string;
		}
	}

//	/**
//	 * 课程状态转换
//	 *
//	 * @date 2013年9月3日
//	 * @param status
//	 * @return
//	 */
//	public static String parserStatusToString(int status) {
//		switch (status) {
//		case 0:
//			return Constants.LESSON_STATUS_0;
//		case 10:
//			return Constants.LESSON_STATUS_10;
//		case 11:
//			return Constants.LESSON_STATUS_11;
//		case 20:
//			return Constants.LESSON_STATUS_20;
//		case 21:
//			return Constants.LESSON_STATUS_21;
//		case 22:
//			return Constants.LESSON_STATUS_22;
//
//		default:
//			return "";
//		}
//	}


	/**
	 * 转字符串成十六进制的ASCII
	 * @param str
	 * @return
	 */
	public static String convertStringToHex(String str){

		try {
			return BinaryToHexString(str.getBytes("gbk"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	/**
	 *
	 * @param bytes
	 * @return 将二进制转换为十六进制字符输出
	 */
	public static String hexStr =  "0123456789ABCDEF";
	public static String BinaryToHexString(byte[] bytes){
		String result = "";
		String hex = "";
		for(int i=0;i<bytes.length;i++){
			hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));
			hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));
			result +=hex+"";
		}
		return result;
	}
}
