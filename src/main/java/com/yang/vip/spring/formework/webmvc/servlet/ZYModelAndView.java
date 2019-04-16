package com.yang.vip.spring.formework.webmvc.servlet;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

public class ZYModelAndView {
    private String viewName;
    private Map<String,?> model;

    public ZYModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ZYModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
