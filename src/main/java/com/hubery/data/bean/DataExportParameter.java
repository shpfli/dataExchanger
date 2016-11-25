/**
 * 
 */
package com.hubery.data.bean;

import java.util.Arrays;

/**
 * @author Hubery
 *
 */
public class DataExportParameter {
	private String exportTable = null;
	private String exportDir = null;
	private String separator = null;
	private int maxSize = 0;
	private String exportTableColumns = null;
	private String[] columns = null;
	private boolean doCompress = true;

	public DataExportParameter() {
		super();
	}

	public String[] getColumns() {
		if (this.columns == null && !"*".equals(this.exportTableColumns) && this.exportTableColumns != null) {
			this.columns = this.exportTableColumns.split(",");
		}
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public String getExportTableColumns() {
		// 如果用户输入的*，表示全部字段。字段顺序由map的key顺序决定。
		if ("*".equals(this.exportTableColumns) && this.columns != null) {
			StringBuilder columnsStrBuilder = new StringBuilder();
			for (String key : columns) {
				columnsStrBuilder.append(key).append(",");
			}
			columnsStrBuilder.deleteCharAt(columnsStrBuilder.length() - 1);
			this.exportTableColumns = columnsStrBuilder.toString();
		}
		return exportTableColumns;
	}

	public void setExportTableColumns(String exportTableColumns) {
		this.exportTableColumns = exportTableColumns;
	}

	public String getExportTable() {
		return exportTable;
	}

	public void setExportTable(String exportTable) {
		this.exportTable = exportTable;
	}

	public String getExportDir() {
		return exportDir;
	}

	public void setExportDir(String exportDir) {
		this.exportDir = exportDir;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * @return the doCompress
	 */
	public boolean isDoCompress() {
		return doCompress;
	}

	/**
	 * @param doCompress
	 *            the doCompress to set
	 */
	public void setDoCompress(boolean doCompress) {
		this.doCompress = doCompress;
	}

	@Override
	public String toString() {
		return "DataExportParameter [exportTable=" + exportTable + ", exportDir=" + exportDir + ", separator="
				+ separator + ", maxSize=" + maxSize + ", exportTableColumns=" + exportTableColumns + ", columns="
				+ Arrays.toString(columns) + ", doCompress=" + doCompress + "]";
	}

}
