package com.loveoyh.ObserverPattern.edusystem;

import java.util.Observable;

/**
 * @Created by oyh.Jerry to 2020/03/01 00:26
 */
public class EduSystem extends Observable {
	private String name = "教务系统";
	private static EduSystem eduSystem = null;
	private EduSystem(){};
	
	public static EduSystem getInstance(){
		if(eduSystem == null){
			eduSystem = new EduSystem();
		}
		return eduSystem;
	}
	
	public void publishQuestion(Question question){
		System.out.println(question.getUsername()+"在"+this.name+"上次提交了一个问题");
		setChanged();
		notifyObservers(question);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
