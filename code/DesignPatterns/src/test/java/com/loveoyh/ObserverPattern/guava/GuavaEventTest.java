package com.loveoyh.ObserverPattern.guava;

import com.google.common.eventbus.EventBus;

/**
 * @Created by oyh.Jerry to 2020/03/01 11:37
 */
public class GuavaEventTest {
	public static void main(String[] args) {
		EventBus eventBus = new EventBus();
		GuavaEvent guavaEvent = new GuavaEvent();
		eventBus.register(guavaEvent);
		eventBus.post("oyh");
	}
}
