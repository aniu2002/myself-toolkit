package au.server.ftpserver.utils;

import java.io.*;

public class FileUtil {

    public static InputStream getInputStream(String filename, Class<?> clazz) {
        InputStream input = null;
        try {
            File file = new File(filename);
            if (file.exists())
                input = new FileInputStream(filename);

            if (input == null) {
                ClassLoader cl = clazz == null ? Thread.currentThread()
                        .getContextClassLoader() : clazz.getClassLoader();
                input = cl.getResourceAsStream(filename);
                if (input == null) {
                    cl = FileUtil.class.getClassLoader();
                    input = cl.getResourceAsStream(filename);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return input;
    }

    public static void doCopy(InputStream ins, OutputStream ops) {
        if (ins == null || ops == null)
            return;
        byte bytes[] = new byte[1024];
        try {
            int len;
            while (true) {
                len = ins.read(bytes);
                if (len == -1)
                    break;
                ops.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ins.close();
                ops.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
