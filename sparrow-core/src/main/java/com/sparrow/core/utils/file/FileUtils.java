package com.sparrow.core.utils.file;

import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.utils.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String STAR = "*";
    public static final String QUESTION = "?";
    public static final String EQUAL = "=";
    public static final char EQUAL_CHAR = '=';

    /**
     * @param filePath
     */
    public static boolean removeFile(String filePath, boolean flag) {
        try {
            if (flag)
                deleteFile(new File(filePath));
            else {
                File file = new File(filePath);
                File subfiles[];

                if (!file.isDirectory())
                    return false;

                subfiles = file.listFiles();
                for (int i = 0; i < subfiles.length; i++) {
                    deleteFile(subfiles[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * @param file
     * @throws java.io.IOException
     */
    private static void deleteFile(File file) throws IOException {
        if (!file.exists())
            throw new IOException("Not exist:" + file.getName());
        if (file.delete() == true)
            return;
        if (file.isDirectory()) {
            File subs[] = file.listFiles();

            for (int i = 0; i < subs.length; i++)
                deleteFile(subs[i]);
        }

        file.delete();
    }

    public static InputStream getFileInputStream(File file) {
        InputStream input = null;
        try {
            if (file != null && file.exists()) {
                return new FileInputStream(file);
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return input;
    }

    public static List<File> getSubFiles(String file) {
        return getSubFiles(new File(file));
    }

    public static List<File> getSubFiles(File file) {
        List<File> fileList = new ArrayList<File>();
        findFile(file, fileList);
        return fileList;
    }

    public static List<File> getSubFiles(String file, String type) {
        return getSubFiles(new File(file), type);
    }

    public static List<File> getSubFiles(File file, String type) {
        List<File> fileList = new ArrayList<File>();
        FilenameFilter filter = new FileNameSelector(type);
        findFile(file, fileList, filter);
        return fileList;
    }

    public static List<File> getSubFiles(File file, String type, String exclude) {
        List<File> fileList = new ArrayList<File>();
        FilenameFilter filter = new FileNameSelector(type, exclude);
        findFile(file, fileList, filter);
        return fileList;
    }

    public static void findFile(File file, List<File> fileList) {
        if (file == null || !file.exists())
            return;
        if (file.isFile()) {
            fileList.add(file);
        } else {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++)
                findFile(files[i], fileList);
        }
    }

    public static void findFile(File file, List<File> fileList,
                                FilenameFilter filter) {
        if (file == null || !file.exists())
            return;
        if (file.isFile()) {
            fileList.add(file);
        } else {
            File files[] = file.listFiles(filter);
            for (int i = 0; i < files.length; i++)
                findFile(files[i], fileList, filter);
        }
    }

    public static InputStream getFileInputStream(String filename) {
        if (StringUtils.isEmpty(filename))
            return null;
        InputStream input = null;
        try {
            if (filename.startsWith("classpath:")) {
                filename = filename.substring(10);
                if (filename.charAt(0) == '/')
                    filename = filename.substring(1);
                ClassLoader cl = FileUtils.class.getClassLoader();
                input = cl.getResourceAsStream(filename);
            } else {
                File file = new File(filename);
                if (file.exists()) {
                    input = new FileInputStream(filename);
                }
            }
            if (input == null && PathResolver.isRelative(filename)) {
                ClassLoader cl = FileUtils.class.getClassLoader();
                input = cl.getResourceAsStream(filename);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return input;

    }

    public static String readFileString(String fileName) {
        return readFileString(fileName, DEFAULT_ENCODING);
    }

    public static String readFileString(String fileName, String encoding) {
        return readFileString(new File(fileName), encoding);
    }

    public static String readFileString(File file) {
        return readFileString(file, DEFAULT_ENCODING);
    }

    public static String readFileString(File file, String encoding) {
        if (!file.exists()) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        BufferedReader fr = null;
        try {
            fr = new BufferedReader(new InputStreamReader(new FileInputStream(
                    file), encoding));
            String line = null;
            while ((line = fr.readLine()) != null) {
                buf.append(line);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        String backJson = buf.toString();
        if (backJson.length() <= 0) {
            return null;
        }
        return backJson;
    }

    /**
     * 根据文件名，读取文件
     *
     * @param fileName
     * @return
     * @throws java.io.IOException
     */
    public static String readStringFromFile(String fileName, String encoding) {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        BufferedReader fr = null;
        try {
            fr = new BufferedReader(new InputStreamReader(new FileInputStream(
                    file), encoding));
            String line = null;
            while ((line = fr.readLine()) != null) {
                buf.append(line);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        String backJson = buf.toString();
        if (backJson.length() <= 0) {
            return null;
        }
        return backJson;
    }

    public static String readFileStringx(String fileName, String encoding) {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        BufferedReader fr = null;
        try {
            fr = new BufferedReader(new InputStreamReader(new FileInputStream(
                    file), encoding));
            String line = null;
            while ((line = fr.readLine()) != null) {
                buf.append(line).append(LINE_SEPARATOR);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        String backJson = buf.toString();
        if (backJson.length() <= 0) {
            return null;
        }
        return backJson;
    }

    public static void writeFile(String fileName, String content, String encode) {
        File file = new File(fileName);
        if (file.exists())
            file.delete();
        else if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, encode);
            osw.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (osw != null)
                    osw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(File file, String content, String encode) {
        try {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encode);
            osw.write(content);
            osw.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Reader getReader(String fileName, String encode) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis, encode);
            return isr;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedReader getBufferedReader(String fileName,
                                                   String encode) {
        return getBufferedReader(new File(fileName), encode);
    }

    public static BufferedReader getBufferedReader(String fileName) {
        return getBufferedReader(fileName, DEFAULT_ENCODING);
    }

    public static BufferedReader getBufferedReader(File file) {
        return getBufferedReader(file, DEFAULT_ENCODING);
    }

    public static BufferedReader getBufferedReader(File file, String encode) {
        try {
            return new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), encode));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedReader getBufferedReader(InputStream ins) {
        return getBufferedReader(ins, DEFAULT_ENCODING);
    }

    public static BufferedReader getBufferedReader(InputStream ins,
                                                   String encode) {
        try {
            return new BufferedReader(new InputStreamReader(ins, encode));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedWriter getBufferedWriter(String fileName,
                                                   String encode) throws Exception {
        return getBufferedWriter(new File(fileName), encode);
    }

    public static BufferedWriter getBufferedWriter(String fileName,
                                                   String encode, boolean append) throws Exception {
        return getBufferedtWriter(new File(fileName), encode, append);
    }

    public static BufferedWriter getBufferedWriter(File file) throws Exception {
        return getBufferedWriter(file, DEFAULT_ENCODING);
    }

    public static BufferedWriter getBufferedWriter(File file, String encode)
            throws Exception {
        return getBufferedtWriter(file, encode, false);
    }

    public static BufferedWriter getBufferedtWriter(File file, String encode,
                                                    boolean append) throws Exception {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                file, append), encode));
    }

    public static Writer getWriter(String fileName, String encode) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encode);
            return osw;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearFile(String fileName) {
        if (fileName.endsWith(":/"))
            return;
        else if ("/".equals(fileName))
            return;
        clearFile(new File(fileName));
    }

    public static void clearFile(File file, Filter filter) {
        System.out.println("Delete file : " + file.getPath());
        if (!file.exists())
            return;
        if (filter != null && !filter.check(file)) {
            System.out.println("忽略文件:" + file.getName());
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fi : files)
                clearFile(fi, filter);
        }
        file.delete();
    }

    public static void clearSub(File file) {
        if (!file.exists())
            return;
        SysLogger.info("Delete file : {}", file.getPath().replace('\\', '/'));
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fi : files)
                clearFile(fi);
        }
    }

    public static void clearFile(File... files) {
        for (File f : files) {
            clearFile(f);
        }
    }

    public static void clearFile(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fi : files)
                clearFile(fi);
        }
        file.delete();
    }

    public static void createDir(File... files) {
        for (File f : files) {
            if (!f.exists())
                f.mkdirs();
        }
    }

    public static void createOrClearDir(File... files) {
        for (File f : files) {
            if (!f.exists())
                f.mkdirs();
            else
                clearFile(f);
        }
    }

    public static void createDir(String... files) {
        for (int i = 0; i < files.length; i++) {
            File f = new File(files[i]);
            if (!f.exists())
                f.mkdirs();
        }
    }

    public static InputStream getInputStream(String filename) {
        if (StringUtils.isEmpty(filename))
            return null;
        InputStream input = null;
        try {
            if (filename.startsWith("classpath:")) {
                filename = filename.substring(10);
                if (filename.charAt(0) == '/')
                    filename = filename.substring(1);
                ClassLoader cl = FileUtils.class.getClassLoader();
                input = cl.getResourceAsStream(filename);
            } else {
                File file = new File(filename);
                if (file.exists())
                    input = new FileInputStream(filename);
            }
            if (input == null && PathResolver.isRelative(filename)) {
                ClassLoader cl = FileUtils.class.getClassLoader();
                input = cl.getResourceAsStream(filename);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return input;

    }

    public static String readFileStringExt(String fileName, String encoding) {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        BufferedReader fr = null;
        try {
            fr = new BufferedReader(new InputStreamReader(new FileInputStream(
                    file), encoding));
            String line;
            while ((line = fr.readLine()) != null) {
                buf.append(line);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fr != null)
                    fr.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        String backJson = buf.toString();
        if (backJson.length() <= 0)
            return null;
        return backJson;
    }


    static class FileNameSelector implements FilenameFilter {
        private String fileType;
        private String exclude;

        FileNameSelector(String type) {
            fileType = ((type == null) ? "" : type.toLowerCase());
        }

        FileNameSelector(String type, String exclude) {
            this.fileType = ((type == null) ? "" : type.toLowerCase());
            this.exclude = ((exclude == null) ? "" : exclude.toLowerCase());
        }

        public boolean accept(File dir, String name) {
            if (exclude != null)
                return name.toLowerCase().endsWith(fileType)
                        && !name.toLowerCase().startsWith(exclude);
            else
                return name.toLowerCase().endsWith(fileType);
        }
    }

    public static abstract class Filter {
        public abstract boolean check(File file);
    }
}
