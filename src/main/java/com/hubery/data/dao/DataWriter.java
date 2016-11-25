/**
 * 
 */
package com.hubery.data.dao;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.State;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hubery.data.bean.DataExportParameter;
import com.hubery.data.bean.DataFile;
import com.hubery.data.bean.IndexFile;
import com.hubery.data.bean.TaskStatus;
import com.hubery.data.utils.FileUtil;
import com.hubery.data.utils.StrEscapeUtil;
import com.hubery.data.utils.ZipUtil;

/**
 * @author Hubery
 *
 */
public class DataWriter implements Runnable {
	private TaskStatus taskStatus;
	private BlockingQueue<Map<String, Object>> queue;
	private DataExportParameter param;
	private Thread produceThread;// 生产者线程，用于监控停止生产事件
	private int index = 1;// 数据文件起始编号

	private IndexFile indexFile; // 索引文件
	private DataFile dataFile; // 当前正在写入的数据文件
	private File dir; // 导出目录

	private List<String> columnTypes;// 用于记录每一列的类型

	public DataWriter(BlockingQueue<Map<String, Object>> queue, DataExportParameter param, Thread produceThread) {
		super();
		this.taskStatus = new TaskStatus();
		this.queue = queue;
		this.param = param;
		this.produceThread = produceThread;

		File dir = new File(param.getExportDir());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		this.dir = new File(dir, generateDirName(param.getExportTable()));
		this.dir.mkdir();

		IndexFile indexFile = new IndexFile();
		indexFile.setSeparator(param.getSeparator());
		this.indexFile = indexFile;

		columnTypes = new ArrayList<>();
	}

	@Override
	public void run() {
		try {
			while (!this.queue.isEmpty() || !produceThread.getState().equals(State.TERMINATED)) {
				Map<String, Object> map = this.queue.poll(1000, TimeUnit.MILLISECONDS);
				// System.out.println(map);
				if (map != null) {
					this.writeToFile(this.toLine(map));
				} else {
					// System.out.println("缓冲队列已空，等待新的数据中……");
				}
			}
			this.dataFile.close();
			this.indexFile.addDataFile(dataFile);
			this.indexFile.setColumns(this.param.getColumns());
			this.indexFile.setColumnTypes(columnTypes.toArray(new String[0]));
			File file = new File(dir, "index.txt");
			this.indexFile.writeToFile(file);

			// 如果需要压缩
			if (param.isDoCompress()) {
				File zipFile = new File(dir.getParent(), dir.getName() + ".zip");
				ZipUtil.zip(zipFile, dir);
				FileUtil.delete(dir);
				this.taskStatus.setMsg(zipFile.getPath());
			} else {
				this.taskStatus.setMsg(dir.getPath());
			}
			this.taskStatus.setSuccess(true);
		} catch (Exception e) {
			this.taskStatus.setSuccess(false);
			this.taskStatus.setMsg(e.getMessage() == null ? "文件生成时出现异常！" : e.getMessage());
		}
	}

	private DataFile createDataFile() throws IOException {
		File file = new File(this.dir, "data_" + this.index++ + ".txt");
		file.createNewFile();
		DataFile dataFile = new DataFile();
		dataFile.setFile(file);
		return dataFile;
	}

	private void writeToFile(String line) throws IOException {
		if (this.dataFile == null) {
			this.dataFile = this.createDataFile();
		}

		// 如果达到临界值，则创建新的文件
		if (this.dataFile.getFileSize() > (this.param.getMaxSize() * 0.9999 * 1048576)) {
			this.dataFile.close();
			this.indexFile.addDataFile(this.dataFile);
			this.dataFile = this.createDataFile();
		}

		this.dataFile.writeLine(line);
	}

	private String toLine(Map<String, Object> map) {
		StringBuilder strBuilder = new StringBuilder();

		String[] columns = this.param.getColumns();
		if (columns == null) {
			columns = map.keySet().toArray(new String[0]);
			this.param.setColumns(columns);
		}

		for (int i = 0; i < columns.length; i++) {
			String name = columns[i].trim().toUpperCase();
			Object val = map.get(name);

			if (columnTypes.size() != columns.length) {
				// 初次
				if (val == null) {
					columnTypes.add(null);
				} else {
					columnTypes.add(val.getClass().getName());
				}
			} else {
				if (columnTypes.get(i) == null && val != null) {
					// 如果还不知道该列的数据类型
					columnTypes.set(i, val.getClass().getName());
				}
			}

			strBuilder.append(this.val2String(val)).append(this.param.getSeparator());
		}

		if (columns.length > 0) {
			strBuilder.deleteCharAt(strBuilder.length() - 1);
		}

		return strBuilder.toString();
	}

	/**
	 * 其他类型的数据转化为String
	 * 
	 * @param val
	 * @return
	 */
	private String val2String(Object val) {
		if (val == null) {
			return "NULL";
		}

		return StrEscapeUtil.escape(val.toString());
	}

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 生成导出的文件所在文件夹名称
	 * 
	 * @param tablename
	 * @return
	 */
	private static String generateDirName(String tablename) {
		StringBuilder sb = new StringBuilder("exchange-");
		sb.append(tablename.toLowerCase()).append("-");
		sb.append(dateFormat.format(new Date()));

		return sb.toString();
	}

	/**
	 * @return the taskStatus
	 */
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

}
