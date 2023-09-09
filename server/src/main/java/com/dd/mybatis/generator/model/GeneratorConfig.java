package com.dd.mybatis.generator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * GeneratorConfig is the Config of mybatis generator config exclude database
 * config
 *
 * Created by Owen on 6/16/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratorConfig {

	private Integer id;
	private String name;
	private Integer connectionId;
	private String table;
	private String bean;
	private String primaryKey;
	private String path;
	private String beanPackage;
	private String beanDir;
	private String mapperPackage;
	private String mapperDir;
	private String interfaceName;
	private String xmlPackage;
	private String xmlDir;
	private String encoding;
	private boolean useExample;
	private boolean offsetLimit;
	private boolean comment;
	private boolean overrideXML;
	private boolean useLombokPlugin;
	private boolean needToStringHashcodeEquals;
	private boolean useSchemaPrefix;
	private boolean forUpdate;
	private boolean annotationDAO;
	private boolean useDAOExtendStyle;
	private boolean jsr310Support;
	private boolean annotation;
	private boolean useActualColumnNames;
	private boolean useTableNameAlias;




}
