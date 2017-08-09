/**
 * Project Name:http-server  
 * File Name:AccessControlFilter.java  
 * Package Name:com.sparrow.core.security.web  
 * Date:2013-12-30下午4:43:52  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *
 */

package com.sparrow.security.web;

import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.common.HttpStatus;
import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.security.SecurityUtils;
import com.sparrow.security.subject.Subject;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * ClassName:AccessControlFilter <br/>
 * Date: 2013-12-30 下午4:43:52 <br/>
 *
 * @author YZC
 * @see
 * @since JDK 1.6
 */
public abstract class AccessControlFilter extends PathMatchingFilter {
    public static final String DEFAULT_LOGIN_URL = SystemConfig.LOGIN_PATH;
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";

    /**
     * <property name="loginUrl" value="/login.jsp" /> <br/>
     * <property name="successUrl" value="/rest/home" /> <br/>
     * <property name="unauthorizedUrl" value="/errors/404.jsp" />
     */
    private String loginUrl = DEFAULT_LOGIN_URL; // DEFAULT_LOGIN_URL

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    protected Subject getSubject(HttpRequest request, HttpResponse response) {
        return SecurityUtils.getSubject();
    }

    protected boolean isAuthenticated(HttpRequest request, HttpResponse response) {
        return this.getSubject(request, response).isAuthenticated();
    }

    /**
     * 用户认证
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     * @throws Exception
     * @author YZC
     */
    protected abstract boolean isAccessAllowed(HttpRequest request,
                                               HttpResponse response, Object mappedValue);

    /**
     * 权限验证
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     * @throws Exception
     * @author YZC
     */
    protected boolean onAccessDenied(HttpRequest request,
                                     HttpResponse response, Object mappedValue) throws Exception {
        return onAccessDenied(request, response);
    }

    protected abstract boolean onAccessDenied(HttpRequest request,
                                              HttpResponse response) throws Exception;

    @Override
    public boolean onPreHandle(HttpRequest request, HttpResponse response,
                               String[] mappedValue) throws Exception {
        // accessAllowed 是否通过验证，并且非login地址，并且允许访问的资源
        // onAccessDenied 是否不被拒绝的，login 地址，get
        // 获取页面，post提交验证，非login直接跳转，返回false，不进入controller
        return this.isAccessAllowed(request, response, mappedValue)
                || this.onAccessDenied(request, response, mappedValue);
    }

    protected boolean isLoginRequest(HttpRequest request, HttpResponse response) {
        if (StringUtils.isEmpty(request.getPathInfo()))
            return true;
        return this.pathsMatch(getLoginUrl(), request);
    }

    protected void saveRequestAndRedirectToLogin(HttpRequest request,
                                                 HttpResponse response) throws IOException {
        saveRequest(request);
        redirectToLogin(request, response);
    }

    protected void saveRequest(HttpRequest request) {
        WebTool.saveRequest(request);
    }

    protected void redirectToLogin(HttpRequest request, HttpResponse response)
            throws IOException {
        String loginUrl = getLoginUrl();
        SysLogger.info(" - Redirect to login page '{}'", loginUrl);
        if (request.isAjaxRequest()) {
            response.setMessage(PathResolver.formatRelativePath(
                    request.getCxtPath(), loginUrl));
            response.setStatus(HttpStatus.SC_HTTP_COUSTM_SECURITY);
        } else
            WebTool.redirect(request, response, loginUrl);
    }
}
