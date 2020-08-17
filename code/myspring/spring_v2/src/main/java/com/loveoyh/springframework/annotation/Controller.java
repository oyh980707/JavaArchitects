package com.loveoyh.springframework.annotation;

import java.lang.annotation.*;

/**
 * @Created by oyh.Jerry to 2020/03/03 17:49
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {

	String value() default "";

}
