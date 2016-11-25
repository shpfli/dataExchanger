/**
 * 
 */
package com.hubery.data.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hubery
 *
 */
public class IndexFile {
	private String[] columns;
	private String[] columnTypes;
	private String separator;
	private long totalNum = 0;
	private List<DataFile> dataFiles = new ArrayList<>();

	public IndexFile() {
		super();
	}

	public static IndexFile read(File file, String _separator) throws IOException {
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		IndexFile indexFile = new IndexFile();

		indexFile.separator = _separator;
		if (_separator.trim().equals("|")) {
			_separator = "\\|";
		}

		try {
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);

			String line = bufferedReader.readLine();
			String[] tmp = line.split(": ");
			if ("columns".equals(tmp[0])) {
				indexFile.columns = tmp[1].split(_separator);
			}

			line = bufferedReader.readLine();
			tmp = line.split(": ");
			if ("types".equals(tmp[0])) {
				indexFile.columnTypes = tmp[1].split(_separator);
			}

			line = bufferedReader.readLine();
			tmp = line.split(": ");
			if ("totalNum".equals(tmp[0])) {
				indexFile.totalNum = Integer.valueOf(tmp[1]);
			}

			line = bufferedReader.readLine();// 第四行
			while ((line = bufferedReader.readLine()) != null) {// 第五行开始为数据文件
				String[] _array = line.split("\t");
				DataFile dataFile = new DataFile();
				dataFile.setFile(new File(file.getParentFile(), _array[0]));
				dataFile.setFileSize(Long.valueOf(_array[1]));
				dataFile.setDataCount(Integer.valueOf(_array[2]));
				dataFile.setMd5(_array[3]);
				indexFile.addDataFile(dataFile);
			}

		} catch (IOException e) {
			throw new IOException("索引文件" + file.getCanonicalPath() + "解析失败！");
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return indexFile;
	}

	/**
	 * 校验索引文件以及其下的数据文件
	 * 
	 * @return
	 */
	public String validate() {
		if (separator == null || columns == null || columnTypes == null || dataFiles.size() < 1) {
			return "索引文件校验失败！";
		}

		String msg = null;
		for (DataFile dataFile : dataFiles) {
			msg = dataFile.validate();
			if (msg != null) {
				return msg;
			}
		}
		return null;
	}

	public void writeToFile(File indexFile) throws IOException {
		if (!indexFile.exists()) {
			indexFile.createNewFile();
		}
		// 写入列名行
		StringBuilder strBuilder = new StringBuilder("columns: ");
		for (String column : this.columns) {
			strBuilder.append(column).append(this.separator);
		}
		strBuilder.deleteCharAt(strBuilder.length() - 1);
		strBuilder.append("\n");

		// 写入对应列类型行
		strBuilder.append("types: ");
		for (String type : columnTypes) {
			strBuilder.append(type).append(this.separator);
		}
		strBuilder.deleteCharAt(strBuilder.length() - 1);
		strBuilder.append("\n");

		FileWriter fileWriter = new FileWriter(indexFile);
		fileWriter.write(strBuilder.toString());
		fileWriter.write("totalNum: " + this.totalNum + "\n");

		fileWriter.write("fileName\tfileSize\tdataCount\tmd5\n");
		for (DataFile dataFile : dataFiles) {
			fileWriter.write(dataFile.getFile().getName() + "\t");
			fileWriter.write(dataFile.getFileSize() + "\t");
			fileWriter.write(dataFile.getDataCount() + "\t");
			fileWriter.write(dataFile.getMd5() + "\n");
		}
		fileWriter.close();
	}

	public void addDataFile(DataFile dataFile) {
		this.dataFiles.add(dataFile);
		this.totalNum += dataFile.getDataCount();
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public String[] getColumnTypes() {
		return columnTypes;
	}

	public void setColumnTypes(String[] columnTypes) {
		this.columnTypes = columnTypes;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public long getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(long totalNum) {
		this.totalNum = totalNum;
	}

	public List<DataFile> getDataFiles() {
		return dataFiles;
	}

	public void setDataFiles(List<DataFile> dataFiles) {
		this.dataFiles = dataFiles;
	}

}
