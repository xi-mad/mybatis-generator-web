package com.dd.mybatis.generator.util;

import com.dd.mybatis.generator.model.DbType;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LibUtils {

    public static String findConnectorLibPath(String dbType) {
        DbType type = DbType.valueOf(dbType);
        try {
            File file = new File(LibUtils.class.getResource("/lib/").toURI().getRawPath() + type.getConnectorJarFile());
            return URLDecoder.decode(file.getPath(), StandardCharsets.UTF_8.displayName());
        } catch (Exception e) {
            throw new RuntimeException("找不到驱动文件，请联系开发者");
        }
    }

    public static List<String> getAllJDBCDriverJarPaths() {
        List<String> jarFilePathList = new ArrayList<>();
        try {
            URL url = LibUtils.class.getResource("/lib");
            File file = new File(url.toURI());
            File[] jarFiles = file.listFiles();
            if (jarFiles != null) {
                for (File jarFile : jarFiles) {
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
