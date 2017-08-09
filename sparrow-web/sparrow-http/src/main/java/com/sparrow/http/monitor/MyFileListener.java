package com.sparrow.http.monitor;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * Created by yuanzc on 2016/4/15.
 */
public class MyFileListener extends FileAlterationListenerAdaptor {

    private static Log log = LogFactory.getLog(MyFileListener.class);
    /**
     * 目录发生变化
     * @param directory 发生变化的目录
     */
    @Override
    public void onDirectoryChange(File directory) {
        super.onDirectoryChange(directory);
        log.info("文件夹改变:" +directory.getAbsolutePath());
    }

    /**
     * 新建目录
     * @param directory 被新建的目录
     */
    @Override
    public void onDirectoryCreate(File directory) {
        super.onDirectoryCreate(directory);
        log.info("新建目录:" +directory.getAbsolutePath());
    }

    /**
     * 删除目录
     * @param directory 被删除目录
     */
    @Override
    public void onDirectoryDelete(File directory) {
        super.onDirectoryDelete(directory);
        log.info("删除目录:" +directory.getAbsolutePath());
    }

    /**
     * 文件发生变化
     * @param file 发生变化的文件
     */
    @Override
    public void onFileChange(File file) {
        super.onFileChange(file);
        log.info("修改文件:" +file.getAbsolutePath());
    }

    /**
     * 新建文件
     * @param file 被新建的文件
     */
    @Override
    public void onFileCreate(File file) {
        super.onFileCreate(file);
        log.info("新建文件:"+file.getAbsolutePath());
    }

    /**
     * 删除文件
     * @param file 被删除的文件
     */
    @Override
    public void onFileDelete(File file) {
        super.onFileDelete(file);
        log.info("删除文件:" +file.getAbsolutePath());
    }

    /**
     * 检查文件开始
     * @param observer 文件观察者
     */
    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
//        log.info("开始检查...");
    }

    /**
     * 检查文件结束
     * @param observer 文件观察者
     */
    @Override
    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
//        log.info("结束检查...");
    }
}
