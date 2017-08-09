package com.sparrow.tools.utils;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过jaxb2做xml->obj和obj->xml的映射
 */
public class JAXB2Util {
    public static final String DEFAULT_ENCODING = "UTF-8";
    /**
     * 存储创建的unmarshaller的context，避免重复创建带来的系统开销
     */
    @SuppressWarnings("unchecked")
    private static Map unmarshallerCache = new HashMap();

    /**
     * 私有构造函数,避免被其他类创建实例
     */
    private JAXB2Util() {

    }

    /**
     * @param obj     实例化对象
     * @param xmlFile xml文件路径
     */
    public static boolean translateObject2XML(Object obj, String xmlFile) {
        return translateObject2XML(obj, xmlFile, DEFAULT_ENCODING);
    }

    public static boolean translateObject2XML(Object obj, File xmlFile) {
        return translateObject2XML(obj, xmlFile, DEFAULT_ENCODING);
    }


    /**
     * 任何的Reader/Writer都能进行缓冲以提高字符输入输出的速度，
     * 同样任何OutputStream/InputStream都能进行缓冲以提高字节IO的速度
     *
     * @param obj      实例化对象
     * @param xmlFile  xml文件路径
     * @param encoding xml文件保存编码格式
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.IOException
     */
    public static boolean translateObject2XML(Object obj, String xmlFile,
                                              String encoding) {
        return translateObject2XML(obj, new File(xmlFile), encoding);
    }

    /**
     * 任何的Reader/Writer都能进行缓冲以提高字符输入输出的速度，
     * 同样任何OutputStream/InputStream都能进行缓冲以提高字节IO的速度
     *
     * @param obj      实例化对象
     * @param xmlFile  xml文件路径
     * @param encoding xml文件保存编码格式
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.IOException
     */
    public static boolean translateObject2XML(Object obj, File xmlFile,
                                              String encoding) {
        if (xmlFile == null)
            return false;
        if (!xmlFile.exists())
            try {
                xmlFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(xmlFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return translateObject2XML(obj, fos, encoding);
    }

    /**
     * @param obj      实例化对象
     * @param os       输出流
     * @param encoding xml文本的编码格式
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.IOException
     */
    public static boolean translateObject2XML(Object obj, OutputStream os,
                                              String encoding) {
        JAXBContext context;
        Writer writer = null;
        /*
         * BufferedWriter在缺省条件下的�?能提升是非常显著�?
		 * 除了BufferedWriter外，BufferedOutputStream也具有相同的品质�?
		 * 对于输入就是BufferedReader和BufferedInputStream
		 */
        try {
            context = JAXBContext.newInstance(obj.getClass());
            Marshaller m = context.createMarshaller();
            writer = new BufferedWriter(new OutputStreamWriter(os, encoding));
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(obj, writer);
        } catch (JAXBException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null)
                    writer.close();
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 将xml转换成obj对象
     *
     * @param xmlFile  xml文件路径
     * @param objClass 类的class
     * @return
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.FileNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static <T> T translateXML2Object(String xmlFile, Class<T> objClass)
            throws FileNotFoundException {
        return translateXML2Object(xmlFile, objClass, DEFAULT_ENCODING);
    }

    /**
     * 将xml转换成obj对象
     *
     * @param xmlFile  xml文件地址
     * @param objClass object的class
     * @param encoding 文本读取的编码方�?
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     * @throws javax.xml.bind.JAXBException
     */
    @SuppressWarnings("unchecked")
    public static <T> T translateXML2Object(String xmlFile, Class<T> objClass,
                                            String encoding) throws FileNotFoundException {
        InputStream is = null;
        File file = new File(xmlFile);
        if (!file.exists()) {
            is = JAXB2Util.class.getClassLoader().getResourceAsStream(xmlFile);
        } else
            is = new FileInputStream(xmlFile);
        return translateXML2Object(is, objClass, encoding);
    }

    /**
     * 将xml转换成obj对象
     *
     * @param xmlFile  xml文件地址
     * @param objClass object的class
     * @param encoding 文本读取的编码方
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     * @throws javax.xml.bind.JAXBException
     */
    @SuppressWarnings("unchecked")
    public static <T> T translateXML2Object(File xmlFile, Class<T> objClass,
                                            String encoding) throws FileNotFoundException {
        InputStream is = null;
        is = new FileInputStream(xmlFile);
        return translateXML2Object(is, objClass, encoding);
    }

    public static <T> T translateXML2Object(File xmlFile, Class<T> objClass) throws FileNotFoundException {
        return translateXML2Object(xmlFile, objClass, DEFAULT_ENCODING);
    }

    /**
     * 将xml转换成obj对象
     *
     * @param instream 输入�?
     * @param objClass object对象的class
     * @param encoding 编码方式
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T translateXML2Object(InputStream instream,
                                            Class<T> objClass, String encoding) {
        Reader fr = null;
        T ob = null;
        try {
            fr = new BufferedReader(new InputStreamReader(instream, encoding));
            JAXBContext context = JAXBContext.newInstance(objClass);
            Unmarshaller um = context.createUnmarshaller();
            ob = objClass.cast(um.unmarshal(fr));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null)
                    fr.close();
                if (instream != null)
                    instream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ob;
    }

    /**
     * 将xml转换成obj对象
     *
     * @param file     输入文件
     * @param objClass object对象的class
     * @param encoding 编码方式
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object easyXML2Object(File file, Class objClass,
                                        String encoding) {
        Reader fr = null;
        Object ob = null;
        InputStream ins = null;
        String clName = objClass.getName();
        try {
            ins = new FileInputStream(file);
            fr = new BufferedReader(new InputStreamReader(ins, encoding));
            Unmarshaller um = null;
            um = (Unmarshaller) unmarshallerCache.get(clName);
            if (um == null) {
                JAXBContext context = JAXBContext.newInstance(objClass);
                um = context.createUnmarshaller();
                unmarshallerCache.put(clName, um);
            }
            ob = um.unmarshal(fr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null)
                    fr.close();
                if (ins != null)
                    ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ob;
    }

    /**
     * 通过内存输出流将xml数据映射成xmlString
     *
     * @param obj xml序列化对象
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.IOException
     */
    public static String obj2XMLString(Object obj) throws JAXBException,
            IOException {
        return obj2XMLString(obj, DEFAULT_ENCODING);
    }

    /**
     * 将xml数据映射成xmlString
     *
     * @param obj      实列
     * @param encoding
     * @return
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.IOException
     */
    public static String obj2XMLString(Object obj, String encoding)
            throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(obj.getClass());
        Marshaller m = context.createMarshaller();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Writer writer = new BufferedWriter(new OutputStreamWriter(os, encoding));
        // 格式
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(obj, writer);

        return os.toString(encoding);
    }


    public static void main(String args[]) throws JAXBException, IOException {
    }
}
