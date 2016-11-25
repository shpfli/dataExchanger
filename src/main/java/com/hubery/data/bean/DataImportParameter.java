/**
 * 
 */
package com.hubery.data.bean;

/**
 * @author Hubery
 * @createDate 2016年11月24日
 */
public class DataImportParameter {
	private String separator;
	private String importTable;
	private String importDir;

	public DataImportParameter() {
		super();
	}

	public DataImportParameter(String separator, String importTable, String importDir) {
		super();
		this.separator = separator;
		this.importTable = importTable;
		this.importDir = importDir;
	}

	/**
	 * 验证是否满足导入条件：参数是否合法，文件是否校验通过
	 * 
	 * @return
	 */
	public String validate() {
		if (separator == null) {
			return "未指定分隔符";
		}
		if (importTable == null) {
			return "未指定要导入的表名";
		}
		if (importDir == null) {
			return "未指定要导入的文件所在目录";
		}
		return null;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getImportTable() {
		return importTable;
	}

	public void setImportTable(String importTable) {
		this.importTable = importTable;
	}

	public String getImportDir() {
		return importDir;
	}

	public void setImportDir(String importDir) {
		this.importDir = importDir;
	}

	@Override
	public String toString() {
		return "DataImportParameter [separator=" + separator + ", importTable=" + importTable + ", importDir="
				+ importDir + "]";
	}

}
