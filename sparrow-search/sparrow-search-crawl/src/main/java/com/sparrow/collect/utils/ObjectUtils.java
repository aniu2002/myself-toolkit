package com.sparrow.collect.utils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Administrator on 2017/8/28 0028.
 */
public class ObjectUtils {
    private ObjectUtils() {

    }

    public static void write(Object object, File file) {
        try {
            write(object, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void write(Object object, OutputStream out) {
        try {
            ObjectOutputStream objOut = new ObjectOutputStream(new GZIPOutputStream(out));
            objOut.writeObject(object);
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object read(File file) {
        try {
            return read(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object read(InputStream in) {
        Object object = null;
        try {
            ObjectInputStream objIn = new ObjectInputStream(new GZIPInputStream(in));
            object = objIn.readObject();
            objIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
}
