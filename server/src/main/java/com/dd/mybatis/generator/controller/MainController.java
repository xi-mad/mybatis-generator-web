package com.dd.mybatis.generator.controller;

import com.dd.mybatis.generator.common.CommonResponse;
import com.dd.mybatis.generator.request.Connection;
import com.dd.mybatis.generator.util.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/main")
public class MainController {

    @PostMapping("/list/table")
    public CommonResponse<List<String>> listTable(@RequestBody Connection connection) {
        try {
            return CommonResponse.success(DbUtil.getTableNames(connection));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CommonResponse.fail(e.getMessage());
        }
    }



}
