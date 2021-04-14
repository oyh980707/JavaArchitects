package com.loveoyh.StatePattern.state;

import com.loveoyh.StatePattern.MarioStateMachine;

/**
 * 马里奥获取斗篷后的状态
 * @Created by oyh.Jerry to 2021/04/14 14:45
 */
public class CapeMario implements IMario {
	
	private static volatile CapeMario INSTANCE;
	
	private CapeMario(){}
	public static CapeMario getInstance() {
		if(null == INSTANCE){
			synchronized (CapeMario.class){
				if(null == INSTANCE){
					INSTANCE = new CapeMario();
				}
			}
		}
		return INSTANCE;
	}
	
	@Override
	public State getName() {
		return State.CAPE;
	}

	@Override
	public void meetMonster(MarioStateMachine stateMachine) {
		stateMachine.setCurrentState(SmallMario.getInstance());
		stateMachine.setScore(stateMachine.getScore() - 200);
	}
}
