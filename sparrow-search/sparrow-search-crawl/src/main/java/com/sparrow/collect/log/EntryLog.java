package com.sparrow.collect.log;

import com.sparrow.collect.crawler.data.EntryData;
import com.sparrow.collect.utils.ConvertUtils;
import com.sparrow.collect.utils.FileIOUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Project Name: test-parent
 * Package Name: com.sparrow.collect.website.log
 * Author : YZC
 * Date: 2016/12/9
 * Time: 12:58
 */
public class EntryLog {
    private static final char splitChar = '~';
    private final Lock lock = new ReentrantLock();
    private PrintWriter printWriter;
    private File writeFile;
    private File readFile;

    public EntryLog(String dir) {
        try {
            File f = new File(dir);
            if (!f.exists())
                f.mkdirs();
            this.writeFile = new File(f, "crawl_entry_1.log");
            this.readFile = new File(f, "crawl_entry_2.log");
            this.printWriter = new PrintWriter(this.writeFile, FileIOUtil.DEFAULT_ENCODING);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void switchFileLog() {
        lock.lock();
        if (this.printWriter != null) {
            this.printWriter.close();
        }
        File tmp = this.writeFile;
        this.writeFile = this.readFile;
        this.readFile = tmp;
        try {
            this.printWriter = new PrintWriter(this.writeFile, FileIOUtil.DEFAULT_ENCODING);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }

    public void writeUrl(EntryData entryData) {
        lock.lock();
        this.printWriter.print(entryData.getDeep());
        this.printWriter.print(splitChar);
        if (entryData.getTitle() != null)
            this.printWriter.print(entryData.getTitle());
        this.printWriter.print(splitChar);
        this.printWriter.println(entryData.getUrl());
        lock.unlock();
    }

    void writeLine(String line) {
        lock.lock();
        this.printWriter.println(line);
        lock.unlock();
    }

    static EntryData parseLine(String str) {
        int idxFirst = str.indexOf(splitChar);
        int idxLast = str.lastIndexOf(splitChar);
        EntryData data = new EntryData();
        data.setDeep(ConvertUtils.toInt(str.substring(0, idxFirst), 1));
        data.setTitle(str.substring(idxFirst + 1, idxLast));
        data.setUrl(str.substring(idxLast + 1));
        return data;
    }

    public int readUrl(UrlReadCallback callback) {
        BufferedReader reader = null;
        int rows = 0;
        try {
            this.switchFileLog();
            reader = new BufferedReader(new FileReader(this.readFile));
            String line;
            boolean suc;
            while ((line = reader.readLine()) != null) {
                suc = callback.handle(parseLine(line));
                if (suc)
                    rows++;
                else
                    this.writeLine(line);
            }
            this.readFile.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return rows;
    }

    public void close(boolean deleteFile) {
        if (this.printWriter != null) {
            this.printWriter.close();
            this.printWriter = null;
        }
        if (deleteFile) {
            this.writeFile.delete();
            this.writeFile = null;
        }
    }

    public void close() {
        this.close(true);
    }
}
