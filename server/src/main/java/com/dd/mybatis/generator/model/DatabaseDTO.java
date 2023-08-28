package com.dd.mybatis.generator.model;

import lombok.Data;
import lombok.ToString;

@Data
public class DatabaseDTO {

	private String name;
	private int value;
	private String driverClass;

	@Override
	public String toString() {
		return name;
	}

}
