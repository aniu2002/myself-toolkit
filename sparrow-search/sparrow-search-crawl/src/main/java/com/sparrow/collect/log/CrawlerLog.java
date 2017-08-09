package com.sparrow.collect.log;

import com.sparrow.collect.utils.FileIOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Project Name: test-parent
 * Package Name: com.sparrow.collect.website.log
 * Author : YZC
 * Date: 2016/12/9
 * Time: 12:58
 */
public class CrawlerLog {
    private PrintWriter printWriter;
    private final Lock lock = new ReentrantLock();

    public CrawlerLog(String dir) {
        try {
            File f = new File(dir);
            if (!f.exists())
                f.mkdirs();
            this.printWriter = new PrintWriter(new File(f, "crawl_err.log"), FileIOUtil.DEFAULT_ENCODING);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(String type, String... contents) {
        lock.lock();
        this.printWriter.print(type);
        for (String content : contents) {
            this.printWriter.print(" - ");
            this.printWriter.print(content);
        }
        this.printWriter.println();
        lock.unlock();
    }

    public void close() {
        this.printWriter.close();
        this.printWriter = null;
    }
}
