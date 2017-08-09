package com.sparrow.file.pool;

import com.sparrow.file.tool.Files;
import com.sparrow.file.tool.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * 这是个最简单的实现，仅仅在一个目录里创建文件，适用于文件数量不多的临时文件池
 *
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SimpleFilePool implements FilePool {

    private static final Logger log = LoggerFactory.getLogger(SimpleFilePool.class);

    private File home;

    /**
     * 文件最大 ID 数，到了就回滚
     */
    private long max;

    private long current;

    public SimpleFilePool(String homePath, long max) {
        if (log.isInfoEnabled()) {
            log.info("Init simple-file-pool by: {} [{}]", homePath, max);
        }
        this.max = max;
        this.home = Files.createDirIfNoExists(homePath);
        this.current = 0;
    }

    private File _F(long fId, String suffix) {
        return new File(home.getAbsolutePath() + "/" + fId + (null == suffix ? "" : suffix));
    }

    public synchronized boolean hasFile(long fId, String suffix) {
        return _F(fId, suffix).exists();
    }

    public long current() {
        return current;
    }

    public synchronized File removeFile(long fId, String suffix) {
        File f = _F(fId, suffix);
        if (f.exists())
            Files.deleteFile(f);
        return f;
    }

    public synchronized File createFile(String suffix) {
        File f = _F(current++, suffix);
        if (current > max)
            current = 0;
        if (!f.exists())
            try {
                Files.createNewFile(f);
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
        return f;
    }

    public long getFileId(File f) {
        String nm = Files.getMajorName(f);
        try {
            return Long.parseLong(nm);
        }
        catch (NumberFormatException e) {}
        return -1;
    }

    public File getFile(long fId, String suffix) {
        File re = _F(fId, suffix);
        if (re.exists())
            return re;
        return null;
    }

    public synchronized File returnFile(long fId, String suffix) {
        File re = _F(fId, suffix);
        if (!re.exists())
            try {
                Files.createNewFile(re);
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
        return re;
    }

    public synchronized boolean hasDir(long fId) {
        return _F(fId, null).exists();
    }

    public synchronized File removeDir(long fId) {
        File f = _F(fId, null);
        Files.deleteDir(f);
        return f;
    }

    public synchronized File createDir() {
        File f = _F(current++, null);
        if (current > max)
            current = 0;
        if (f.exists())
            Files.clearDir(f);
        else
            Files.makeDir(f);
        return f;
    }

    public File getDir(long fId) {
        File re = _F(fId, null);
        if (re.exists())
            return re;
        return null;
    }

    public synchronized File returnDir(long fId) {
        File re = _F(fId, null);
        if (!re.exists())
            Files.makeDir(re);
        return re;
    }

    public synchronized void clear() {
        Files.clearDir(home);
    }

}
