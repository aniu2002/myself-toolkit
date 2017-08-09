/**
 * Project Name:http-server
 * File Name:ActionControllerFactory.java
 * Package Name:com.sparrow.core.http.controller
 * Date:2014-1-3下午1:19:38
 */

package com.sparrow.server.controller;

import com.sparrow.core.utils.ReflectHelper;

/**
 * ClassName:ActionControllerFactory <br/>
 * Date: 2014-1-3 下午1:19:38 <br/>
 *
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class ActionControllerFactory {
    private static final String defaultClazz;
    private static final Class<ActionController> destClass = ActionController.class;

    static {
        defaultClazz = System.getProperty("web.controller",
                "com.sparrow.server.web.controller.ControllerFacade");
        // defaultClazz = ServiceLoadUtil.getServiceName(ActionController.class,
        // "com.sparrow.core.web.Controller");
    }

    public static ActionController configureActionController(String configPath) {
        ActionController controller = getActionController();
        if (controller != null)
            controller.setBeanConfig(configPath);
        return controller;
    }

    public static ActionController getActionController() {
        try {
            Class<?> clas = ReflectHelper.classForName(defaultClazz);
            if (destClass.isAssignableFrom(clas))
                return destClass.cast(clas.newInstance());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
