package com.loveoyh.BuilderPattern;

import java.io.Serializable;

/**
 * 建造者模式
 * 参考 org.springframework.http.ResponseEntity.class
 * @Created by oyh.Jerry to 2020/09/28 09:03
 */
public class ResponseEntity implements Serializable {
	private final Integer status;
	private final Object body;
	
	private ResponseEntity(Integer status, Object body){
		this.status = status;
		this.body = body;
	}
	
	public static BodyBuilder status(Integer status) {
		if(null == status){
			throw new IllegalArgumentException("status arg must be not null.");
		}
		return new DefaultBuilder(status);
	}
	
	public static BodyBuilder ok(){
		return status(200);
	}
	public static ResponseEntity ok(Object body){
		return ok().build(body);
	}
	
	public static BodyBuilder badRequest(){
		return status(400);
	}
	
	public static BodyBuilder notFound(){
		return status(404);
	}
	
	public interface BodyBuilder {
		ResponseEntity body(Object body);
		ResponseEntity build();
		ResponseEntity build(Object body);
	}
	
	private static class DefaultBuilder implements BodyBuilder{
		private final Integer statusCode;
		
		public DefaultBuilder(Integer statusCode) {
			this.statusCode = statusCode;
		}
		
		@Override
		public ResponseEntity build(){
			return build(null);
		}
		
		@Override
		public ResponseEntity build(Object body){
			return new ResponseEntity(this.statusCode, body);
		}
		
		@Override
		public ResponseEntity body(Object body) {
			return new ResponseEntity(this.statusCode, body);
		}
	}
	
}
