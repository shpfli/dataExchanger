/**
 * 
 */
package com.hubery.data.utils;

/**
 * @author Hubery
 * @createDate 2016年11月23日
 */
public class StrEscapeUtil {

	/**
	 * 转义字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String escape(String str) {
		return UnicodeUtil.string2Unicode(str);
	}

	/**
	 * 反转义字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String unescape(String str) {
		return UnicodeUtil.unicode2String(str);
	}
}
