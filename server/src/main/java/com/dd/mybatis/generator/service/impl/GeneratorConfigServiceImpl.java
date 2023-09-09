package com.dd.mybatis.generator.service.impl;

import com.dd.mybatis.generator.exception.CheckException;
import com.dd.mybatis.generator.model.GeneratorConfig;
import com.dd.mybatis.generator.service.GeneratorConfigService;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneratorConfigServiceImpl implements GeneratorConfigService {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    Gson gson;

    @Override
    public List<GeneratorConfig> list() {
        return jdbcTemplate.query("SELECT * FROM generator_config", (rs, rowNum) -> {
            String value = rs.getString("value");
            GeneratorConfig connection = gson.fromJson(value, GeneratorConfig.class);
            connection.setId(rs.getInt("id"));
            return connection;
        });
    }

    @Override
    public void save(GeneratorConfig request) {
        String value = gson.toJson(request);
        if (request.getId() == null) {
            jdbcTemplate.update("INSERT INTO generator_config (name, value) values(?, ?)", request.getName(), value);
        } else {
            jdbcTemplate.update("UPDATE generator_config SET name = ?, value = ? where id = ?", request.getName(), value, request.getId());
        }
    }

    @Override
    public void delete(Integer id) {
        if (id == null) {
            throw new CheckException("id不能为空");
        }
        jdbcTemplate.update("DELETE FROM generator_config where id = ?", id);
    }

    @Override
    public GeneratorConfig get(Integer id) {
        if (id == null) {
            throw new CheckException("id不能为空");
        }
        List<GeneratorConfig> query = jdbcTemplate.query("SELECT * FROM generator_config", (rs, rowNum) -> {
            String value = rs.getString("value");
            GeneratorConfig connection = gson.fromJson(value, GeneratorConfig.class);
            connection.setId(rs.getInt("id"));
            return connection;
        });
        return query.isEmpty() ? null : query.get(0);
    }
}
