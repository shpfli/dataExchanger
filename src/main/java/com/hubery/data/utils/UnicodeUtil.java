/**
 * 
 */
package com.hubery.data.utils;

/**
 * @author Hubery
 * @createDate 2016年11月24日
 */
public class UnicodeUtil {
	/**
	 * 字符串转换unicode
	 */
	public static String string2Unicode(String string) {
		StringBuffer unicode = new StringBuffer();

		for (int i = 0; i < string.length(); i++) {
			// 取出每一个字符
			char c = string.charAt(i);

			// 转换为unicode
			unicode.append("\\u" + Integer.toHexString(c));
		}
		return unicode.toString();
	}

	/**
	 * unicode 转字符串
	 */
	public static String unicode2String(String unicode) {
		StringBuffer string = new StringBuffer();
		String[] hex = unicode.split("\\\\u");
		for (int i = 1; i < hex.length; i++) {
			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);
			// 追加成string
			string.append((char) data);
		}
		return string.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] array = { "a|b", "ab", "afb", "asb" };
		String separator = "|";
		// System.out.println(StringEscapeUtils.escapeJava(separator));

		StringBuilder stringBuilder = new StringBuilder();

		for (String str : array) {
			stringBuilder.append(string2Unicode(str)).append(separator);
		}
		int index = stringBuilder.lastIndexOf(separator);
		stringBuilder.delete(index, stringBuilder.length());
		String testString = stringBuilder.toString();

		System.out.println("testString: " + testString);

		String[] a = testString.split("\\|");
		for (String str : a) {
			// System.out.println(str);
			System.out.println(unicode2String(str));
		}
	}

}
