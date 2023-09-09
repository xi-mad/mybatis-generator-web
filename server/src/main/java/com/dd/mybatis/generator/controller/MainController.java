package com.dd.mybatis.generator.controller;

import com.dd.mybatis.generator.bridge.MybatisGeneratorBridge;
import com.dd.mybatis.generator.common.CommonResponse;
import com.dd.mybatis.generator.model.GeneratorConfig;
import com.dd.mybatis.generator.model.Connection;
import com.dd.mybatis.generator.service.ConnectionService;
import com.dd.mybatis.generator.util.DbUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/main")
public class MainController {

    @Resource
    ConnectionService connectionService;

    @PostMapping("/list/table")
    public CommonResponse<List<String>> listTable(@RequestBody Connection connection) {
        try {
            return CommonResponse.success(DbUtils.getTableNames(connection));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/generate")
    public CommonResponse<Void> generate(@RequestBody GeneratorConfig generatorConfig) {
        try {
            Connection conn = connectionService.get(generatorConfig.getConnectionId());
            MybatisGeneratorBridge bridge = new MybatisGeneratorBridge();
            bridge.setGeneratorConfig(generatorConfig);
            bridge.setConnection(conn);
            checkDirs(generatorConfig);
//            bridge.setIgnoredColumns(ignoredColumns);
//            bridge.setColumnOverrides(columnOverrides);
            bridge.generate();
            return CommonResponse.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResponse.fail(e.getMessage());
        }
    }

    private boolean checkDirs(GeneratorConfig config) {
        List<String> dirs = new ArrayList<>();
        dirs.add(config.getPath());
        dirs.add(config.getPath().concat("/").concat(config.getBeanDir()));
        dirs.add(config.getPath().concat("/").concat(config.getMapperDir()));
        dirs.add(config.getPath().concat("/").concat(config.getXmlDir()));
        boolean haveNotExistFolder = false;
        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.exists()) {
                haveNotExistFolder = true;
            }
        }
        if (haveNotExistFolder) {
            for (String dir : dirs) {
                try {
                    FileUtils.forceMkdir(new File(dir));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        return true;
    }



}
