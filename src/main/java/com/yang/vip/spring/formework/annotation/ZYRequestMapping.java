package com.yang.vip.spring.formework.annotation;

import java.lang.annotation.*;

/**请求URL*/
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZYRequestMapping {
    String value() default "";
}
