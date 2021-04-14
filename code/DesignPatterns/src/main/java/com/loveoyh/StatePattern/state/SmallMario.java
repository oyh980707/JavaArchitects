package com.loveoyh.StatePattern.state;

import com.loveoyh.StatePattern.MarioStateMachine;

/**
 * 马里奥初始状态，变小状态
 * @Created by oyh.Jerry to 2021/04/14 14:30
 */
public class SmallMario implements IMario {
	
	private static volatile SmallMario INSTANCE;
	
	private SmallMario() {}
	public static SmallMario getInstance() {
		if(INSTANCE == null){
			synchronized (SmallMario.class){
				if(INSTANCE == null) {
					INSTANCE = new SmallMario();
				}
			}
		}
		return INSTANCE;
	}
	
	@Override
	public State getName() {
		return State.SMALL;
	}
	
	@Override
	public void obtainMushRoom(MarioStateMachine stateMachine) {
		stateMachine.setCurrentState(SuperMario.getInstance());
		stateMachine.setScore(stateMachine.getScore() + 100);
	}
	
	@Override
	public void obtainCape(MarioStateMachine stateMachine) {
		stateMachine.setCurrentState(CapeMario.getInstance());
		stateMachine.setScore(stateMachine.getScore() + 200);
	}
	
	@Override
	public void obtainFireFlower(MarioStateMachine stateMachine) {
		stateMachine.setCurrentState(FireMario.getInstance());
		stateMachine.setScore(stateMachine.getScore() + 300);
	}
	
}
