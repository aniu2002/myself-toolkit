/**  
 * Project Name:http-server  
 * File Name:AnnotationController.java  
 * Package Name:com.sparrow.core.http.web
 * Date:2014-1-3上午11:44:27  
 *  
 */

package com.sparrow.server.controller;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;

import java.util.List;

/**
 * ClassName:AnnotationController <br/>
 * Date: 2014-1-3 上午11:44:27 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class AnnotationController implements ActionController {

	@Override
	public void process(HttpRequest request, HttpResponse response) {

	}

	@Override
	public void initialize() {

	}

	@Override
	public void addControllerBean(ActionBeanConfig cfg) {
		System.out.println("web : " + cfg.getClaz());
	}

	@Override
	public void removeController(String name) {

	}

	@Override
	public void addControllerConfig(ActionBeanConfig cfg) {

	}

	@Override
	public void resetController(List<ActionBeanConfig> beans) {
		// TODO Auto-generated method stub

	}

}
