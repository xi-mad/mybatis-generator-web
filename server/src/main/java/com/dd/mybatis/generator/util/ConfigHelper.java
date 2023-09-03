package com.dd.mybatis.generator.util;

import com.dd.mybatis.generator.model.DbType;
import com.dd.mybatis.generator.model.GeneratorConfig;
import com.dd.mybatis.generator.request.Connection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * XML based config file help class
 * <p>
 * Created by Owen on 6/16/16.
 */
public class ConfigHelper {

    private static final Logger _LOG = LoggerFactory.getLogger(ConfigHelper.class);
    private static final String BASE_DIR = "config";
    private static final String CONFIG_FILE = "/sqlite3.db";
    private static final Gson gson = new GsonBuilder().create();

    public static void createEmptyFiles() throws Exception {
        File file = new File(BASE_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        File uiConfigFile = new File(BASE_DIR + CONFIG_FILE);
        if (!uiConfigFile.exists()) {
            uiConfigFile.createNewFile();
        }
    }

    public static List<Connection> loadDatabaseConfig() throws Exception {
        try (java.sql.Connection conn = ConnectionManager.getConnection();
             Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery("SELECT * FROM dbs")) {
            List<Connection> configs = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String value = rs.getString("value");
                Connection connection = gson.fromJson(value, Connection.class);
                connection.setId(id);
                configs.add(connection);
            }
            return configs;
        }
    }

    public static void saveDatabaseConfig(Connection request) throws Exception {
        try (java.sql.Connection conn = ConnectionManager.getConnection();
             Statement stat = conn.createStatement()) {
            String jsonStr = gson.toJson(request);
            String sql;
            if (request.getId() == null) {
                sql = String.format("INSERT INTO dbs (name, value) values('%s', '%s')", request.getName(), jsonStr);
            } else {
                sql = String.format("UPDATE dbs SET name = '%s', value = '%s' where id = %d", request.getName(), jsonStr, request.getId());
            }
            stat.executeUpdate(sql);
        }
    }

    public static void deleteDatabaseConfig(Integer id) throws Exception {
        if (id == null) {
            return;
        }
        try (java.sql.Connection conn = ConnectionManager.getConnection();
             Statement stat = conn.createStatement()) {
            stat.executeUpdate(String.format("delete from dbs where id=%d", id));
        }
    }

    public static void saveGeneratorConfig(GeneratorConfig generatorConfig) throws Exception {
        try (java.sql.Connection conn = ConnectionManager.getConnection();
             Statement stat = conn.createStatement()) {
            String jsonStr = gson.toJson(generatorConfig);
            stat.executeUpdate(String.format("INSERT INTO generator_config values('%s', '%s')", generatorConfig.getName(), jsonStr));
        }
    }

    public static GeneratorConfig loadGeneratorConfig(String name) throws Exception {
        try (java.sql.Connection conn = ConnectionManager.getConnection();
             Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(String.format("SELECT * FROM generator_config where name='%s'", name));) {
            GeneratorConfig generatorConfig = null;
            if (rs.next()) {
                String value = rs.getString("value");
                generatorConfig = gson.fromJson(value, GeneratorConfig.class);
            }
            return generatorConfig;
        }
    }

    public static List<GeneratorConfig> loadGeneratorConfigs() throws Exception {
        try (java.sql.Connection conn = ConnectionManager.getConnection();
             Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery("SELECT * FROM generator_config")) {
            List<GeneratorConfig> configs = new ArrayList<>();
            while (rs.next()) {
                String value = rs.getString("value");
                configs.add(gson.fromJson(value, GeneratorConfig.class));
            }
            return configs;
        }
    }

    public static int deleteGeneratorConfig(String name) throws Exception {
        try (java.sql.Connection conn = ConnectionManager.getConnection();
             Statement stat = conn.createStatement();) {
            return stat.executeUpdate(String.format("DELETE FROM generator_config where name='%s'", name));
        }
    }

    public static String findConnectorLibPath(String dbType) {
        DbType type = DbType.valueOf(dbType);
        URL resource = Thread.currentThread().getContextClassLoader().getResource("logback.xml");
        _LOG.info("jar resource: {}", resource);
        if (resource != null) {
            try {
                File file = new File(resource.toURI().getRawPath() + "/../lib/" + type.getConnectorJarFile());
                return URLDecoder.decode(file.getCanonicalPath(), StandardCharsets.UTF_8.displayName());
            } catch (Exception e) {
                throw new RuntimeException("找不到驱动文件，请联系开发者");
            }
        } else {
            throw new RuntimeException("lib can't find");
        }
    }

    public static List<String> getAllJDBCDriverJarPaths() {
        List<String> jarFilePathList = new ArrayList<>();
        try {
            URL url = ConfigHelper.class.getResource("/lib");
            File file = new File(url.toURI());
            _LOG.info("jar lib path: {}", file.getCanonicalPath());
            File[] jarFiles = file.listFiles();
            if (jarFiles != null) {
                for (File jarFile : jarFiles) {
                    _LOG.info("jar file: {}", jarFile.getAbsolutePath());
                    if (jarFile.isFile() && jarFile.getAbsolutePath().endsWith(".jar")) {
                        jarFilePathList.add(jarFile.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("找不到驱动文件，请联系开发者");
        }
        return jarFilePathList;
    }


}
