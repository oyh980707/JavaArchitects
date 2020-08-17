package com.loveoyh.springframework.annotation;

import java.lang.annotation.*;

/**
 * @Created by oyh.Jerry to 2020/03/03 17:52
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
	
	String value() default "";
	
}
