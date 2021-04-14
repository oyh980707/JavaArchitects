package com.loveoyh.StatePattern.state;

import com.loveoyh.StatePattern.MarioStateMachine;

/**
 * 马里奥获得火焰后的状态
 * @Created by oyh.Jerry to 2021/04/14 14:46
 */
public class FireMario implements IMario {
	
	private static volatile FireMario INSTANCE;
	
	private FireMario(){}
	public static FireMario getInstance() {
		if(null == INSTANCE){
			synchronized (FireMario.class){
				if(null == INSTANCE){
					INSTANCE = new FireMario();
				}
			}
		}
		return INSTANCE;
	}
	
	@Override
	public State getName() {
		return State.FIRE;
	}

	@Override
	public void meetMonster(MarioStateMachine stateMachine) {
		stateMachine.setCurrentState(SmallMario.getInstance());
		stateMachine.setScore(stateMachine.getScore() - 300);
	}
}
