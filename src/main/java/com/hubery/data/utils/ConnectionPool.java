/**
 * 
 */
package com.hubery.data.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

/**
 * @author Hubery
 * @createDate 2016年11月23日
 */
public class ConnectionPool {
	private static BasicDataSource dataSource = null;

	public static void init() {
		if (dataSource != null) {
			try {
				dataSource.close();
			} catch (Exception e) {
			}
			dataSource = null;
		}

		try {
			Properties p = new Properties();
			InputStream inStream = ConnectionPool.class.getClassLoader().getResourceAsStream("db.properties");
			p.load(inStream);

			dataSource = (BasicDataSource) BasicDataSourceFactory.createDataSource(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized Connection getConnection() throws SQLException {
		if (dataSource == null) {
			init();
		}

		Connection conn = null;

		if (dataSource != null) {
			conn = dataSource.getConnection();
		}

		return conn;
	}
}
