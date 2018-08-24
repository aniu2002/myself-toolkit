package com.sparrow.collect.backup;

import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * <B>Description</B> TODO <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 *
 * @author tanghongjun
 * @createTime 2014年6月12日 下午2:54:50
 */
public interface FullIndexBackup {

    OutputStream getOutputStream(String path) throws IOException;

    InputStream getInputStream(String path) throws IOException;

    List<String> getFilesDir(String path) throws IOException;

    boolean mkdirs(String path) throws IOException;

    void initFileSystem(String path) throws IOException;

    void clearPathDir(String path);

    boolean copyFromLocalFile(boolean isDelSrc, boolean overwrite, String src, String dst) throws IOException;

    boolean copyToLocalFile(boolean isDelSrc, String src, String dst) throws IOException;

    boolean exists(String path) throws IOException;

    boolean delDir(String path) throws IOException;

    void close() ;

    boolean copyFromLocalFile(boolean isDelSrc, boolean overwrite, String src, String dst, FileFilter fileFilter) throws IOException;

}
