package com.sparrow.collect.persist.format;

/**
 * Created by Administrator on 2016/12/2.
 */
public class FileRowFormat implements DataFormat<String> {

    @Override
    public Object[] format(String s) {
        int idx = s.indexOf('[');
        String t = s.substring(0, idx).trim();
        String sub = s.substring(idx + 1);
        idx = sub.indexOf(']');
        String images = sub.substring(0, idx);
        idx = images.indexOf(',');
        if (idx != -1)
            images = images.substring(0, idx);
        if (images.length() > 255) {
            System.err.println(t + " - " + images.length());
        }
        images = images.replace('\\', '/');
        sub = sub.substring(idx + 1);
        idx = sub.indexOf('-');
        String refUrl = sub.substring(idx + 1).trim();
        String gifUrl = refUrl;
        idx = sub.indexOf('>');
        if (idx != -1) {
            gifUrl = refUrl.substring(idx + 1).trim();
            refUrl = refUrl.substring(0, idx);
        }
        if (t.length() > 128) {
            System.out.println(" --------- " + t);
            t = t.substring(0, 128);
        }
        return new Object[]{t, images, gifUrl, refUrl};
    }
}
