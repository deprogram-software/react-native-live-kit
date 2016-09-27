package com.yuanyinguoji.livekit.application;


import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import java.util.Random;


public class SoftApplication extends Application {

	public static SoftApplication softApplication;

	public static String signKey = "12345678";
	public static String encryptKey = "99999999";



	@Override
	public void onCreate() {
		softApplication = this;
		super.onCreate();


		produce();

	}

	// 产生key
	public static void produce() {
		signKey = getKey();
		encryptKey = getKey();
	}

	// 产生字母加数字的随机数
	public static String getKey() {
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 8; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字
			if ("char".equalsIgnoreCase(charOrNum)) { // 字符串
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
				buffer.append((char) (choice + random.nextInt(26)));
			} else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
				buffer.append(random.nextInt(10));
			}
		}
		return buffer.toString();
	}



	/**
	 * 得到系统的版本号
	 * 
	 * @return
	 */
	public String getOSVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 得到应用的版本号
	 * 
	 * @return
	 */
	public int getAppVersionCode() {
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		int versionCode = 0;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			versionCode = packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 得到应用的版本号
	 * 
	 * @return
	 */
	public String getAppVersionName() {
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		String versionCode = "";
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			versionCode = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}







}
