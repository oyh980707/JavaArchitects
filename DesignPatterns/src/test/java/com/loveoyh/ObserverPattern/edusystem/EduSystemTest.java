package com.loveoyh.ObserverPattern.edusystem;

/**
 * @Created by oyh.Jerry to 2020/03/01 01:22
 */
public class EduSystemTest {
	public static void main(String[] args) {
		EduSystem eduSystem = EduSystem.getInstance();
		Teacher oyh = new Teacher("oyh");
		Teacher hgr = new Teacher("hgr");
		
		eduSystem.addObserver(oyh);
		eduSystem.addObserver(hgr);
		
		Question question = new Question();
		question.setUsername("oyyx");
		question.setContent("你喜欢我吗？");
		
		eduSystem.publishQuestion(question);
	}
}
