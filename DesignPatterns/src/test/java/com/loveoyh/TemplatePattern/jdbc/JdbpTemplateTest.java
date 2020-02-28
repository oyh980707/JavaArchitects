package com.loveoyh.TemplatePattern.jdbc;

import javax.servlet.http.HttpServlet;
import java.util.List;

/**
 * @Created by oyh.Jerry to 2020/02/28 05:46
 */
public class JdbpTemplateTest {
	public static void main(String[] args) throws Exception {
		MemberDao memberDao = new MemberDao(null);
		List<?> result = memberDao.selectAll();
		System.out.println(result);
	}
}
