package com.loveoyh.StatePattern;

import com.loveoyh.StatePattern.state.IMario;
import com.loveoyh.StatePattern.state.SmallMario;

/**
 * 状态机，保存状态信息的
 */
public class MarioStateMachine {
	
	private int score;
	
	private IMario currentState;
	
	public MarioStateMachine() {
		this.score = 0;
		this.currentState = SmallMario.getInstance();
	}
	
	public void obtainMushRoom() {
		this.currentState.obtainMushRoom(this);
	}
	
	public void obtainCape() {
		this.currentState.obtainCape(this);
	}
	
	public void obtainFireFlower() {
		this.currentState.obtainFireFlower(this);
	}
	
	public void meetMonster() {
		this.currentState.meetMonster(this);
	}
	
	public int getScore() {
		return this.score;
	}
	
	public IMario getCurrentState() {
		return this.currentState;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public void setCurrentState(IMario mario) {
		this.currentState = mario;
	}
}