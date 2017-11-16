package com.sparrow.server.web.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sparrow.core.exception.RefInvokeException;
import com.sparrow.http.base.HttpRequest;
import com.sparrow.http.base.HttpResponse;
import com.sparrow.server.controller.ActionBeanConfig;
import com.sparrow.server.controller.ActionController;
import com.sparrow.core.log.SysLogger;
import com.sparrow.orm.session.SessionFactory;
import com.sparrow.server.context.Application;
import com.sparrow.service.context.AnnotationHelper;
import com.sparrow.service.context.AppServiceContext;
import com.sparrow.service.context.ServiceContext;
import com.sparrow.service.exception.BeanDefineException;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.server.web.annotation.ReqMapping;
import com.sparrow.server.web.annotation.WebController;
import com.sparrow.server.web.common.ModuleComparator;
import com.sparrow.server.web.config.ControllerClassConfig;
import com.sparrow.server.web.config.ControllerMethodConfig;
import com.sparrow.server.web.config.MatchedHandler;
import com.sparrow.server.web.config.UrlMatcherItem;
import com.sparrow.server.web.config.UrlMatcherMethodConfig;
import com.sparrow.server.web.config.UrlMatcherTool;
import com.sparrow.server.web.converter.MessageConverter;
import com.sparrow.server.web.converter.MessageConverterFactory;
import com.sparrow.server.web.meta.RequestInvoker;
import com.sparrow.server.web.resource.MessageResource;
import com.sparrow.server.web.resource.ResourcesFactory;
import com.sparrow.server.web.struct.UrlPatternCheck;
import com.sparrow.server.web.view.BaseFileResolver;
import com.sparrow.server.web.view.Resolver;
import org.apache.commons.beanutils.BeanUtils;

