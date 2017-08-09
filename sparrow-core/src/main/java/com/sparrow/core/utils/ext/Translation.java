package com.sparrow.core.utils.ext;

import com.sparrow.core.utils.FileIOUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


public class Translation {
    StringBuffer fantiBuffer;
    StringBuffer jiantiBuffer;

    public HashMap<Character, Character> fan2Jian = new HashMap<Character, Character>();
    public HashMap<Character, Character> jian2Fan = new HashMap<Character, Character>();

    public Translation() {
        formMap("classpath:font/fanti.txt", "classpath:font/jianti.txt");
    }

    public StringBuffer getDictionary(String path) {
        BufferedReader bufferReader = null;
        try {
            bufferReader = new BufferedReader(new InputStreamReader(FileIOUtil.getFileInputStream(path)));
            StringBuffer readAll = new StringBuffer();
            String line;
            while ((line = bufferReader.readLine()) != null) {
                readAll.append(line);
            }
            return readAll;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferReader != null)
                try {
                    bufferReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    public void formMap(String pathOfFanti, String pathOfJianti) {
        jiantiBuffer = getDictionary(pathOfJianti);
        fantiBuffer = getDictionary(pathOfFanti);
        int k = jiantiBuffer.length();
        Character fan = null;
        Character jian = null;
        for (int i = 0; i < k; i++) {
            fan = fantiBuffer.charAt(i);
            jian = jiantiBuffer.charAt(i);
            fan2Jian.put(fan, jian);
            jian2Fan.put(jian, fan);
        }
    }

    public void translateToSample(StringBuilder from) {
        int i = from.length();
        char come;
        for (int k = 0; k < i; k++) {
            come = from.charAt(k);
            if (fan2Jian.containsKey(come)) {
                from.setCharAt(k, fan2Jian.get(come));
            }
        }

    }

    public void translateToComplex(StringBuilder from) {
        int i = from.length();
        char come;
        for (int k = 0; k < i; k++) {
            come = from.charAt(k);
            if (jian2Fan.containsKey(come)) {
                from.setCharAt(k, jian2Fan.get(come));
            }
        }
    }

    public static void main(String[] args) {
        Translation tran = new Translation();
        StringBuilder from = new StringBuilder();
        from.append("核心提示：澳大利亚FAXTS新闻3月5日刊登评论认为，美国在全球一系列被解释成用来防御来自伊朗和朝鲜导弹袭击的弹道导弹防御系统的部署行为，以及最近将先进具有反导能力爱国者导弹出售给台湾的动作，其根本目的是针对两个主要的核大国--中国和俄罗斯。");
        tran.translateToComplex(from);
        System.out.println(from.toString());
        from.delete(0, from.length());
        from.append("種子連接：澳大利亚FAXTS新闻3月5日刊登评论认为，美国在全球一系列被解释成用来防御来自伊朗和朝鲜导弹袭击的弹道导弹防御系统的部署行为，以及最近将先进具有反导能力爱国者导弹出售给台湾的动作，其根本目的是针对两个主要的核大国--中国和俄罗斯。");
        tran.translateToSample(from);
        System.out.println(from);
    }
}
