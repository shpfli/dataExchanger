/**
 * 
 */
package com.hubery.data.service.impl;

import java.io.File;
import java.lang.Thread.State;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.hubery.data.bean.DataExportParameter;
import com.hubery.data.bean.DataImportParameter;
import com.hubery.data.bean.IndexFile;
import com.hubery.data.bean.TaskStatus;
import com.hubery.data.dao.DataQuery;
import com.hubery.data.dao.DataReader;
import com.hubery.data.dao.DataWriter;
import com.hubery.data.dao.DbImporter;
import com.hubery.data.service.DataExchangeService;
import com.hubery.data.utils.FileUtil;
import com.hubery.data.utils.ZipUtil;

/**
 * @author Hubery
 *
 */
public class DataExchangeServiceImpl implements DataExchangeService {
	private static final int MAX_QUEUE_LENGTH = 10000;

	@Override
	public String importData(DataImportParameter param) {
		// 参数校验
		String msg = param.validate();
		if (msg != null) {
			return msg;
		}

		// 文件校验
		File appointedFile = new File(param.getImportDir());// 用户指定的文件或目录
		if (!appointedFile.exists()) {
			return "指定的“导入文件目录”不存在！";
		}

		File tmpDir = null;// 解压产生的临时目录
		File dataDir = null;// index.txt所在的目录，即工作目录

		if (appointedFile.isFile()) {// 如果是压缩文件，则先解压
			String zipName = appointedFile.getName();
			int index = zipName.lastIndexOf(".zip");

			String fileName = zipName;
			if (index > 0) {
				fileName = zipName.substring(0, index);
			}

			tmpDir = new File(appointedFile.getParentFile(), "tmp_" + System.currentTimeMillis());
			ZipUtil.unzip(appointedFile, tmpDir);
			dataDir = new File(tmpDir, fileName);
		} else {
			dataDir = appointedFile;
		}

		File indexFile = new File(dataDir, "index.txt");// 索引文件index.txt

		if (indexFile == null || !indexFile.isFile()) {
			return "指定目录未找到索引文件index.txt！";
		}

		IndexFile index_file;
		String resultMsg = null;
		try {
			index_file = IndexFile.read(indexFile, param.getSeparator());
			msg = index_file.validate();
			if (msg != null) {
				return msg;
			}

			resultMsg = this.startImportJob(index_file, param.getImportTable());
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = "导入失败！";
		}

		// 如果是程序生成的临时文件目录，则清楚掉
		if (dataDir.getName().startsWith("tmp_")) {
			FileUtil.delete(dataDir);
		}
		return resultMsg;
	}

	private String startImportJob(IndexFile indexFile, String tableName) {
		BlockingQueue<Map<String, Object>> queue = new LinkedBlockingQueue<>(MAX_QUEUE_LENGTH);

		DataReader dataReader = new DataReader(queue, indexFile);
		Thread producer = new Thread(dataReader);

		DbImporter dbImporter = new DbImporter(queue, indexFile, tableName, producer);
		Thread customer = new Thread(dbImporter);

		System.out.println("\n导入任务开始……");

		producer.start();
		customer.start();

		while (!producer.getState().equals(State.TERMINATED) || !customer.getState().equals(State.TERMINATED)) {
			Thread.yield();
		}
		TaskStatus readTaskStatus = dataReader.getTaskStatus(), importTaskStatus = dbImporter.getTaskStatus();

		System.out.println("导入任务结束！");
		if (readTaskStatus.isSuccess() && importTaskStatus.isSuccess()) {
			return "导入成功！";
		} else {
			StringBuilder errorMsg = new StringBuilder();
			if (!readTaskStatus.isSuccess()) {
				errorMsg.append(readTaskStatus.getMsg()).append("\n");
			}
			if (!importTaskStatus.isSuccess()) {
				errorMsg.append(importTaskStatus.getMsg());
			}
			return errorMsg.toString();
		}
	}

	@Override
	public String exportData(DataExportParameter param) {
		BlockingQueue<Map<String, Object>> queue = new LinkedBlockingQueue<>(MAX_QUEUE_LENGTH);

		// 抓取数据的线程
		DataQuery dataFetcher = new DataQuery(queue, param);
		Thread producer = new Thread(dataFetcher);

		// 写文件线程
		DataWriter dataWriter = new DataWriter(queue, param, producer);
		Thread customer = new Thread(dataWriter);

		System.out.println("\n导出任务开始……");
		producer.start();
		customer.start();

		while (!producer.getState().equals(State.TERMINATED) || !customer.getState().equals(State.TERMINATED)) {
			Thread.yield();
		}

		System.out.println("导出任务结束！");
		if (dataFetcher.getTaskStatus().isSuccess() && dataWriter.getTaskStatus().isSuccess()) {
			return "导出成功: " + dataWriter.getTaskStatus().getMsg();
		} else {
			StringBuilder errorMsg = new StringBuilder();
			if (!dataFetcher.getTaskStatus().isSuccess()) {
				errorMsg.append(dataFetcher.getTaskStatus().getMsg()).append("\n");
			}
			if (!dataWriter.getTaskStatus().isSuccess()) {
				errorMsg.append(dataWriter.getTaskStatus().getMsg());
			}
			return errorMsg.toString();
		}
	}

}
