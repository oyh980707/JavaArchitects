package com.loveoyh.BuilderPattern;

/**
 * @Created by oyh.Jerry to 2020/09/28 09:19
 */
public class TestCase {
	public static void main(String[] args) {
		Demo demo = new Demo.DefaultBuilder().setStr("hello word!").build();
		
		ResponseEntity result = ResponseEntity.status(100).build();
		ResponseEntity ok = ResponseEntity.ok().build();
		ResponseEntity badRequest = ResponseEntity.badRequest().build();
	}
}
