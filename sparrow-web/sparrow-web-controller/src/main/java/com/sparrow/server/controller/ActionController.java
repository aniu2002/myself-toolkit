/**
 * Project Name:http-server  
 * File Name:ActionController.java  
 * Package Name:com.sparrow.core.http.controller  
 * Date:2014-1-3上午11:41:58  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *
 */

package com.sparrow.server.controller;

import java.util.List;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;

/**
 * ClassName:ActionController <br/>
 * Date: 2014-1-3 上午11:41:58 <br/>
 *
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public interface ActionController {
    void initialize();

    void process(HttpRequest request, HttpResponse response) throws Throwable;

    void addControllerBean(ActionBeanConfig cfg);

    void addControllerConfig(ActionBeanConfig cfg);

    void resetController(List<ActionBeanConfig> beans);

    void setBeanConfig(String config);

    void removeController(String name);
}
