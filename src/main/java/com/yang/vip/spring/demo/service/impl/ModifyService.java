package com.yang.vip.spring.demo.service.impl;

import com.yang.vip.spring.demo.service.IModifyService;
import com.yang.vip.spring.formework.annotation.ZYService;

/**
 * 增删改业务
 */
@ZYService
public class ModifyService implements IModifyService {

    @Override
    public String add(String name, String addr) {
        return String.format("modifyService add,name=%s,addr=%s",name,addr);
    }

    @Override
    public String edit(Integer id, String name) {
        return String.format("modifyService edit,id=%s,name=%s",id,name);
    }

    @Override
    public String remove(Integer id) {
        return String.format("modifyService remove,id=%s",id);
    }
}
