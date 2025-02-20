package com.loveoyh.TemplatePattern.jdbc;

/**
 * 实体类
 * @Created by oyh.Jerry to 2020/02/28 05:33
 */
public class Member {
	private String username;
	private String password;
	private String nickName;
	private int age;
	private String addr;
	
	public Member() {
	}
	
	public Member(String username, String password, String nickName, int age, String addr) {
		this.username = username;
		this.password = password;
		this.nickName = nickName;
		this.age = age;
		this.addr = addr;
	}
	
	@Override
	public String toString() {
		return "Member{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				", nickName='" + nickName + '\'' +
				", age=" + age +
				", addr='" + addr + '\'' +
				'}';
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public String getAddr() {
		return addr;
	}
	
	public void setAddr(String addr) {
		this.addr = addr;
	}
}
