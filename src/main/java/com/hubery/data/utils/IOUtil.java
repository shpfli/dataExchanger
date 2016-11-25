/**
 * 
 */
package com.hubery.data.utils;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Hubery
 * @createDate 2016年11月24日
 */
public class IOUtil {
	/**
	 * 关闭一个或多个流对象
	 * 
	 * @param closeables
	 *            可关闭的流对象列表
	 * @throws IOException
	 */
	public static void close(Closeable... closeables) throws IOException {
		if (closeables != null) {
			for (Closeable closeable : closeables) {
				if (closeable != null) {
					closeable.close();
				}
			}
		}
	}

	/**
	 * 关闭一个或多个流对象
	 * 
	 * @param closeables
	 *            可关闭的流对象列表
	 */
	public static void closeQuietly(Closeable... closeables) {
		try {
			close(closeables);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		IOUtil.closeQuietly(null, new FileInputStream("C:\\test\\test.zip"));
		System.out.println("OK");
	}
}
