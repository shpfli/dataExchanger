package com.hubery.data.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Hubery
 * @createDate 2016年11月24日
 */
public class ZipUtil {
	/**
	 * 解压zip文件到指定目录
	 * 
	 * @param zipFile
	 * @param destDir
	 */
	public static void unzip(File zipFile, File destDir) {
		ZipInputStream zipIn = null;
		BufferedInputStream bIn = null;

		try {
			zipIn = new ZipInputStream(new FileInputStream(zipFile));
			bIn = new BufferedInputStream(zipIn);

			if (!destDir.isDirectory()) {
				destDir.mkdirs();
			}

			ZipEntry zipEntry = null;
			while ((zipEntry = zipIn.getNextEntry()) != null) {
				File file = new File(destDir, zipEntry.getName());
				if (zipEntry.isDirectory()) {
					file.mkdirs();
				} else {
					if (file.exists()) {
						file.delete();
					} else {
						file.getParentFile().mkdirs();
					}
					file.createNewFile();

					FileOutputStream fout = null;
					BufferedOutputStream bout = null;

					try {
						fout = new FileOutputStream(file);
						bout = new BufferedOutputStream(fout);

						int b;
						while ((b = bIn.read()) != -1) {
							bout.write(b);
						}

					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						IOUtil.closeQuietly(bout, fout);
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeQuietly(bIn, zipIn);
		}

	}

	/**
	 * 压缩文件或文件夹
	 * 
	 * @param zipFile
	 * @param sourceFile
	 */
	public static void zip(File zipFile, File sourceFile) {
		ZipOutputStream zipOutputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		System.out.println("开始压缩文件……");
		try {
			zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
			bufferedOutputStream = new BufferedOutputStream(zipOutputStream);

			zip(zipOutputStream, sourceFile, sourceFile.getName(), bufferedOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeQuietly(bufferedOutputStream, zipOutputStream);
		}
		System.out.println("压缩完成！见：" + zipFile.getPath());
	}

	private static void zip(ZipOutputStream zipOutputStream, File sourceFile, String base,
			BufferedOutputStream bufferedOutputStream) throws IOException {
		if (sourceFile.isDirectory()) {
			File[] sonFiles = sourceFile.listFiles();
			if (sonFiles.length == 0) {
				zipOutputStream.putNextEntry(new ZipEntry(base + "/"));
				System.out.println(base + "/");
			}

			for (File file : sonFiles) {
				zip(zipOutputStream, file, base + "/" + file.getName(), bufferedOutputStream);
			}
		} else {
			bufferedOutputStream.flush();//防止上个文件还没写完
			zipOutputStream.putNextEntry(new ZipEntry(base));
			System.out.println(base);

			FileInputStream in = null;
			BufferedInputStream bin = null;
			try {
				in = new FileInputStream(sourceFile);
				bin = new BufferedInputStream(in);

				int b;
				while ((b = bin.read()) != -1) {
					bufferedOutputStream.write(b);
				}
			} catch (IOException e) {
				throw e;
			} finally {
				IOUtil.closeQuietly(bin, in);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File zipFile = new File("C:\\test\\test.zip");
		File sourceFile = new File("C:\\test\\exchange-b_claim_ebao-20161124215058");
		// File destDir = new File("C:/test");

		zip(zipFile, sourceFile);
		// unzip(zipFile, destDir);
	}
}
