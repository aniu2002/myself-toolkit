package com.sparrow.core.utils.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;

public class ZIPUtil {
    /**
     * Return a zipped String.
     *
     * @param str
     * @return
     */
    public static final String doZip(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }

        byte[] compressed;
        ByteArrayOutputStream out = null;
        ZipOutputStream zout = null;

        try {
            out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            // zout.setLevel(9);
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str.getBytes());
            zout.closeEntry();
            compressed = out.toByteArray();
        } catch (IOException e) {
            compressed = null;
        } finally {
            if (zout != null) {
                try {
                    zout.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        String ret = null;
        if (compressed != null) {
            ret = Base64.encodeBase64String(compressed);
        }
        // if (ret.length() > 4000) {
        // ret = null;
        // }
        return ret;
    }

    public static String encoding(String content) {
        String ret = null;
        if (content != null) {
            ret = Base64.encodeBase64String(content.getBytes());
        }
        return ret;
    }

    /**
     * Upzip a String.
     *
     * @param str
     * @return
     */
    public static final String unZip(String str) {
        String decompressed;
        byte[] compressed;
        if (str == null || str.equals(""))
            return null;

        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zin = null;
        try {
            compressed = Base64.decodeBase64(str);
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            zin = new ZipInputStream(in);
            // ZipEntry entry = zin.getNextEntry();
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            decompressed = null;
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return decompressed;
    }
}
