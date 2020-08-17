package com.loveoyh.ObserverPattern.edusystem;

import java.util.Observable;
import java.util.Observer;

/**
 * 观察者
 * @Created by oyh.Jerry to 2020/03/01 01:17
 */
public class Teacher implements Observer {
	private String name;
	
	public Teacher(String name) {
		this.name = name;
	}
	
	
	@Override
	public void update(Observable o, Object arg) {
		EduSystem eduSystem = (EduSystem) o;
		Question question = (Question) arg;
		System.out.println("===========================");
		System.out.println(name+"老师，您收到一个来自"+eduSystem.getName()+"的提问："+question.getUsername()
		+"提问:"+question.getContent());
	}
}
