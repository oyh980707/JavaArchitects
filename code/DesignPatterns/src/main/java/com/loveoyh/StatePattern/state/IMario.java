package com.loveoyh.StatePattern.state;

import com.loveoyh.StatePattern.MarioStateMachine;
import com.loveoyh.StatePattern.UnsupportOperateException;

public interface IMario {
	
	State getName();
	
	default void obtainMushRoom(MarioStateMachine stateMachine) {
		throw new UnsupportOperateException();
	};
	
	default void obtainCape(MarioStateMachine stateMachine) {
		throw new UnsupportOperateException();
	};
	
	default void obtainFireFlower(MarioStateMachine stateMachine) {
		throw new UnsupportOperateException();
	};
	
	default void meetMonster(MarioStateMachine stateMachine) {
		throw new UnsupportOperateException();
	};
	
}
