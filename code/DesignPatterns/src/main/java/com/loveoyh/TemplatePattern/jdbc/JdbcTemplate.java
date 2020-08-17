package com.loveoyh.TemplatePattern.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象模板类
 * @Created by oyh.Jerry to 2020/02/28 05:03
 */
public abstract class JdbcTemplate {
	private DataSource dataSource;
	
	public JdbcTemplate(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	public List<?> executeQuery(String sql,RowMapper<?> rowMapper,Object[] vlaues) throws Exception {
		/*
		 * 1.创建连接
		 * 2.创建语句集
		 * 3.执行语句集
		 * 4.处理结果集
		 * 5.关闭结果集
		 * 6.关闭语句集
		 * 7.关闭连接
		 */
		Connection conn = this.getConnection();
		PreparedStatement ps = this.createPreparedStatement(conn,sql);
		ResultSet rs = this.executeQuery(ps,vlaues);
		List<?> result = this.parseResultSet(rs,rowMapper);
		this.closeResultSet(rs);
		this.closeStatement(ps);
		this.closeConnection(conn);
		return result;
	}
	/*
	 * 获取数据库连接
	 */
	protected Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	/*
	 * 获取预处理语句集
	 */
	protected PreparedStatement createPreparedStatement(Connection conn, String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}
	/*
	 * 执行语句集
	 */
	protected ResultSet executeQuery(PreparedStatement ps, Object[] values) throws SQLException {
		for(int i=0;i<values.length;i++){
			ps.setObject(i,values[i]);
		}
		return ps.executeQuery();
	}
	/*
	 * 处理结果集
	 */
	protected List<?> parseResultSet(ResultSet rs, RowMapper<?> rowMapper) throws Exception {
		List<Object> result = new ArrayList<>();
		int rows = 1;
		while(rs.next()){
			result.add(rowMapper.mapRow(rs,rows++));
		}
		return result;
	}
	/*
	 * 关闭结果集
	 */
	protected void closeResultSet(ResultSet rs) throws SQLException {
		rs.close();
	}
	/*
	 * 关闭语句集
	 */
	
	protected void closeStatement(PreparedStatement ps) throws SQLException {
		ps.close();
	}
	
	protected void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}
	
}
