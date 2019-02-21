package com.sparrow.collect.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yzc on 2018/4/4.
 */
@Slf4j
public class FileIOUtil {
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String OS_ENCODING = Charset.defaultCharset()
            .toString();

    public static Map<String, String> fileToMap(String source, char split) {
        Map<String, String> map = streamToMap(getFileInputStream(source), split);
        if (map == null)
            map = Collections.emptyMap();
        return map;
    }

    public static Map<String, String> streamToMap(InputStream inputStream, char split) {
        if (inputStream == null)
            return MapUtils.EMPTY_MAP;
        BufferedReader reader = getBufferedReader(inputStream);
        if (reader == null)
            return MapUtils.EMPTY_MAP;
        IOUtils.closeQuietly(reader);
        String line;
        Map<String, String> map = new HashMap();
        try {
            while ((line = reader.readLine()) != null) {
                int idx = line.indexOf(split);
                if (idx == -1)
                    continue;
                map.put(line.substring(0, idx), line.substring(idx + 1));
            }
        } catch (IOException e) {
            log.error("Load file for IOException", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error("Close file error", e);
            }
        }
        return map;
    }

    public static InputStream getFileInputStream(String filename) {
        if (StringUtils.isEmpty(filename))
            return null;
        InputStream input = null;
        try {
            if (filename.startsWith("classpath:")) {
                ClassLoader cl = FileIOUtil.class.getClassLoader();
                input = cl.getResourceAsStream(filename.substring(10));
            } else {
                File file = new File(filename);
                if (file.exists())
                    input = new FileInputStream(filename);
            }
            if (input == null && PathResolver.isRelative(filename)) {
                ClassLoader cl = FileIOUtil.class.getClassLoader();
                input = cl.getResourceAsStream(filename);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return input;
    }

    public static String readString(String fileName) {
        return readString(getBufferedReader(getFileInputStream(fileName), DEFAULT_ENCODING));
    }

    public static String readFile(String fileName, String encoding) {
        return readFile(new File(fileName), encoding);
    }

    /**
     * 根据文件名，读取文件
     *
     * @return
     * @throws IOException
     */
    public static String readFile(File file, String encoding) {
        return readString(getBufferedReader(file, encoding));
    }

    public static String readFile(File file) {
        return readString(getBufferedReader(file, DEFAULT_ENCODING));
    }

    /**
     * 根据文件名，读取文件
     *
     * @return
     * @throws IOException
     */
    public static String readString(BufferedReader fr) {
        try {
            if (fr == null)
                return null;
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = fr.readLine()) != null) {
                buf.append(line).append(LINE_SEPARATOR);
            }
            return buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static BufferedReader getBufferedReader(InputStream ins) {
        return getBufferedReader(ins, DEFAULT_ENCODING);
    }

    public static BufferedReader getBufferedReader(InputStream ins, String encode) {
        try {
            return new BufferedReader(new InputStreamReader(ins, encode));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param fileName
     * @throws IOException
     */
    public static void writeFile(String fileName, String content) {
        writeFile(new File(fileName), content, DEFAULT_ENCODING);
    }

    public static void writeFile(String fileName, String content, String encode) {
        writeFile(new File(fileName), content, encode);
    }

    public static void writeFile(File file, String content, String encode) {
        if (StringUtils.isEmpty(content))
            return;
        Writer out = null;
        try {
            out = getWriter(file, encode);
            out.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static BufferedReader getBufferedReader(String fileName,
                                                   String encode) {
        return getBufferedReader(new File(fileName), encode);
    }

    public static BufferedReader getBufferedReader(File file, String encode) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return new BufferedReader(new InputStreamReader(fis, encode));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return null;
    }

    public static BufferedWriter getWriter(String fileName, String encode)
            throws Exception {
        return getWriter(new File(fileName), encode);
    }

    public static BufferedWriter getWriter(String fileName, String encode,
                                           boolean append) throws Exception {
        return getWriter(new File(fileName), encode, append);
    }

    public static BufferedWriter getWriter(File file) throws Exception {
        return getWriter(file, DEFAULT_ENCODING);
    }

    public static BufferedWriter getWriter(File file, String encode)
            throws Exception {
        return getWriter(file, encode, false);
    }

    public static BufferedWriter getWriter(File file, String encode,
                                           boolean append) throws Exception {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                file, append), encode));
    }

    public static PrintWriter getPrinter(String file, String encode)
            throws Exception {
        return getPrinter(new File(file), encode);
    }

    public static PrintWriter getPrinter(File file, String encode)
            throws Exception {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        return new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), encode));
    }

    public static BufferedInputStream getInStream(String fileName)
            throws FileNotFoundException {
        return getInStream(new File(fileName));
    }

    public static BufferedInputStream getInStream(File file)
            throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    public static BufferedOutputStream getOutStream(String fileName)
            throws FileNotFoundException {
        return getOutStream(new File(fileName));
    }

    public static BufferedOutputStream getOutStream(File file)
            throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(file));
    }

    public static void cleanFile(String file) {
        clearFile(file);
    }

    public static void cleanFile(File file) {
        clearFile(file);
    }

    public static void cleanFile(String file, FilenameFilter filter) {
        cleanFile(new File(file), filter);
    }

    public static void cleanFile(File file, FilenameFilter filter) {
        if (file.isDirectory()) {
            File files[] = file.listFiles(filter);
            for (File f : files) {
                f.delete();
            }
        }
    }

    public static void clearFile(String fileName) {
        clearFile(new File(fileName));
    }

    public static void clearFile(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fi : files)
                clearFile(fi);
        } else
            file.delete();
    }

    public static void clearFile(File... files) {
        for (File f : files) {
            clearFile(f);
        }
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
}