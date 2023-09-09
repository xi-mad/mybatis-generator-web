package com.dd.mybatis.generator.controller;

import com.dd.mybatis.generator.common.CommonResponse;
import com.dd.mybatis.generator.model.GeneratorConfig;
import com.dd.mybatis.generator.service.GeneratorConfigService;
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
@RequestMapping("/api/generator/config")
public class GeneratorConfigController {

    @Resource
    GeneratorConfigService generatorConfigService;

    @GetMapping("/list")
    public CommonResponse<List<GeneratorConfig>> listGeneratorConfig() {
        return CommonResponse.success(generatorConfigService.list());
    }
    @PostMapping("/save")
    public CommonResponse<Void> saveGeneratorConfig(@RequestBody GeneratorConfig request) {
        generatorConfigService.save(request);
        return CommonResponse.success();
    }

    @PostMapping("/delete")
    public CommonResponse<Void> deleteGeneratorConfig(@RequestBody GeneratorConfig request) {
        generatorConfigService.delete(request.getId());
        return CommonResponse.success();
    }

}
