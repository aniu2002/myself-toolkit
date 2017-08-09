package com.sparrow.tools.shell;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by Administrator on 2016/3/12 0012.
 */
public class Test {
    public static void main(String args[]) {
        //取得根目录路径
        String rootPath = Test.class.getClassLoader().getResource(".").getFile().toString();
        System.out.println(rootPath);
        //当前目录路径
        String currentPath1 = Test.class.getResource(".").getFile().toString();
        System.out.println(currentPath1);
        String currentPath2 = Test.class.getResource("").getFile().toString();
        System.out.println(currentPath2);
        //当前目录的上级目录路径
        String parentPath = Test.class.getResource("../").getFile().toString();
        System.out.println(parentPath);

        System.out.println(System.getProperty("user.dir"));
        Properties properties = System.getProperties();
        Enumeration<?> p = properties.propertyNames();
        while (p.hasMoreElements()) {
            Object o = p.nextElement();
            Object v = properties.get(o);
            System.out.println(o + " = " + v);
        }
    }
}
