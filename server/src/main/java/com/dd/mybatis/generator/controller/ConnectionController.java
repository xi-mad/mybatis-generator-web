package com.dd.mybatis.generator.controller;

import com.dd.mybatis.generator.common.CommonResponse;
import com.dd.mybatis.generator.model.Connection;
import com.dd.mybatis.generator.service.ConnectionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

    @Resource
    ConnectionService connectionService;

    @GetMapping("/list")
    public CommonResponse<List<Connection>> listConnection() {
        return CommonResponse.success(connectionService.list());
    }
    @PostMapping("/save")
    public CommonResponse<Void> saveConnection(@RequestBody Connection request) {
        connectionService.save(request);
        return CommonResponse.success();
    }

    @PostMapping("/delete")
    public CommonResponse<Void> deleteConnection(@RequestBody Connection request) {
        connectionService.delete(request.getId());
        return CommonResponse.success();
    }

    @PostMapping("/test")
    public CommonResponse<String> testConnection(@RequestBody Connection request) {
        connectionService.test(request);
        return CommonResponse.success("连接成功");
    }
}
