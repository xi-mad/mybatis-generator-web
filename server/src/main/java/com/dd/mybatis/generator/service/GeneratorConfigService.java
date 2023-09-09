package com.dd.mybatis.generator.service;

import com.dd.mybatis.generator.model.GeneratorConfig;

import java.util.List;

public interface GeneratorConfigService {
    List<GeneratorConfig> list();

    void save(GeneratorConfig request);

    void delete(Integer id);

    GeneratorConfig get(Integer id);
}
