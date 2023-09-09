package com.dd.mybatis.generator.util;

import com.dd.mybatis.generator.exception.CheckException;
import com.dd.mybatis.generator.model.Connection;
import com.dd.mybatis.generator.model.DbType;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.internal.util.ClassloaderUtility;

import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class DbUtils {

    private static final int DB_CONNECTION_TIMEOUTS_SECONDS = 1;

    private static final Map<DbType, Driver> drivers = new HashMap<>();

	public static java.sql.Connection getConnection(Connection config) throws ClassNotFoundException, SQLException {
		DbType dbType = DbType.valueOf(config.getType());
		if (drivers.get(dbType) == null){
			loadDbDriver(dbType);
		}

		String url = getConnectionUrlWithSchema(config);
	    Properties props = new Properties();

	    props.setProperty("user", config.getUsername());
	    props.setProperty("password", config.getPassword());

		DriverManager.setLoginTimeout(DB_CONNECTION_TIMEOUTS_SECONDS);
	    java.sql.Connection connection = drivers.get(dbType).connect(url, props);
        log.info("getConnection, connection url: {}", connection);
        return connection;
    }

    public static List<String> getTableNames(Connection config) throws Exception {
		try (java.sql.Connection connection = getConnection(config)) {
			List<String> tables = new ArrayList<>();
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs;
			if (DbType.valueOf(config.getType()) == DbType.SQL_Server) {
				String sql = "select name from sysobjects  where xtype='u' or xtype='v' order by name";
				rs = connection.createStatement().executeQuery(sql);
				while (rs.next()) {
					tables.add(rs.getString("name"));
				}
			} else if (DbType.valueOf(config.getType()) == DbType.Oracle) {
				rs = md.getTables(null, config.getUsername().toUpperCase(), null, new String[]{"TABLE", "VIEW"});
			} else if (DbType.valueOf(config.getType()) == DbType.Sqlite) {
				String sql = "Select name from sqlite_master;";
				rs = connection.createStatement().executeQuery(sql);
				while (rs.next()) {
					tables.add(rs.getString("name"));
				}
			} else {
				rs = md.getTables(config.getSchema(), null, "%", new String[]{"TABLE", "VIEW"});//针对 postgresql 的左侧数据表显示
			}
			while (rs.next()) {
				tables.add(rs.getString(3));
			}
			if (tables.size() > 1) {
				Collections.sort(tables);
			}
			return tables;
		}
	}

    public static String getConnectionUrlWithSchema(Connection request) {
		DbType dbType = DbType.valueOf(request.getType());
		String connectionUrl = String.format(dbType.getConnectionUrlPattern(),
				request.getHost(), request.getPort(), request.getSchema(), request.getEncoding());
		log.info("getConnectionUrlWithSchema, connection url: {}", connectionUrl);
        return connectionUrl;
    }

	/**
	 * 加载数据库驱动
	 * @param dbType 数据库类型
	 */
	private static void loadDbDriver(DbType dbType){
		List<String> driverJars = LibUtils.getAllJDBCDriverJarPaths();
		ClassLoader classloader = ClassloaderUtility.getCustomClassloader(driverJars);
		try {
			Class<?> clazz = Class.forName(dbType.getDriverClass(), true, classloader);
			Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();
			log.info("load driver class: {}", driver);
			drivers.put(dbType, driver);
		} catch (Exception e) {
			log.error("load driver error", e);
			throw new CheckException("找不到"+dbType.getConnectorJarFile()+"驱动");
		}
	}
}
