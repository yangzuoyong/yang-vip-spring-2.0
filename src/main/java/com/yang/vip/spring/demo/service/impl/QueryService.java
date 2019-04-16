package com.yang.vip.spring.demo.service.impl;

import com.yang.vip.spring.demo.service.IQueryService;
import com.yang.vip.spring.formework.annotation.ZYService;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询业务
 */
@ZYService
@Slf4j
public class QueryService implements IQueryService {
    @Override
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = String.format("name:%s,time:%s",name,time);
        log.info(String.format("这是在业务方法中打印的：",json));
        return json;
    }
}
