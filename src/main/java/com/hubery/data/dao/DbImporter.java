/**
 * 
 */
package com.hubery.data.dao;

import java.lang.Thread.State;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hubery.data.bean.IndexFile;
import com.hubery.data.bean.TaskStatus;
import com.hubery.data.utils.ConnectionPool;

/**
 * @author Hubery
 * @createDate 2016年11月24日
 */
public class DbImporter implements Runnable {
	private static final int BATCH_SIZE = 1000;
	private BlockingQueue<Map<String, Object>> queue;
	private IndexFile indexFile;
	private Thread producer;// 生产者
	private String tableName;// 表名

	private TaskStatus taskStatus;

	public DbImporter(BlockingQueue<Map<String, Object>> queue, IndexFile indexFile, String tableName,
			Thread producer) {
		super();
		this.queue = queue;
		this.indexFile = indexFile;
		this.producer = producer;
		this.tableName = tableName;
		this.taskStatus = new TaskStatus();
	}

	@Override
	public void run() {
		String[] columns = indexFile.getColumns();
		int i = 0;

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			String sql = generateSql();
			connection = ConnectionPool.getConnection();
			ps = connection.prepareStatement(sql);

			while (!queue.isEmpty() || !producer.getState().equals(State.TERMINATED)) {
				Map<String, Object> map = queue.poll(1, TimeUnit.SECONDS);
				if (map != null) {
					for (int j = 0; j < columns.length; j++) {
						ps.setObject(j + 1, map.get(columns[j]));
					}
					ps.addBatch();
					if (++i % BATCH_SIZE == 0) {
						ps.executeBatch();
					}
				}
			}

			ps.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();
			taskStatus.setSuccess(false);
			taskStatus.setMsg(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			taskStatus.setSuccess(false);
			taskStatus.setMsg("插入数据库时发生异常！");
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		taskStatus.setSuccess(true);
	}

	private String generateSql() {
		StringBuilder sb = new StringBuilder(" insert into "), sb_values = new StringBuilder(" values(");
		sb.append(tableName).append(" (");
		for (String column : indexFile.getColumns()) {
			sb.append(column).append(",");
			sb_values.append("?,");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb_values.deleteCharAt(sb_values.length() - 1);

		sb.append(") ").append(sb_values).append(") ");
		return sb.toString();
	}

	/**
	 * @return the taskStatus
	 */
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}
}
