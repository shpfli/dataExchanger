/**
 * 
 */
package com.hubery.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hubery.data.bean.DataExportParameter;
import com.hubery.data.bean.TaskStatus;
import com.hubery.data.utils.ConnectionPool;

/**
 * @author Hubery
 *
 */
public class DataQuery implements Runnable {
	private BlockingQueue<Map<String, Object>> queue;
	private DataExportParameter param;
	private TaskStatus taskStatus;

	public DataQuery(BlockingQueue<Map<String, Object>> queue, DataExportParameter param) {
		super();
		this.queue = queue;
		this.param = param;
		this.taskStatus = new TaskStatus();
	}

	@Override
	public void run() {
		String sql = "select " + this.param.getExportTableColumns() + " from " + param.getExportTable();
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet result = null;
		try {
			conn = ConnectionPool.getConnection();
			pstmt = conn.prepareStatement(sql);
			result = pstmt.executeQuery();
			while (result.next()) {
				ResultSetMetaData resultSetMetaData = result.getMetaData();
				Map<String, Object> item = new HashMap<String, Object>();
				for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
					String name = resultSetMetaData.getColumnName(i);
					Object val = result.getObject(i);
					item.put(name.toUpperCase(), val);
				}
				// 将数据加入队列
				while (!queue.offer(item, 1, TimeUnit.MINUTES)) {
					// 如果队列已满，则等待
					System.out.println("缓冲队列已满，等待中……");
				}
			}
			this.taskStatus.setSuccess(true);
		} catch (SQLException e) {
			e.printStackTrace();
			this.taskStatus.setSuccess(false);
			this.taskStatus.setMsg(e.getMessage());
		} catch (Exception e) {
			this.taskStatus.setSuccess(false);
			this.taskStatus.setMsg("查询数据时，出现异常！");
		} finally {
			try {
				if (result != null) {
					result.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * @return the taskStatus
	 */
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

}
