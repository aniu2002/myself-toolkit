/**  
 * Project Name:http-server  
 * File Name:PermissionsAuthorizationFilter.java  
 * Package Name:com.sparrow.core.security.authz  
 * Date:2013-12-30下午6:15:27  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.security.authz;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.security.subject.Subject;


/**
 * ClassName:PermissionsAuthorizationFilter <br/>
 * Date: 2013-12-30 下午6:15:27 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class PermissionsAuthorizationFilter extends AuthorizationFilter {

	@Override
	protected boolean isAccessAllowed(HttpRequest request,
			HttpResponse response, Object mappedValue) {
		Subject subject = getSubject(request, response);

		boolean isPermitted = true;
		if (!subject.isPermitted(request.getPathInfo())) {
			isPermitted = false;
		}
			
/*		String[] perms = (String[]) mappedValue;
		if (perms != null && perms.length > 0) {
			if (perms.length == 1) {
				if (!subject.isPermitted(perms[0])) {
					isPermitted = false;
				}
			} else {
				if (!subject.isPermittedAll(perms)) {
					isPermitted = false;
				}
			}
		}*/
		return isPermitted;
	}
}
