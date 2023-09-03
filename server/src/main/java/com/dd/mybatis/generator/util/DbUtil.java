package com.dd.mybatis.generator.util;

import com.dd.mybatis.generator.exception.DbDriverLoadingException;
import com.dd.mybatis.generator.model.DatabaseConfig;
import com.dd.mybatis.generator.model.DbType;
import com.dd.mybatis.generator.request.Connection;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Owen on 6/12/16.
 */
public class DbUtil {

    private static final Logger _LOG = LoggerFactory.getLogger(DbUtil.class);
    private static final int DB_CONNECTION_TIMEOUTS_SECONDS = 1;

    private static final Map<DbType, Driver> drivers = new HashMap<>();

	private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private static volatile boolean portForwaring = false;
	private static final Map<Integer, Session> portForwardingSession = new ConcurrentHashMap<>();

    public static Session getSSHSession(DatabaseConfig databaseConfig) {
		if (StringUtils.isBlank(databaseConfig.getSshHost())
				|| StringUtils.isBlank(databaseConfig.getSshPort())
				|| StringUtils.isBlank(databaseConfig.getSshUser())
				|| (StringUtils.isBlank(databaseConfig.getPrivateKey()) && StringUtils.isBlank(databaseConfig.getSshPassword()))
		) {
			return null;
		}

		Session session = null;
		try {
			//Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Integer sshPort = NumberUtils.createInteger(databaseConfig.getSshPort());
			int port = sshPort == null ? 22 : sshPort;
			session = jsch.getSession(databaseConfig.getSshUser(), databaseConfig.getSshHost(), port);
			if (StringUtils.isNotBlank(databaseConfig.getPrivateKey())) {
				//使用秘钥方式认证
				jsch.addIdentity(databaseConfig.getPrivateKey(), StringUtils.defaultIfBlank(databaseConfig.getPrivateKeyPassword(), null));
			}else {
				session.setPassword(databaseConfig.getSshPassword());
			}
			session.setConfig(config);
		}catch (JSchException e) {
			//Ignore
		}
		return session;
	}

	public static void engagePortForwarding(Session sshSession, DatabaseConfig config) {
		if (sshSession != null) {
			AtomicInteger assinged_port = new AtomicInteger();
			Future<?> result = executorService.submit(() -> {
				try {
					Integer localPort = NumberUtils.createInteger(config.getLport());
					Integer RemotePort = NumberUtils.createInteger(config.getRport());
					int lport = localPort == null ? Integer.parseInt(config.getPort()) : localPort;
					int rport = RemotePort == null ? Integer.parseInt(config.getPort()) : RemotePort;
					Session session = portForwardingSession.get(lport);
					if (session != null && session.isConnected()) {
						String s = session.getPortForwardingL()[0];
						String[] split = StringUtils.split(s, ":");
						boolean portForwarding = String.format("%s:%s", split[0], split[1]).equals(lport + ":" + config.getHost());
						if (portForwarding) {
							return;
						}
					}
					sshSession.connect();
					assinged_port.set(sshSession.setPortForwardingL(lport, config.getHost(), rport));
					portForwardingSession.put(lport, sshSession);
					portForwaring = true;
					_LOG.info("portForwarding Enabled, {}", assinged_port);
				} catch (JSchException e) {
					_LOG.error("Connect Over SSH failed", e);
					if (e.getCause() != null && e.getCause().getMessage().equals("Address already in use: JVM_Bind")) {
						throw new RuntimeException("Address already in use: JVM_Bind");
					}
					throw new RuntimeException(e.getMessage());
				}
			});
			try {
				result.get(5, TimeUnit.SECONDS);
			}catch (Exception e) {
				shutdownPortForwarding(sshSession);
				if (e.getCause() instanceof RuntimeException) {
					throw (RuntimeException)e.getCause();
				}
				if (e instanceof TimeoutException) {
					throw new RuntimeException("OverSSH 连接超时：超过5秒");
				}

				_LOG.info("executorService isShutdown:{}", executorService.isShutdown());
				throw new RuntimeException("OverSSH 失败，请检查连接设置:" + e.getMessage());
			}
		}
	}

	public static void shutdownPortForwarding(Session session) {
		portForwaring = false;
		if (session != null && session.isConnected()) {
			session.disconnect();
			_LOG.info("portForwarding turn OFF");
		}
//		executorService.shutdown();
	}

    public static java.sql.Connection getConnection(Connection config) throws ClassNotFoundException, SQLException {
		DbType dbType = DbType.valueOf(config.getType());
		if (drivers.get(dbType) == null){
			loadDbDriver(dbType);
		}

		String url = getConnectionUrlWithSchema(config);
	    Properties props = new Properties();

	    props.setProperty("user", config.getUsername()); //$NON-NLS-1$
	    props.setProperty("password", config.getPassword()); //$NON-NLS-1$

		DriverManager.setLoginTimeout(DB_CONNECTION_TIMEOUTS_SECONDS);
	    java.sql.Connection connection = drivers.get(dbType).connect(url, props);
        _LOG.info("getConnection, connection url: {}", connection);
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

//    public static List<UITableColumnVO> getTableColumns(DatabaseConfig dbConfig, String tableName) throws Exception {
//        String url = getConnectionUrlWithSchema(dbConfig);
//        _LOG.info("getTableColumns, connection url: {}", url);
//		Session sshSession = getSSHSession(dbConfig);
//		engagePortForwarding(sshSession, dbConfig);
//		Connection conn = getConnection(dbConfig);
//		try {
//			DatabaseMetaData md = conn.getMetaData();
//			ResultSet rs = md.getColumns(dbConfig.getSchema(), null, tableName, null);
//			List<UITableColumnVO> columns = new ArrayList<>();
//			while (rs.next()) {
//				UITableColumnVO columnVO = new UITableColumnVO();
//				String columnName = rs.getString("COLUMN_NAME");
//				columnVO.setColumnName(columnName);
//				columnVO.setJdbcType(rs.getString("TYPE_NAME"));
//				columns.add(columnVO);
//			}
//			return columns;
//		} finally {
//			conn.close();
//			shutdownPortForwarding(sshSession);
//		}
//	}

    public static String getConnectionUrlWithSchema(Connection request) {
		DbType dbType = DbType.valueOf(request.getType());
		String connectionUrl = String.format(dbType.getConnectionUrlPattern(),
				request.getHost(), request.getPort(), request.getSchema(), request.getEncoding());
        _LOG.info("getConnectionUrlWithSchema, connection url: {}", connectionUrl);
        return connectionUrl;
    }

	/**
	 * 加载数据库驱动
	 * @param dbType 数据库类型
	 */
	private static void loadDbDriver(DbType dbType){
		List<String> driverJars = ConfigHelper.getAllJDBCDriverJarPaths();
		ClassLoader classloader = ClassloaderUtility.getCustomClassloader(driverJars);
		try {
			Class clazz = Class.forName(dbType.getDriverClass(), true, classloader);
			Driver driver = (Driver) clazz.newInstance();
			_LOG.info("load driver class: {}", driver);
			drivers.put(dbType, driver);
		} catch (Exception e) {
			_LOG.error("load driver error", e);
			throw new DbDriverLoadingException("找不到"+dbType.getConnectorJarFile()+"驱动");
		}
	}
}
