package com.loveoyh.springframework.annotation;

import java.lang.annotation.*;

/**
 * @Created by oyh.Jerry to 2020/03/03 17:45
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
	String value() default "";
}
