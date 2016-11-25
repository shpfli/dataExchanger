/**
 * 
 */
package com.hubery.data.service;

import com.hubery.data.bean.DataExportParameter;
import com.hubery.data.bean.DataImportParameter;

/**
 * @author Hubery
 *
 */
public interface DataExchangeService {
	/**
	 * 数据导入
	 * 
	 * @param param
	 * @return
	 */
	public String importData(DataImportParameter param);

	/**
	 * 数据导出
	 * 
	 * @param param
	 * @return
	 */
	public String exportData(DataExportParameter param);
}
