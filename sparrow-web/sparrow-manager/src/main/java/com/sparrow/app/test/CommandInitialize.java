package com.sparrow.app.test;


import com.sparrow.http.command.Command;
import com.sparrow.server.config.BeanContext;
import com.sparrow.server.config.ParameterWatcher;

/**
 * Created by Administrator on 2015/6/2 0002.
 */
public class CommandInitialize {
    public static void main(String args[]) {
        ParameterWatcher watcher = new ParameterWatcher<Command>() {
            @Override
            public void watch(String parameter, Command bean) {
                System.out.println(" path : " + parameter + " , " + bean.getClass().getName());
            }

            @Override
            public Class<Command> accept() {
                return Command.class;
            }
        };
        BeanContext beanContext = new BeanContext("classpath:eggs/beanConfig.xml", watcher);
    }
}
