package com.dd.mybatis.generator.service.impl;

import com.dd.mybatis.generator.exception.CheckException;
import com.dd.mybatis.generator.model.Connection;
import com.dd.mybatis.generator.service.ConnectionService;
import com.dd.mybatis.generator.util.DbUtils;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    Gson gson;

    @Override
    public List<Connection> list() {
        return jdbcTemplate.query("SELECT * FROM dbs", (rs, rowNum) -> {
            String value = rs.getString("value");
            Connection connection = gson.fromJson(value, Connection.class);
            connection.setId(rs.getInt("id"));
            return connection;
        });
    }

    @Override
    public synchronized void save(Connection conn) {
        if (StringUtils.isAnyEmpty(conn.getName(), conn.getType(), conn.getHost(), conn.getPort(), conn.getUsername(), conn.getSchema(), conn.getEncoding())) {
            throw new CheckException("密码以外其他字段必填");
        }
        String value = gson.toJson(conn);
        if (conn.getId() == null) {
            jdbcTemplate.update("INSERT INTO dbs (name, value) values(?, ?)", conn.getName(), value);
        } else {
            jdbcTemplate.update("UPDATE dbs SET name = ?, value = ? where id = ?", conn.getName(), value, conn.getId());
        }
    }

    @Override
    public void delete(Integer id) {
        if (id == null) {
            throw new CheckException("id不能为空");
        }
        jdbcTemplate.update("delete from dbs where id = ?", id);
    }

    @Override
    public void test(Connection conn) {
        if (StringUtils.isAnyEmpty(conn.getName(), conn.getType(), conn.getHost(), conn.getPort(), conn.getUsername(), conn.getSchema(), conn.getEncoding())) {
            throw new CheckException("密码以外其他字段必填");
        }
        try {
            DbUtils.getConnection(conn);
        } catch (ClassNotFoundException | SQLException e) {
            throw new CheckException(e);
        }
    }

    @Override
    public Connection get(Integer id) {
        if (id == null) {
            return null;
        }
        List<Connection> conn = jdbcTemplate.query("SELECT * FROM dbs where id = ?", new Object[]{id}, new int[]{Types.INTEGER}, (rs, rowNum) -> {
            String value = rs.getString("value");
            Connection connection = gson.fromJson(value, Connection.class);
            connection.setId(rs.getInt("id"));
            return connection;
        });
        return conn.isEmpty() ? null : conn.get(0);
    }
}
