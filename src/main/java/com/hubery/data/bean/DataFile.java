/**
 * 
 */
package com.hubery.data.bean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.hubery.data.utils.MD5Util;

/**
 * @author Hubery
 *
 */
public class DataFile {
	private File file;
	private long fileSize = 0L;
	private int dataCount = 0;
	private String md5;

	private FileWriter writer;

	public DataFile() {
		super();
	}

	public DataFile(File file) {
		super();
		this.file = file;
	}

	public void writeLine(String line) throws IOException {
		if (this.writer == null) {
			this.writer = new FileWriter(this.file);
		}

		this.writer.write(line + "\n");
		this.fileSize = this.file.length();
		this.dataCount++;
	}

	public void close() {
		try {
			this.writer.close();
			this.fileSize = this.file.length();
			this.setMd5(MD5Util.getFileMD5String(this.file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String validate() {
		if (!file.isFile()) {
			return "数据文件" + file.getPath() + "不存在！";
		}

		try {
			String _md5 = MD5Util.getFileMD5String(file);
			if (!this.md5.equals(_md5)) {
				return "数据文件(" + file.getPath() + ")MD5校验失败！";
			}
		} catch (IOException e) {
			return e.getMessage();
		}

		return null;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getFileSize() {
		return fileSize;
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

}
