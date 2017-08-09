/**  
 * Project Name:http-server  
 * File Name:RestActionHandler.java  
 * Package Name:com.sparrow.core.http.action  
 * Date:2014-1-3下午1:09:38  
 *  
 */

package com.sparrow.server.handler;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.handler.DefaultHandler;
import com.sparrow.server.controller.ActionController;
import com.sparrow.server.controller.ActionControllerFactory;

/**
 * ClassName:RestActionHandler <br/>
 * Date: 2014-1-3 下午1:09:38 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class NormalRestActionHandler extends DefaultHandler {
	private ActionController controller;

	public NormalRestActionHandler( ) {

	}

	public NormalRestActionHandler(ActionController controller) {
		this.controller = controller;
	}

	public ActionController getController() {
		return controller;
	}

	public void setController(ActionController controller) {
		this.controller = controller;
	}

	@Override
	protected void doInitialize() {
		if (this.controller == null)
			this.controller = ActionControllerFactory.configureActionController(
					SystemConfig.getProperty("bean.config","classpath:eggs/config.xml"));
		this.controller.initialize();
	}

	@Override
	public void process(HttpRequest request, HttpResponse response)
			throws Throwable {
		this.controller.process(request, response);
	}

}
