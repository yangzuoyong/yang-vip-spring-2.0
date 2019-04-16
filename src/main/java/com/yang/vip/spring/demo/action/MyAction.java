package com.yang.vip.spring.demo.action;

import com.yang.vip.spring.demo.service.IModifyService;
import com.yang.vip.spring.demo.service.IQueryService;
import com.yang.vip.spring.formework.annotation.ZYAutowired;
import com.yang.vip.spring.formework.annotation.ZYController;
import com.yang.vip.spring.formework.annotation.ZYRequestMapping;
import com.yang.vip.spring.formework.annotation.ZYRequestParam;
import com.yang.vip.spring.formework.webmvc.servlet.ZYModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ZYController
@ZYRequestMapping("/web")
public class MyAction {
    @ZYAutowired
    IQueryService queryService;
    @ZYAutowired
    IModifyService modifyService;
    public ZYModelAndView query(HttpServletRequest request, HttpServletResponse response, @ZYRequestParam("name")String name){
        return out(response,queryService.query(name));
    }
    @ZYRequestMapping("/add*.json")
    public ZYModelAndView add(HttpServletRequest request,HttpServletResponse response,
                              @ZYRequestParam("name") String name,@ZYRequestParam("addr") String addr){
        String result = null;
        try {
            return out(response,modifyService.add(name,addr));
        } catch (Exception e) {
            Map<String,Object> model = new HashMap<String,Object>();
            model.put("detail",e.getCause().getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
            return new ZYModelAndView("500",model);
        }

    }

    @ZYRequestMapping("/remove.json")
    public ZYModelAndView remove(HttpServletRequest request,HttpServletResponse response,
                                 @ZYRequestParam("id") Integer id){
        return out(response,modifyService.remove(id));
    }

    @ZYRequestMapping("/edit.json")
    public ZYModelAndView edit(HttpServletRequest request,HttpServletResponse response,
                               @ZYRequestParam("id") Integer id,
                               @ZYRequestParam("name") String name){
        return out(response,modifyService.edit(id,name));
    }
    private ZYModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
