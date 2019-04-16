package com.yang.vip.spring.formework.aop.config;

import lombok.Data;

@Data
public class ZYAopConfig {
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
