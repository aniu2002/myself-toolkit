/**
 * Project Name:http-server  
 * File Name:RestActionHandler.java  
 * Package Name:com.sparrow.core.http.action  
 * Date:2014-1-3下午1:09:38  
 *
 */

package com.sparrow.server.handler;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.filter.HttpFilter;
import com.sparrow.http.handler.ActionHandler;
import com.sparrow.server.controller.ActionController;
import com.sparrow.server.controller.ActionControllerFactory;
import com.sparrow.server.filter.AbstractHttpFilter;
import com.sparrow.server.filter.DefaultHttpFilter;

/**
 * ClassName:RestActionHandler <br/>
 * Date: 2014-1-3 下午1:09:38 <br/>
 *
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public class RestActionHandler extends ActionHandler {
    private ActionController controller;
    private HttpFilter httpFilter;

    public RestActionHandler() {
        this(null, null);
    }

    public RestActionHandler(HttpFilter httpFilter) {
        this(null, httpFilter);
    }

    public RestActionHandler(ActionController controller,
                             HttpFilter httpFilter) {
        this.controller = controller;
        this.httpFilter = httpFilter;
    }

    public ActionController getController() {
        return controller;
    }

    public void setController(ActionController controller) {
        this.controller = controller;
    }

    public HttpFilter getHttpdFilter() {
        return httpFilter;
    }

    public void setHttpdFilter(AbstractHttpFilter httpdFilter) {
        this.httpFilter = httpdFilter;
    }

    @Override
    protected void doProcess(HttpRequest request, HttpResponse response)
            throws Throwable {
        this.httpFilter.doFilter(request, response);
    }

    @Override
    protected void doInitialize() {
        if (this.controller == null)
            this.controller = ActionControllerFactory.getActionController();
        this.controller.initialize();
        if (this.httpFilter == null)
            this.httpFilter = new DefaultHttpFilter();
        if (this.httpFilter instanceof AbstractHttpFilter)
            ((AbstractHttpFilter) this.httpFilter).setTargetController(this.controller);
    }

    @Override
    protected void doStop() {
        this.controller = null;
        this.httpFilter = null;
    }
}
