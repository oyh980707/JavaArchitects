package com.loveoyh.TemplatePattern.jdbc;

import java.sql.ResultSet;

/**
 * RowMapper可以将数据中的每一行数据封装成用户定义的类
 * @Created by oyh.Jerry to 2020/02/28 05:04
 */
public interface RowMapper<T> {

	T mapRow(ResultSet rs,int rowNum) throws Exception;

}
