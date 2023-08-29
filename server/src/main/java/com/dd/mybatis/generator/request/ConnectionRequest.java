package com.dd.mybatis.generator.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.awt.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ConnectionRequest {

    private Integer id;
    
    private String name;
    
    private String host;
    
    private String port;
    
    private String userName;
    
    private String password;
    
    private String schema;
    
    private String encoding;
    
    private String dbType;
}
