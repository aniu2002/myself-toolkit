package com.sparrow.http.monitor;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by yuanzc on 2016/4/15.
 */
public class Starter {
    private static Log log = LogFactory.getLog(Starter.class);

    /**
     * 测试文件系统监测
     *
     * @param args
     */
    public static void main(String[] args) {
        long interval = 1000;

        final String path = "/home/leocook/test";
        FileAlterationObserver observer = null;

        try {
            observer = new FileAlterationObserver(path, null, null);
            //添加监听器
            observer.addListener(new MyFileListener());

            FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
            monitor.start();
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }
}
