package com.sparrow.collect.task;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class TaskMain {
    public static final String basePackage = "com.sparrow.collect.task.";

    public static final Map<String, String> TASK_MAP = new HashMap<String, String>();

    static {
        TASK_MAP.put("configure", "com.sparrow.collect.task.special.ConfigureTask");
        TASK_MAP.put("multi", "com.sparrow.collect.task.special.MultiEntryCrawlerTask");
        TASK_MAP.put("scope", "com.sparrow.collect.task.special.ScopeCrawlerTask");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        // start("crawler");
        long time = System.currentTimeMillis();
        start("site");
        time = System.currentTimeMillis() - time;
        System.out.println(String.format("cost : %s s", time / 1000));
    }

    @SuppressWarnings("unchecked")
    public static void start(String task) {
        String taskClass = TASK_MAP.get(task);
        if (StringUtils.isEmpty(taskClass))
            taskClass = basePackage + task + "."
                    + Character.toUpperCase(task.charAt(0)) + task.substring(1)
                    + "Task";
        try {
            Class<AbstractTask> clz = (Class<AbstractTask>) Class.forName(taskClass);
            AbstractTask tsk = clz.newInstance();
            tsk.start();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
