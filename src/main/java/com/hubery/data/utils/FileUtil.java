/**
 * 
 */
package com.hubery.data.utils;

import java.io.File;

/**
 * @author Hubery
 * @createDate 2016年11月24日
 */
public class FileUtil {
	public static void delete(File f) {
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (File sonFile : fs) {
				delete(sonFile);
			}
		}
		f.delete();
	}

	public static void main(String[] args) {
		File f = new File("C:/test/aaa");
		delete(f);
	}
}
