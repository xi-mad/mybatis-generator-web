package com.dd.mybatis.generator.service;

import com.dd.mybatis.generator.model.Connection;

import java.util.List;

public interface ConnectionService {

    List<Connection> list();

    void save(Connection conn);

    void delete(Integer id);

    void test(Connection conn);

    Connection get(Integer id);

}
