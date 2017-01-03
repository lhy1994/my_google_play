package com.example.liuhaoyuan.mygoogleplay.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	public static String encode(String pass) {
		MessageDigest instance = null;
		try {
			instance = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] digest = instance.digest(pass.getBytes());
		StringBuffer stringBuffer=new StringBuffer();
		for(byte b:digest){
			int i=b&0xff;
			String hexstrString= Integer.toHexString(i);
			if(hexstrString.length()<2){
				hexstrString="0"+hexstrString;
			}
			stringBuffer.append(hexstrString);
		}
		return stringBuffer.toString();
	}
}
