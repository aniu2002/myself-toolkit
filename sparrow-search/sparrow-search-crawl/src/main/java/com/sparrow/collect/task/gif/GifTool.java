package com.sparrow.collect.task.gif;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Administrator on 2017/8/28 0028.
 */
public class GifTool {
    private GifTool() {

    }

    public static void main(String args[]) {
        File dir = new File("D:\\fanhao\\extract\\img");
        for (File f : dir.listFiles()) {
            System.out.println(f.getPath());
            if (!isImage(f.getPath())) {
                f.delete();
            }
        }
    }

    public static boolean isImage(String srcFileName) {
        FileInputStream imgFile;
        byte[] b = new byte[10];
        int l;
        try {
            imgFile = new FileInputStream(srcFileName);
            l = imgFile.read(b);
            imgFile.close();
        } catch (Exception e) {
            return false;
        }
        if (l == 10) {
            byte b0 = b[0];
            byte b1 = b[1];
            byte b2 = b[2];
            byte b3 = b[3];
            byte b6 = b[6];
            byte b7 = b[7];
            byte b8 = b[8];
            byte b9 = b[9];
            if (b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') {
                return true;
            } else if (b1 == (byte) 'P' && b2 == (byte) 'N' && b3 == (byte) 'G') {
                return true;
            } else if (b6 == (byte) 'J' && b7 == (byte) 'F' && b8 == (byte) 'I' && b9 == (byte) 'F') {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
