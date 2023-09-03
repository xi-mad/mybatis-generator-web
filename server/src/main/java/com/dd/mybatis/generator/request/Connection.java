package com.dd.mybatis.generator.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Connection {

    private Integer id;
    private String name;
    private String type;
    private String host;
    private String port;
    private String username;
    private String password;
    private String schema;
    private String encoding;
}
