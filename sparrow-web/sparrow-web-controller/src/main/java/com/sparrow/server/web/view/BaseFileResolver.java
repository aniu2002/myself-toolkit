/**  
 * Project Name:http-server  
 * File Name:BaseFileResolver.java  
 * Package Name:com.sparrow.core.web.view  
 * Date:2014-1-3下午3:01:43  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.server.web.view;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.common.HttpHelper;
import com.sparrow.http.view.FreemarkerView;
import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.config.SystemConfig;

/**
 * ClassName:BaseFileResolver <br/>
 * Date: 2014-1-3 下午3:01:43 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class BaseFileResolver implements Resolver {
	private final String prePath = SystemConfig.WEB_ROOT;
	private final String suffix = ".html";

	@Override
	public void resolve(HttpRequest request, HttpResponse response,
			String module, String path) {
		if (path.startsWith("redirect:")) {
			HttpHelper.redirect(response, path.substring(9) + suffix);
		} else if (path.startsWith("view:")) {
			response.setView(new FreemarkerView(path.substring(5), request.getParas()));
		} else {
			String mPath = PathResolver.formatPath(this.prePath, module);
			response.setSource(PathResolver.formatPath(mPath, path) + suffix);
		}
	}
}