public class ControllerFacade extends AnnotationHelper implements
        ActionController {
    private static final String DELIMITERS = ",";
    private ServiceContext context;
    private String excludeReqs = "jsp,js,html";
    private String excludes[];
    private String resoucesKey = "messages.test";
    private ControllerClassConfig controllers[];
    private ControllerClassConfig defaultController;
    private List<ActionBeanConfig> controllerBeanList = new ArrayList<ActionBeanConfig>();
    private Resolver resolver = new BaseFileResolver();
    private Object synObject = new Object();

    public boolean isContains(String str, String inds[]) {
        if (str == null)
            return false;
        for (int i = 0; i < inds.length; i++) {
            if (str.equals(inds[i])) {
                return true;
            }
        }
        return false;
    }

    public void initialize() throws BeanDefineException {
        this.excludes = StringUtils.tokenizeToStringArray(this.excludeReqs,
                DELIMITERS);
        // this.annotationEvent(new AnnotationConfig(this.scanPath), bcfg);
        context = new AppServiceContext("classpath*:beans/*.xml", this);
        //
        Application.app().setActionController(this);
        Application.app().setServiceContext(context);
        Application.app().setSessionFactory(
                (SessionFactory) context.getBean("sessionFactory"));
        //
        MessageResource resources = this.initMessageResouce(this.resoucesKey);
        context.setBean("messageResource", resources);
        context.initialize(true);

        WebBundle.loadWebBundle(true);
        if (this.controllerBeanList != null
                && !this.controllerBeanList.isEmpty())
            this.initializeController();
    }

    private MessageResource initMessageResouce(String resoucesKey) {
        ResourcesFactory factoryObject = ResourcesFactory.createFactory();
        return factoryObject.createResources(resoucesKey);
    }

    public void addControllerBean(ActionBeanConfig cfg) {
        // this.controllerBeanList.add(cfg);
        synchronized (synObject) {
            ControllerClassConfig[] oriControllers = this.controllers;
            int len = oriControllers == null ? 0 : oriControllers.length;
            ControllerClassConfig[] newControllers = new ControllerClassConfig[len + 1];
            if (len > 0) {
                for (int i = 0; i < len; i++)
                    newControllers[i] = oriControllers[i];
            }
            ControllerClassConfig controllerCfg = this.createControllerConfig(
                    cfg, cfg.getClazzRef());
            newControllers[len] = controllerCfg;
            this.controllers = newControllers;
        }
    }

    private ControllerClassConfig createControllerConfig(ActionBeanConfig cfg,
                                                         Class<?> clazz) {
        WebController webc = clazz.getAnnotation(WebController.class);
        String controlPath = webc.value();
        ControllerClassConfig webcfg = new ControllerClassConfig();
        webcfg.setControllerClazz(clazz);
        webcfg.setModule(controlPath);
        webcfg.setBeanClazz(cfg.getClaz());
        webcfg.setBeanId(cfg.getId());
        Method methods[] = clazz.getDeclaredMethods();
        SysLogger.info("-- Controller[" + webcfg.getModule() + "] - class="
                + cfg.getClaz());
        for (Method method : methods) {
            if (method.isAnnotationPresent(ReqMapping.class)) {
                ReqMapping reqm = method.getAnnotation(ReqMapping.class);
                String path = reqm.value();
                if (path.length() > 0 && path.charAt(0) != '/')
                    path = "/" + path;
                // PathResolver.addRootSeparator(
                boolean patternurl = UrlPatternCheck.parse(path);
                if (patternurl) {
                    UrlMatcherItem itm = UrlMatcherTool.match(path,
                            reqm.method());
                    if (itm != null) {
                        UrlMatcherMethodConfig methodCfg = new UrlMatcherMethodConfig(
                                itm);
                        methodCfg.setMethodInvoker(method);
                        methodCfg.setPath(path);
                        methodCfg.setReqMethod(reqm.method());
                        SysLogger.info(" - Mapping : " + methodCfg.getPath()
                                + " - " + methodCfg.getReqMethod());
                        webcfg.add(methodCfg);
                    }
                } else {
                    ControllerMethodConfig methodCfg = new ControllerMethodConfig();
                    methodCfg.setMethodInvoker(method);
                    methodCfg.setPath(path);
                    methodCfg.setReqMethod(reqm.method());
                    SysLogger.info(" - Mapping : " + methodCfg.getPath()
                            + " - " + methodCfg.getReqMethod());
                    webcfg.add(methodCfg.getPath(), methodCfg);
                }
            }
        }
        /** 初始化 */
        webcfg.initialize();
        return webcfg;
    }

    private void initializeController() {
        System.out
                .println("********* Initialize web begin *****************");
        try {
            Iterator<ActionBeanConfig> iter = this.controllerBeanList
                    .iterator();
            Class<?> clazz;
            List<ControllerClassConfig> list = new ArrayList<ControllerClassConfig>();
            while (iter.hasNext()) {
                ActionBeanConfig cfg = iter.next();
                clazz = cfg.getClazzRef();
                if (clazz == null)
                    continue;
                if (clazz.isAnnotationPresent(WebController.class)) {
                    ControllerClassConfig webcfg = this.createControllerConfig(
                            cfg, clazz);
                    if ("".equals(webcfg.getModule())
                            || "/".equals(webcfg.getModule().trim())) {
                        this.defaultController = webcfg;
                        continue;
                    }
                    list.add(webcfg);
                }
            }
            Collections.sort(list, new ModuleComparator());
            controllers = list.toArray(new ControllerClassConfig[list.size()]);
            // for (ControllerClassConfig c : controllers)
            // System.out.println(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out
                .println("********* Initialize  web end *****************");
    }

    public void process(HttpRequest request, HttpResponse response)
            throws Throwable {
        // 500
        String matchPath = request.getPathInfo();
        String _oriMethod = request.getMethod();
        String httpMethod = _oriMethod;
        if ("post".equalsIgnoreCase(httpMethod)) {
            String tmp = request.getParameter("_method");
            if (tmp != null)
                httpMethod = tmp;
        }
        SysLogger.info(" {} ", matchPath);
        ControllerClassConfig controller = this.getController(matchPath);
        System.out.println(" - Request : " + matchPath + " , " + controller
                + " - " + _oriMethod);
        if (controller == null) {
            throw new RefInvokeException(
                    " Controller is not matched ; Path:" + matchPath);
        } else {
            int length = controller.getModule().length();
            String mapPath = matchPath;
            if (length > 1 && StringUtils.isNotEmpty(matchPath))
                mapPath = matchPath.substring(length);
            String reqPath = "";
            String suffix = null;
            if (StringUtils.isEmpty(mapPath)) {
                mapPath = "/";
            } else {
                int ind = mapPath.lastIndexOf('/');
                if (ind > 0) {
                    reqPath = mapPath.substring(0, ind);
                    mapPath = mapPath.substring(ind);
                }
                ind = mapPath.lastIndexOf('.');
                if (ind != -1) {
                    suffix = mapPath.substring(ind + 1);
                    mapPath = mapPath.substring(0, ind);
                }
                reqPath = reqPath + mapPath;
            }
            // 过滤请求
            if (this.isContains(suffix, this.excludes)) {
                String str = this.getMessage(-1, suffix,
                        "refused for suffix : " + suffix);
                throw new RefInvokeException(str);
            }

            MatchedHandler handler = controller.getMatched(reqPath, httpMethod);
            if (handler == null) {
                String str = this.getMessage(-1, suffix,
                        "Can't found Method for req : " + reqPath);
                throw new RefInvokeException(str);
            } else {
                ControllerMethodConfig methodCfg = handler.getMethodcfg();
                RequestInvoker invoker = methodCfg.getMethodInvoker();
                // this.setPathVariables(request, handler);
                ReqValueGetter getter = new ReqValueGetter(request, handler);
                System.out.println(" - Handler : " + reqPath + " # method="
                        + invoker.getMethod().getName());
                Map<String, String> reqParas = request.getParas();
                System.out.println(" - Parameters : " + reqParas);
                Object result = invoker.invoke(controller.getControllerInst(),
                        getter);
                MessageConverter converter = invoker.getConverter();
                if (converter == null) {
                    converter = this.getMessageConverter(request, response,
                            suffix);
                    // 构建content type的subtype
                    if (result instanceof String) {
                        String s = result.toString();
                        response.setMessage(s);
                        this.resolver.resolve(request, response,
                                controller.getModule(), s);
                        return;
                    }
                }
                response.setContentType(converter.getMimeType());
                response.setMessage(converter.convert(result));
                this.handleReturn(request, response);
            }
        }
    }

    protected void setPathVariables(HttpRequest request, MatchedHandler handler) {
        if (handler == null)
            return;
        String[] paraKeys = handler.getParakeys();
        String[] values = handler.getValues();
        int len = (paraKeys == null ? 0 : paraKeys.length);
        for (int i = 0; i < len; i++)
            request.addParameter(paraKeys[i], values[i]);
    }

    protected MessageConverter getMessageConverter(HttpRequest request,
                                                   HttpResponse response, String suffix) {
        MessageConverter converter;
        String format = suffix;
        if (suffix == null)
            format = request.getParameter("format");
        if (format == null) {
            if (request.isAjaxRequest())
                converter = MessageConverterFactory.JSON_CONVERTER;
            else
                converter = MessageConverterFactory.getConverter(request
                        .getMimeType());
        } else if ("json".equals(suffix)) {
            converter = MessageConverterFactory.JSON_CONVERTER;
        } else if ("xml".equals(suffix)) {
            converter = MessageConverterFactory.XML_CONVERTER;
        } else
            converter = MessageConverterFactory.getConverter(suffix);
        if (converter == null)
            converter = MessageConverterFactory.JSON_CONVERTER;
        return converter;
    }

    protected String getMessage(int code, String reqtype, String msg) {
        if ("json".equals(reqtype)) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"flag\":").append(code).append(",\"msg\":\"")
                    .append(msg).append("\"}");
            return sb.toString();
        }
        return msg;
    }

    protected void handleReturn(HttpRequest request, HttpResponse response)
            throws IOException {

    }

    protected void wrapError(HttpResponse response, String error) {
        wrapError(response, "text/html;charset=UTF-8", error);
    }

    protected void wrapError(HttpResponse response, String contentType,
                             String error) {
        response.setStatus(500);
        response.setContentType(contentType);
        response.setCharEncoding("UTF-8");
        response.setMessage(error);
    }

    public Map<String, String> featchParas(HttpRequest request, String[] paras) {
        Map<String, String> map = new HashMap<String, String>();
        for (String pkey : paras) {
            map.put(pkey, request.getParameter(pkey));
        }
        return map;
    }

    public Map<String, String> featchAllParas(HttpRequest request) {
        return request.getParas();
    }

    private ControllerClassConfig getController(String path) {
        ControllerClassConfig controller = this.selectController(path);
        if (controller == null)
            return null;
        if (controller.getControllerInst() != null)
            return controller;
        synchronized (synObject) {
            if (controller.getControllerInst() == null) {
                Object controllerInst = this.context.getBean(controller
                        .getBeanId());
                if (controllerInst == null)
                    throw new BeanDefineException(
                            "Controller instance can't load . Path:" + path);
                controller.setControllerInst(controllerInst);
            }
        }
        return controller;
    }

    private ControllerClassConfig selectController(String path) {
        for (ControllerClassConfig controller : this.controllers) {
            if (controller != null && path.startsWith(controller.getModule())) {
                return controller;
            }
        }
        return this.defaultController;
    }

    public void addControllerConfig(ActionBeanConfig cfg) {
        this.controllerBeanList.add(cfg);
    }

    @Override
    protected com.sparrow.service.config.SimpleBeanConfig getAnnotationSimpleConfig(Class<?> claz) {
        if (claz.isAnnotationPresent(WebController.class)) {
            com.sparrow.service.config.SimpleBeanConfig sbcfg = ControllerAnnotationHelper
                    .getWebSampleConfig(claz);
            ActionBeanConfig simpleBeanConfig=new ActionBeanConfig();
            try {
                BeanUtils.copyProperties(simpleBeanConfig, sbcfg);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            this.addControllerConfig(simpleBeanConfig);
            return sbcfg;
        }
        return null;
    }

    @Override
    public void removeController(String name) {
        Object obj = this.context.removeBean(name);
        SysLogger.info("- Remove web : " + obj);
        obj = null;
    }

    void removeControllerGx(String name) {
        ControllerClassConfig controllers[] = this.controllers;
        for (int i = 0; i < controllers.length; i++) {
            ControllerClassConfig controller = controllers[i];
            if (controller == null)
                continue;
            String n = controller.getModule();
            if (n.equals(name)) {
                controller.setControllerClazz(null);
                controller.setControllerInst(null);
                controller.destroy();
                controllers[i] = null;
                this.context.removeBean(n);
                break;
            }
        }
    }

    boolean hasIn(ControllerClassConfig n, ActionBeanConfig arrays[]) {
        if (arrays == null || arrays.length == 0)
            return false;
        for (ActionBeanConfig nx : arrays) {
            if (StringUtils.equals(n.getModule(), nx.getId()))
                return true;
        }
        return false;
    }

    @Override
    public synchronized void resetController(List<ActionBeanConfig> beans) {
        if (beans == null || beans.isEmpty())
            return;
        ActionBeanConfig arrays[] = beans.toArray(new ActionBeanConfig[beans
                .size()]);

        List<ControllerClassConfig> list = new ArrayList<ControllerClassConfig>();
        ControllerClassConfig ccfgs[] = this.controllers;

        for (int i = 0; i < ccfgs.length; i++) {
            ControllerClassConfig ccfg = ccfgs[i];
            if (ccfg == null)
                continue;
            if (this.hasIn(ccfg, arrays)) {
                ccfg.setControllerClazz(null);
                ccfg.setControllerInst(null);
                ccfg.destroy();
                ccfgs[i] = null;
            } else {
                list.add(ccfg);
            }
        }

        for (ActionBeanConfig nx : arrays) {
            ControllerClassConfig controllerCfg = this.createControllerConfig(
                    nx, nx.getClazzRef());
            list.add(controllerCfg);
        }

        Collections.sort(list, new ModuleComparator());
        this.controllers = list.toArray(new ControllerClassConfig[list.size()]);
        for (ControllerClassConfig c : controllers)
            System.out.println(c);
    }
}
