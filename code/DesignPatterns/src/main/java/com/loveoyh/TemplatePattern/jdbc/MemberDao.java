package com.loveoyh.TemplatePattern.jdbc;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

/**
 * 数据库操作类
 * @Created by oyh.Jerry to 2020/02/28 05:35
 */
public class MemberDao extends JdbcTemplate {
	
	public MemberDao(DataSource dataSource) {
		super(dataSource);
	}
	
	public List<?> selectAll() throws Exception {
		String sql = "select * from t_member";
		return executeQuery(sql, new RowMapper<Member>() {
			@Override
			public Member mapRow(ResultSet rs, int rowNum) throws Exception {
				Member member = new Member();
				member.setUsername(rs.getString("username"));
				member.setPassword(rs.getString("password"));
				member.setNickName(rs.getString("nickname"));
				member.setAge(rs.getInt("age"));
				member.setAddr(rs.getString("addr"));
				return member;
			}
		}, null);
	}
}
