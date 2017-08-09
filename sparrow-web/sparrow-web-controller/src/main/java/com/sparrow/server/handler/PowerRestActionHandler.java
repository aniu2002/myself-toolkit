/**
 * Project Name:http-server
 * File Name:RestActionHandler.java
 * Package Name:com.sparrow.core.http.action
 * Date:2014-1-3下午1:09:38
 */

package com.sparrow.server.handler;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.security.cache.CacheManager;
import com.sparrow.security.handler.SecurityHandler;
import com.sparrow.security.relam.BRealm;
import com.sparrow.security.relam.DefaultBRelam;
import com.sparrow.server.controller.ActionController;
import com.sparrow.server.controller.ActionControllerFactory;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * ClassName:RestActionHandler <br/>
 * Date: 2014-1-3 下午1:09:38 <br/>
 *
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class PowerRestActionHandler extends SecurityHandler {
    private ActionController controller;

    public PowerRestActionHandler(String logUrl, String succesUrl) {
        this(null, new DefaultBRelam(), logUrl, succesUrl);
    }

    public PowerRestActionHandler(BRealm bRelam, String logUrl, String succesUrl) {
        this(null, bRelam, logUrl, succesUrl);
    }

    public PowerRestActionHandler(ActionController controller, BRealm bRelam,
                                  String logUrl, String succesUrl) {
        this.controller = controller;
        this.initializeSecurity(bRelam, new CacheManager(), PATH_SET, logUrl,
                succesUrl);
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
                    SystemConfig.getProperty("bean.config", "classpath:eggs/config.xml"));
        this.controller.initialize();
    }

    @Override
    public void process(HttpRequest request, HttpResponse response)
            throws Throwable {
        this.controller.process(request, response);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.doHandle(exchange);
    }
}
