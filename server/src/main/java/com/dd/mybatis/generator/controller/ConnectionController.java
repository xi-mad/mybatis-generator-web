package com.dd.mybatis.generator.controller;

import com.dd.mybatis.generator.common.CommonResponse;
import com.dd.mybatis.generator.request.Connection;
import com.dd.mybatis.generator.util.ConfigHelper;
import com.dd.mybatis.generator.util.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/connection")
public class ConnectionController {

    @GetMapping("/list")
    public CommonResponse<List<Connection>> listConnection() {
        try {
            return CommonResponse.success(ConfigHelper.loadDatabaseConfig());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResponse.fail(e.getMessage());
        }
    }
    @PostMapping("/save")
    public CommonResponse<Void> saveConnection(@RequestBody Connection request) {
        try {
            if (StringUtils.isAnyEmpty(request.getName(), request.getType(), request.getHost(), request.getPort(), request.getUsername(), request.getSchema(), request.getEncoding())) {
                return CommonResponse.fail("密码以外其他字段必填");
            }
            ConfigHelper.saveDatabaseConfig(request);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/test")
    public CommonResponse<String> testConnection(@RequestBody Connection request) {
        try {
            if (StringUtils.isAnyEmpty(request.getName(), request.getType(), request.getHost(), request.getPort(), request.getUsername(), request.getSchema(), request.getEncoding())) {
                return CommonResponse.fail("密码以外其他字段必填");
            }
            DbUtil.getConnection(request);
            return CommonResponse.success("连接成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResponse.fail(e.getMessage());
        }
    }
}
