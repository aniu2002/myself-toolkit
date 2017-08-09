package com.sparrow.core.utils.ext;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-8-8
 * Time: 下午4:20
 * To change this template use File | Settings | File Templates.
 */
public class ShortUrl {
    static final char[] chars = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    static final int base30bit = 1073741823;
    static final int baseIdxMod = 61;

    public static void main(String[] args) {
        System.out.println(base30bit);
        System.out.println(baseIdxMod);
        String url = "http://www.sunchis.com";
        for (String string : ShortText(url)) {
            print(string);
        }
    }

    public static String[] ShortText(String string) {
        String key = "XuLiang";                 //自定义生成MD5加密字符串前的混合KEY
        String hex = DigestUtils.md5Hex(key + string);
        int hexLen = hex.length();
        int subHexLen = hexLen / 8;
        String[] ShortStr = new String[4];

        for (int i = 0; i < subHexLen; i++) {
            String outChars = "";
            int j = i + 1;
            String subHex = hex.substring(i * 8, j * 8);
            int idx = (int) (base30bit & Long.valueOf(subHex, 16));

            for (int k = 0; k < 6; k++) {
                int index = baseIdxMod & idx;
                outChars += chars[index];
                idx = idx >> 5;
            }
            ShortStr[i] = outChars;
        }

        return ShortStr;
    }

    private static void print(Object messagr) {
        System.out.println(messagr);
    }
}
