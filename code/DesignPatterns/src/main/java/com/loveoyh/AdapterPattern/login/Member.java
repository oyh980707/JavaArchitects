package com.loveoyh.AdapterPattern.login;

/**
 * @Created by oyh.Jerry to 2020/02/28 14:23
 */
public class Member {
	private String username;
	private String password;
	private String mid;
	private String info;
	
	public Member() {
	}
	
	@Override
	public String toString() {
		return "Member{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				", mid='" + mid + '\'' +
				", info='" + info + '\'' +
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
	
	public String getMid() {
		return mid;
	}
	
	public void setMid(String mid) {
		this.mid = mid;
	}
	
	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
}
