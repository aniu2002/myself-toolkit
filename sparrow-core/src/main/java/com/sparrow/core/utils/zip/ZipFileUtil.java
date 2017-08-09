package com.sparrow.core.utils.zip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipFileUtil {
    /**
     * @param zipFilePath ,toDir zipFilePath是zip文件路径
     * @param toDir       toDirzip解压目录
     * @return void
     * 完成zip的解压工
     */
    public static boolean unCompress(String zipFilePath, String toDir) {
        String compress = zipFilePath;// "D:/test/jsUI.zip";
        String decompression = toDir;// "D:/test/tar/";
        File unCompressDir = null;
        ZipFile zipFile = null;
        Enumeration<? extends ZipEntry> zipEntries = null;

        try {
            unCompressDir = new File(decompression);
            zipFile = new ZipFile(compress);
            if (!unCompressDir.exists())
                unCompressDir.mkdir();
            zipEntries = zipFile.entries();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (zipEntries.hasMoreElements()) {
            ZipEntry ze = (ZipEntry) zipEntries.nextElement();
            String zename = ze.getName();
            try {
                zename = new String(zename.getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (ze.isDirectory()) {
                File file = new File(unCompressDir.getAbsolutePath() + "/"
                        + zename);
                file.mkdirs();
            } else {
                File file = null;
                InputStream is = null;
                String filePath = "";

                file = new File(unCompressDir.getAbsolutePath() + "/" + zename)
                        .getParentFile();
                if (!file.exists()) {
                    file.mkdirs();
                }
                filePath = unCompressDir.getAbsolutePath() + "/" + zename;
                try {
                    is = zipFile.getInputStream(ze);
                    copyFile(is, filePath);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
        }

        try {
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean unCompress(File zipFp, String toDir) {
        String decompression = toDir;// "D:/test/tar/";
        File unCompressDir = null;
        ZipFile zipFile = null;
        Enumeration<? extends ZipEntry> zipEntries = null;

        try {
            unCompressDir = new File(decompression);
            zipFile = new ZipFile(zipFp);
            if (!unCompressDir.exists())
                unCompressDir.mkdir();
            zipEntries = zipFile.entries();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        while (zipEntries.hasMoreElements()) {
            ZipEntry ze = (ZipEntry) zipEntries.nextElement();
            String zename = ze.getName();

            try {
                zename = new String(zename.getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (ze.isDirectory()) {
                File file = new File(unCompressDir.getAbsolutePath() + "/"
                        + zename);
                file.mkdirs();
            } else {
                File file = null;
                InputStream is = null;
                String filePath = "";

                file = new File(unCompressDir.getAbsolutePath() + "/" + zename)
                        .getParentFile();
                if (!file.exists()) {
                    file.mkdirs();
                }
                filePath = unCompressDir.getAbsolutePath() + "/" + zename;
                try {
                    is = zipFile.getInputStream(ze);
                    copyFile(is, filePath);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
        }

        try {
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * @param srcInStream zip的文件输入流
     * @param targetFile  保存解压的文件的目标路径
     *                    遍厉解压拷贝zip的文
     */
    private static void copyFile(InputStream srcInStream, String targetFile) {
        FileOutputStream fos;
        byte zeby[] = new byte[1024];// (int) ze.getSize()
        if (srcInStream == null)
            return;
        try {
            int byts = 0;
            fos = new java.io.FileOutputStream(targetFile);
            while (true) {
                byts = srcInStream.read(zeby);
                if (byts == -1)
                    break;
                fos.write(zeby, 0, byts);
            }
            srcInStream.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileName 压缩文件夹的路径
     * @param zipFile  生成目标zip文件
     *                 根据目录创建压缩 zip文件
     */
    public static boolean compressFile(String fileName, String zipFile)
            throws ZipException {
        String compress = fileName;// 要压缩的目录
        String decompression = zipFile;// zip文件路径
        ZipOutputStream zos = null;
        File file = null;
        File zipf = null;

        try {
            file = new File(compress);
            zipf = new File(decompression);
            if (!file.exists())
                return false;
            if (zipf.exists())
                zipf.delete();
            zos = new ZipOutputStream(new FileOutputStream(zipf));
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++)
                    compress(zos, files[i], "");
            } else
                compress(zos, file, "");
            zos.close();
        } catch (ZipException e) {
            // System.out.println("Exception e:"+e);
            throw e;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * @param zos     zip压缩输出流句
     * @param file    要压缩的单个文件
     * @param context 文件路径上下
     *                <p/>
     *                对文件夹路径下的文件进行递归压缩到目标zip文件
     */
    private static void compress(ZipOutputStream zos, File file, String context)
            throws ZipException {
        String fileName = "", fileEntry;
        if (file.isFile()) {
            byte buffer[] = new byte[8192];
            java.io.FileInputStream is = null;
            ZipEntry ze = null;

            fileName = make8859toGB(file.getName());
            if ("".equals(context))
                fileEntry = fileName;
            else
                fileEntry = context + File.separator + fileName;
            ze = new ZipEntry(fileEntry);

            try {
                is = new java.io.FileInputStream(file);
                int len;
                zos.putNextEntry(ze);
                while ((len = is.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                is.close();
                zos.closeEntry();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.isDirectory()) {
            File files[] = file.listFiles();
            if (files != null)
                for (int index = 0; index < files.length; index++) {
                    if (context == null || context.equals(""))
                        compress(zos, files[index], file.getName());
                    else
                        compress(zos, files[index], context + File.separator
                                + file.getName());
                }
        }
    }

    /**
     * @param str 输入字符�?未统�?��码的)
     * @return String 转换为GB2312的编�?
     */
    public static String make8859toGB(String str) {
        try {
            String str8859 = new String(str.getBytes("ISO-8859-1"), "GB2312");
            return str8859;
        } catch (UnsupportedEncodingException ioe) {
            return "";
        }
    }
}