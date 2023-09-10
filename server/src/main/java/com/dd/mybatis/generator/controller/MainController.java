package com.dd.mybatis.generator.controller;

import com.dd.mybatis.generator.bridge.MybatisGeneratorBridge;
import com.dd.mybatis.generator.common.CommonResponse;
import com.dd.mybatis.generator.exception.CheckException;
import com.dd.mybatis.generator.model.Connection;
import com.dd.mybatis.generator.model.GeneratorConfig;
import com.dd.mybatis.generator.service.ConnectionService;
import com.dd.mybatis.generator.util.DbUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/list/table")
    public CommonResponse<List<String>> listTable(Integer id) {
        try {
            Connection connection = connectionService.get(id);
            return CommonResponse.success(DbUtils.getTableNames(connection));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/generate")
    public CommonResponse<Void> generate(@RequestBody GeneratorConfig generatorConfig) {
        try {
            checkConfig(generatorConfig);
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

    private void checkConfig(GeneratorConfig generatorConfig) {
        if (generatorConfig.getConnectionId() == null) {
            throw new CheckException("请选择数据库表");
        }
        if (StringUtils.isEmpty(generatorConfig.getTable())) {
            throw new CheckException("请选择数据库表");
        }
        if (StringUtils.isEmpty(generatorConfig.getBean())) {
            throw new CheckException("请输入实体类名");
        }
        if (StringUtils.isEmpty(generatorConfig.getPath())) {
            throw new CheckException("请输入项目所在路径");
        }
        if (StringUtils.isEmpty(generatorConfig.getBeanPackage())) {
            throw new CheckException("请输入实体类包名");
        }
        if (StringUtils.isEmpty(generatorConfig.getMapperDir())) {
            throw new CheckException("请输入实体类存放目录");
        }
        if (StringUtils.isEmpty(generatorConfig.getMapperPackage())) {
            throw new CheckException("请输入Mapper类包名");
        }
        if (StringUtils.isEmpty(generatorConfig.getMapperDir())) {
            throw new CheckException("请输入Mapper类存放目录");
        }
        if (StringUtils.isEmpty(generatorConfig.getXmlPackage())) {
            throw new CheckException("请输入Mapper.xml包名");
        }
        if (StringUtils.isEmpty(generatorConfig.getXmlDir())) {
            throw new CheckException("请输入Mapper.xml存放目录");
        }
        if (StringUtils.isEmpty(generatorConfig.getEncoding())) {
            throw new CheckException("请输入编码");
        }
    }

    private void checkDirs(GeneratorConfig config) {
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
        }
    }



}
