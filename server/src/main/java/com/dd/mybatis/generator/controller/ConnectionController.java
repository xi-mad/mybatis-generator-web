package com.dd.mybatis.generator.controller;

import com.dd.mybatis.generator.common.CommonResponse;
import com.dd.mybatis.generator.model.DatabaseConfig;
import com.dd.mybatis.generator.request.ConnectionRequest;
import com.dd.mybatis.generator.util.ConfigHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/connection")
public class ConnectionController {

    @PostMapping("/save")
    public CommonResponse<Void> saveConnection(ConnectionRequest request) {
        try {
//            ConfigHelper.saveDatabaseConfig(request);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/test")
    public CommonResponse<Void> testConnection(ConnectionRequest request) {
        try {
//            ConfigHelper.saveDatabaseConfig(request);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResponse.fail(e.getMessage());
        }
    }
}
