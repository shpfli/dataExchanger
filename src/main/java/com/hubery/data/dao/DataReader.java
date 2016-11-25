/**
 * 
 */
package com.hubery.data.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.hubery.data.bean.DataFile;
import com.hubery.data.bean.IndexFile;
import com.hubery.data.bean.TaskStatus;
import com.hubery.data.utils.IOUtil;
import com.hubery.data.utils.StrEscapeUtil;

/**
 * @author Hubery
 * @createDate 2016年11月24日
 */
public class DataReader implements Runnable {
	private BlockingQueue<Map<String, Object>> queue;
	private IndexFile indexFile;
	private TaskStatus taskStatus;

	public DataReader(BlockingQueue<Map<String, Object>> queue, IndexFile indexFile) {
		super();
		this.queue = queue;
		this.indexFile = indexFile;
		this.taskStatus = new TaskStatus();
	}

	@Override
	public void run() {
		FileReader fr = null;
		BufferedReader bufferedReader = null;
		try {
			for (DataFile dataFile : this.indexFile.getDataFiles()) {
				fr = new FileReader(dataFile.getFile());
				bufferedReader = new BufferedReader(fr);

				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					Map<String, Object> map = line2Map(line, indexFile);
					this.queue.put(map);
				}
				IOUtil.closeQuietly(bufferedReader, fr);
			}
			this.taskStatus.setSuccess(true);
			System.out.println("数据文件读取完毕！");
		} catch (Exception e) {
			e.printStackTrace();
			this.taskStatus.setSuccess(false);
			this.taskStatus.setMsg("数据文件解析失败！");
		} finally {
			IOUtil.closeQuietly(bufferedReader, fr);
		}
	}

	/**
	 * 解析一行数据
	 * 
	 * @param line
	 * @param indexFile
	 * @return
	 * @throws IOException
	 */
	private Map<String, Object> line2Map(String line, IndexFile indexFile) throws IOException {
		Map<String, Object> map = new HashMap<>();
		String separator = indexFile.getSeparator();
		if ("|".equals(separator.trim())) {
			separator = "\\|";
		}

		String[] columns = indexFile.getColumns();
		String[] types = indexFile.getColumnTypes();
		String[] data = line.split(separator);
		if (data.length != columns.length || data.length != types.length) {
			throw new IOException("字段头和数据不一致。数据文件解析失败！");
		}

		for (int i = 0; i < data.length; i++) {
			String key = columns[i].trim();
			String item = data[i];
			String type = types[i];
			Object val = null;
			if ("NULL".equals(item.toUpperCase())) {
				map.put(key, null);
			} else {
				// 反转义
				item = StrEscapeUtil.unescape(item);

				if ("java.sql.Timestamp".equals(type)) {
					val = Timestamp.valueOf(item);
				} else if ("java.math.BigDecimal".equals(type)) {
					val = BigDecimal.valueOf(Double.valueOf(item));
				} else if ("java.sql.Date".equals(type)) {
					val = Date.valueOf(item);
				} else {
					val = item;
				}
			}

			map.put(key, val);
		}

		return map;
	}

	/**
	 * @return the taskStatus
	 */
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}
}
