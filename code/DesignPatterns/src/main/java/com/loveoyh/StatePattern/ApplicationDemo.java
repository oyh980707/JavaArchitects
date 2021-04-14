package com.loveoyh.StatePattern;

import com.loveoyh.StatePattern.state.IMario;

public class ApplicationDemo {
	
	public static void main(String[] args) {
		MarioStateMachine mario = new MarioStateMachine();
		mario.obtainMushRoom();
		int score = mario.getScore();
		IMario state = mario.getCurrentState();
		System.out.println("mario score: " + score + "; state: " + state.getName());
	}
	
}