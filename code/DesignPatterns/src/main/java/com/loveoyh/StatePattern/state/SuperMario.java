package com.loveoyh.StatePattern.state;

import com.loveoyh.StatePattern.MarioStateMachine;

/**
 * 马里奥变大状态，从小状态吃了蘑菇
 * @Created by oyh.Jerry to 2021/04/14 14:35
 */
public class SuperMario implements IMario {
	private static volatile SuperMario INSTANCE;
	
	private SuperMario(){}
	public static SuperMario getInstance() {
		if(null == INSTANCE){
			synchronized (SuperMario.class){
				if(null == INSTANCE){
					INSTANCE = new SuperMario();
				}
			}
		}
		return INSTANCE;
	}
	
	@Override
	public State getName() {
		return State.SUPER;
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
