package com.yang.vip.spring.formework.annotation;

import java.lang.annotation.*;

/** 自动注入 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZYAutowired {
    String value() default "";
}
