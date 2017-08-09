package com.sparrow.security.handler;

import com.sparrow.http.handler.DefaultHandler;
import com.sparrow.security.SecurityUtils;
import com.sparrow.security.cache.CacheManager;
import com.sparrow.security.relam.BRealm;
import com.sparrow.security.relam.DefaultBRelam;
import com.sparrow.security.subject.Subject;
import com.sparrow.security.subject.SubjectManager;
import com.sparrow.security.subject.WebSecurityManager;
import com.sparrow.security.web.WebRequestFilter;
import com.sparrow.core.utils.ClassUtils;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.base.HttpProcessor;
import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.http.common.HttpHelper;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class SecurityHandler extends DefaultHandler implements HttpProcessor {
    public static final String[] PATH_SET = new String[]{"/**=authc,authz",
            "/db=anon"};
    private WebRequestFilter httpdFilter;
    private final String loginUrl = "/login";

    public SecurityHandler() {

    }

    public SecurityHandler(String loginUrl, String successUrl) {
        this(new DefaultBRelam(), loginUrl, successUrl);
    }

    public SecurityHandler(BRealm bRelam, String loginUrl, String successUrl) {
        this(bRelam, new CacheManager(), loginUrl, successUrl);
    }

    public SecurityHandler(BRealm bRelam, CacheManager cacheManager,
                           String loginUrl, String successUrl) {
        this(bRelam, cacheManager, PATH_SET, loginUrl, successUrl);
    }

    public SecurityHandler(BRealm bRelam, CacheManager cacheManager,
                           String[] pathSet, String loginUrl, String successUrl) {
        this.initializeSecurity(bRelam, cacheManager, pathSet, loginUrl,
                successUrl);
    }

    protected void initializeSecurity(BRealm bRelam, CacheManager cacheManager,
                                      String[] pathSet, String loginUrl, String successUrl) {
        WebSecurityManager securityManager = new WebSecurityManager();
        securityManager.setCacheManager(cacheManager);
        if (bRelam == null) {
            String name = SystemConfig.getProperty("security.relam",
                    "com.sparrow.security.relam.DefaultBRelam");
            bRelam = ClassUtils.instance(name, BRealm.class);
        }
        securityManager.setRelam(bRelam);
        SecurityUtils.setSecurityManager(securityManager);

        this.httpdFilter = new WebRequestFilter(loginUrl, successUrl);
        this.httpdFilter.setProcessor(this);

        this.setPathConfig(this.httpdFilter, pathSet);
    }

    void setPathConfig(WebRequestFilter httpdFilter, List<String> paths) {
        for (String s : paths) {
            String ng[] = StringUtils.tokenizeToStringArray(s, "=");
            httpdFilter.processPathConfig(ng[0], ng[1]);
        }
    }

    void setPathConfig(WebRequestFilter httpdFilter, String[] paths) {
        for (String s : paths) {
            String ng[] = StringUtils.tokenizeToStringArray(s, "=");
            httpdFilter.processPathConfig(ng[0], ng[1]);
        }
    }

    @Override
    protected void doHandle(final HttpRequest request,
                            final HttpResponse response) throws Exception {
        boolean isLogin = false;
        boolean isGet = "get".equalsIgnoreCase(request.getMethod());
        if (isGet && StringUtils.equals(this.loginUrl, request.getPathInfo()))
            isLogin = true;

        final WebRequestFilter httpdFilter = this.httpdFilter;
        Map<String, String> cookies = HttpHelper.getCookies(request
                .getHeaders());
        String sid = null;
        if (cookies != null)
            sid = cookies.get(HttpHelper.SESSION_ID);
        Subject subject = null;
        if (sid != null) {
            subject = SubjectManager.getSubject(sid);
        } else {
            sid = UUID.randomUUID().toString();
            HttpHelper.createCookieMark(response.getHeaders(),
                    HttpHelper.SESSION_ID, sid);
        }
        if (subject == null) {
            subject = new Subject(sid);
            SubjectManager.saveSubject(subject);
        }

        if (isGet && !isLogin && !subject.isAuthenticated()) {
            HttpHelper.redirectToLoginPath(request, response, SystemConfig.LOGIN_PATH);
            return;
        }
        subject.execute(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                httpdFilter.doFilter(request, response);
                return Boolean.TRUE;
            }
        });
    }
}
