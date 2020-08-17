package com.loveoyh.springframework.annotation;

import java.lang.annotation.*;

/**
 * @Created by oyh.Jerry to 2020/03/03 17:48
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

	String value() default "";
	
}
