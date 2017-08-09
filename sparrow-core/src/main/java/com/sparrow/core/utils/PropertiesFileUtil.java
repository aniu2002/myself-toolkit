package com.sparrow.core.utils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sparrow.core.utils.file.FileUtils;

/**
 * @author Yzc
 * @version 3.0
 * @date 2009-5-26
 */
public class PropertiesFileUtil {
    /**
     * @param filename
     * @return
     * @throws java.io.IOException
     */
    public static Properties getPropertiesEl(String filename) {
        if (StringUtils.isEmpty(filename))
            throw new RuntimeException("配置文件为空");
        Properties p = new Properties();
        try {
            InputStream input = getPropertyFileInputStream(filename);
            if (input != null)
                p.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static Properties getProps(String content) {
        if (StringUtils.isEmpty(content))
            throw new RuntimeException("配置内容为空");
        Properties p = new Properties();
        try {
            InputStream input = new ByteArrayInputStream(content.getBytes());
            if (input != null)
                p.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * 合并配置文件
     *
     * @param fileNames
     * @return
     * @author YZC
     */
    public static Properties mergePropertyFiles(String[] fileNames) {
        if (fileNames == null || fileNames.length == 0)
            return null;
        Properties p = new Properties();
        InputStream input = null;
        for (String fileName : fileNames) {
            if (StringUtils.isEmpty(fileName))
                continue;
            try {
                input = getPropertyFileInputStream(fileName);
                if (input == null)
                    continue;
                p.load(input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null)
                        input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return p;
    }

    /**
     * 合并配置文件
     *
     * @param fileNames
     * @return
     * @author YZC
     */
    public static Properties mergePropertyFiles(List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty())
            return null;
        Properties p = null;

        p = new Properties();
        InputStream input = null;
        for (String fileName : fileNames) {
            if (StringUtils.isEmpty(fileName))
                continue;
            try {
                input = getPropertyFileInputStream(fileName);
                if (input == null)
                    continue;
                p.load(input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(input!=null ) try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return p;
    }

    public static File getPropertyFile(String filename) {
        if (StringUtils.isEmpty(filename))
            return null;
        File file = null;
        String fileStr;
        if (filename.startsWith("classpath:")) {
            ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
            URL url = cl.getResource(filename.substring(10));
            if (url != null) {
                fileStr = url.getFile();
                file = new File(fileStr);
            }
        } else if (PathResolver.isRelative(filename)) {
            file = new File(filename);
            if (!file.exists()) {
                ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
                URL url = cl.getResource(filename);
                if (url != null) {
                    fileStr = url.getFile();
                    file = new File(fileStr);
                }
            }
        } else
            file = new File(filename);

        return file;
    }

    /**
     * 获取properties文件的输入流
     *
     * @param filename
     * @return
     * @author YZC
     */
    static InputStream getPropertyFileInputStream(String filename) {
        if (StringUtils.isEmpty(filename))
            return null;
        InputStream input = null;
        try {
            if (filename.startsWith("classpath:")) {
                ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
                input = cl.getResourceAsStream(filename.substring(10));
            } else if (PathResolver.isRelative(filename)) {
                File file = new File(filename);
                if (file.exists()) {
                    input = new FileInputStream(filename);
                } else {
                    ClassLoader cl = PropertiesFileUtil.class.getClassLoader();
                    input = cl.getResourceAsStream(filename);
                }
            } else {
                File file = new File(filename);
                if (file.exists()) {
                    input = new FileInputStream(filename);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return input;
    }

    /**
     * 将unicode 字符串
     *
     * @param str 待转字符串
     * @return 普通字符串
     */
    public static String revert(String str) {
        if (str != null && str.trim().length() > 0) {
            String un = str.trim();
            StringBuffer sb = new StringBuffer();
            int idx = un.indexOf("\\u");
            while (idx >= 0) {
                if (idx > 0) {
                    sb.append(un.substring(0, idx));
                }
                String hex = un.substring(idx + 2, idx + 2 + 4);
                sb.append((char) Integer.parseInt(hex, 16));
                un = un.substring(idx + 2 + 4);
                idx = un.indexOf("\\u");
            }
            sb.append(un);
            return sb.toString();
        }
        return str;
    }

    public static String gbEncoding(final String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            str += "\\u" + Integer.toHexString(ch);
        }
        return str;
    }

    public static List<PropItem> readPropItems(String path) {
        InputStream ins = getPropertyFileInputStream(path);
        if (ins == null)
            return null;
        BufferedReader reader = FileUtils.getBufferedReader(ins);
        if (reader != null) {
            String line;
            try {
                int idx;
                List<PropItem> lt = new ArrayList<PropItem>();
                while ((line = reader.readLine()) != null) {
                    if (line.length() < 1 || line.charAt(0) == '#')
                        continue;
                    idx = line.indexOf(FileUtils.EQUAL_CHAR);
                    if (idx != -1) {
                        String tm = revert(line.substring(idx + 1));
                        lt.add(new PropItem(line.substring(0, idx), tm));
                    } else
                        lt.add(new PropItem(line, ""));
                }
                return lt;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param filename
     * @return
     * @throws java.io.IOException
     */
    public static Properties getProperties(String filename) {
        Properties p = null;
        InputStream input = null;
        File file = null;
        try {
            file = new File(filename);
            if (file.exists()) {
                input = new FileInputStream(filename);
            } else {
                return null;
            }
            p = new Properties();
            p.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * @return
     * @throws java.io.IOException
     */
    public static Properties getProperties(File file) {
        Properties p = null;
        InputStream input = null;
        try {
            if (file.exists()) {
                input = new FileInputStream(file);
            } else
                return null;

            p = new Properties();
            p.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return p;
    }

    /**
     * @param properties
     * @param fileName
     */
    public static void writeProperties(Properties properties, String fileName) {
        if (fileName == null || fileName.equals(""))
            return;
        File file = null;
        file = new File(fileName);
        writeProperties(properties, file);
    }

    /**
     * @param properties
     * @param file
     */
    public static void writeProperties(Properties properties, File file) {
        try {
            OutputStream fos = new FileOutputStream(file);
            writeProperties(properties, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param properties
     * @param outStream
     */
    public static void writeProperties(Properties properties,
                                       OutputStream outStream) {
        if (properties == null || outStream == null) {
            return;
        }
        try {
            properties.store(outStream, "utils.PropertiesFileUtil");
            outStream.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param inMap
     * @param fileName
     */
    public static void writeProperties(Map<String, String> inMap,
                                       String fileName) {
        writeProperties(inMap, fileName);
    }

    /**
     * @param inMap
     * @param file
     */
    public static void writeProperties(Map<String, String> inMap, File file) {
        if (inMap != null && !inMap.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = inMap.entrySet()
                    .iterator();
            Map.Entry<String, String> entry;
            StringBuilder sb=new StringBuilder();
            boolean first=true;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if(first)
                    first=false;
                else
                   sb.append(FileUtils.LINE_SEPARATOR);
                sb.append(entry.getKey()).append('=').append(entry.getValue());
            }
            FileUtils.writeFile(file,sb.toString(),FileUtils.DEFAULT_ENCODING);
        }
    }

}

class PropItem {
    String k;
    String v;

    public PropItem(String k, String v) {
        this.k = k;
        this.v = v;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

}
