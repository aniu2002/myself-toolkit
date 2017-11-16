package com.sparrow.collect.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileIOUtil {
    public static final String DEFAULT_ENCODING = "UTF-8";

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;

    }

    public static String readFile(String fileName) {
        return readFile(fileName, DEFAULT_ENCODING);
    }

    public static String readFile(File file) {
        return readFile(file, DEFAULT_ENCODING);
    }

    public static String readFile(String fileName, String encoding) {
        return readFile(new File(fileName), encoding);
    }

    public static BufferedReader getBufferedReader(InputStream ins,
                                                   String encode) {
        try {
            if (ins == null)
                throw new RuntimeException("file input stream is null ! ");
            return new BufferedReader(new InputStreamReader(ins, encode));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> readLines(String fileName) {
        List<String> lines = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = getBufferedReader(getFileInputStream(fileName), DEFAULT_ENCODING);
            while (br.ready())
                lines.add(br.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    public static String readString(String fileName) {
        BufferedReader fr = null;
        try {
            fr = getBufferedReader(getFileInputStream(fileName), DEFAULT_ENCODING);
            if (fr == null) {
                return null;
            }
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = fr.readLine()) != null) {
                buf.append(line);
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

    /**
     * 根据文件名，读取文件
     *
     * @return
     * @throws java.io.IOException
     */
    public static String readFile(File file, String encoding) {
        BufferedReader fr = null;
        try {
            fr = getBufferedReader(file, encoding);
            if (fr == null) {
                return null;
            }
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = fr.readLine()) != null) {
                buf.append(line);
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

    /**
     * @param fileName
     * @throws java.io.IOException
     */
    public static void writeFile(String fileName, String content) {
        writeFile(new File(fileName), content, DEFAULT_ENCODING);
    }

    public static void writeFile(String fileName, String content, String encode) {
        writeFile(new File(fileName), content, encode);
    }


    public static void writeFile(File file, String content) {
        writeFile(file, content, DEFAULT_ENCODING);
    }

    public static void writeFile(File file, String content, String encode) {
        if (StringUtils.isEmpty(content))
            return;
        Writer out = null;
        try {
            out = getWriter(file, encode);
            out.write(content);
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

    public static BufferedWriter getWriter(String fileName, String encode)
            throws Exception {
        return getWriter(new File(fileName), encode);
    }

    public static BufferedWriter getWriter(String fileName, String encode,
                                           boolean append) throws Exception {
        return getWriter(new File(fileName), encode, append);
    }

    public static BufferedWriter getWriter(File file) throws Exception {
        return getWriter(file, "utf-8");
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
            for (File fi : files) {
                clearFile(fi);
            }
        } else {
            file.delete();
        }
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
