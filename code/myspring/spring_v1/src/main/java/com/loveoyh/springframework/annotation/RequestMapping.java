package com.loveoyh.springframework.annotation;

import java.lang.annotation.*;

/**
 * @Created by oyh.Jerry to 2020/03/03 17:50
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

	String value() default "";
	
}
