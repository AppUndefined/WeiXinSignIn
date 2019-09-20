package com.discern.discern.utils;

import org.hibernate.dialect.MySQL5Dialect;

/**
 * 设置创建表所使用的引擎和编码集
 */
public class DB_Charset extends MySQL5Dialect{
    @Override
    public String getTableTypeString() {
        return "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_general_ci AUTO_INCREMENT = 1";
    }
}
