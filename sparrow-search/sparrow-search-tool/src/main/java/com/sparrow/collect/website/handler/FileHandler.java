package com.sparrow.collect.website.handler;

import com.sparrow.collect.website.lucene.creator.IFileCreator;
import com.sparrow.collect.website.lucene.data.FileIndexItem;
import com.sparrow.collect.website.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class FileHandler {
    static final String BASE_DIR = System.getProperty("data.store.path");
    static String LINE_SEPARATOR = System.getProperty("line.separator");
    private boolean write2file = true;

    public FileHandler() {
    }

    public FileHandler(boolean write2file) {
        this.write2file = write2file;
    }

    public boolean isWrite2file() {
        return write2file;
    }

    public void setWrite2file(boolean write2file) {
        this.write2file = write2file;
    }

    protected abstract String getContent(File file);

    protected boolean skipRow(String row) {
        return false;
    }

    protected boolean isDit(String str) {
        for (char ch : str.toCharArray()) {
            if (!Character.isDigit(ch))
                return false;
        }
        return true;
    }

    String[] formatArray(String array[]) {
        List<String> list = new ArrayList<String>();
        String cel;
        int len = array.length;

        for (int i = 0; i < len; i++) {
            cel = array[i];
            if (StringUtils.isEmpty(cel))
                continue;
//            if (i == (len - 1) && !isDit(cel))
//                break;
            list.add(cel);
        }
        return list.toArray(new String[list.size()]);
    }

    String formatAge(String s) {
        if (StringUtils.isNotEmpty(s)) {
            int idx = s.indexOf('-');
            if (idx != -1)
                return s.substring(0, idx);
        }
        return s;
    }

    String formatPrice(String s) {
        if (StringUtils.isNotEmpty(s)) {
            int idx = s.indexOf(',');
            if (idx != -1)
                return s;
            String s2;
            idx = s.indexOf('p');
            if (idx == -1)
                idx = s.indexOf('P');
            if (idx == -1)
                idx = s.indexOf('m');
            if (idx != -1) {
                s2 = s.substring(0, idx);
                return s2 + "," + s2;
            } else {
                return s + "," + s;
            }
        }
        return s;
    }

    protected int findAge(String array[], int start) {
        int k = start;
        String cel;
        for (; k < array.length; k++) {
            cel = array[k];
            if (StringUtils.isEmpty(cel))
                continue;
            cel = this.formatAge(cel);
            if (this.isDit(cel)) {
                array[k] = cel;
                break;
            }
        }
        return k;
    }

    protected void writeLine(String line, Writer pw, int n) throws IOException {
        String array[] = line.split(" ");
        int len = array.length;
        if (len < 6 || len > 20) {
            pw.write(line);
            pw.write(LINE_SEPARATOR);
            return;
        }
        System.out.println(line);
        String newArray[] = new String[8];
        String cel;
        int j = 0;
        int i = 0;
        boolean hasSkip = false;
        array = this.formatArray(array);
        len = array.length;
        if (len > 6) {
            newArray[0] = array[0];
            newArray[1] = array[1];
            newArray[2] = array[2];
            j = 4;
            i = 8 - 4;
            i = this.findAge(array, 4);
            cel = array[i];
            newArray[j++] = cel;
            cel = this.formatPrice(array[i + 1]);
            newArray[j++] = cel;

            StringBuilder sb = new StringBuilder();
            int k = 3;
            int mx = i;
            boolean isFirst = true;
            for (; k < mx; k++) {
                cel = array[k];
                if (!isFirst)
                    sb.append("-");
                else
                    isFirst = false;
                sb.append(cel);
            }
            newArray[3] = sb.toString();
            i = i + 2;
            for (; i < len && j < 7; i++) {
                newArray[j++] = array[i];
            }
//            if (i > 8)
//                i = 8;
            sb.delete(0, sb.length());
            for (; i < len; i++) {
                sb.append(array[i]);
            }
            if (sb.length() > 0)
                newArray[j] = sb.toString();
            else
                newArray[j] = "无";
        } else {
            for (i = 0; i < len; i++) {
                cel = array[i];
                newArray[j++] = cel;
                if (len == 7 && i == 5) {
                    newArray[j++] = cel;
                }
            }
            if (len == 8)
                newArray[j] = "无";
        }
        pw.write(String.valueOf(n));
        for (i = 1; i < 8; i++) {
            cel = newArray[i];
            if (StringUtils.isEmpty(cel))
                continue;
            pw.write(',');
            pw.write(cel);
        }
        pw.write(LINE_SEPARATOR);
    }

    public void handle(File file, IndexWriter writer, IFileCreator creator) {
        String str = this.getContent(file);
        if (str == null)
            return;
        FileIndexItem item = new FileIndexItem(file);
        item.setAuthor("admin");
        if (this.write2file) {
            String fileName = file.getName();
            int dotPos = fileName.lastIndexOf(".");
            fileName = fileName.substring(0, dotPos) + ".txt";
            Writer pw = this.getPrinter(file, fileName);
            if (pw != null) {
                try {
                    StringReader stringReader = new StringReader(str);
                    BufferedReader reader = new BufferedReader(stringReader);
                    String line = null;
                    int n = 1;
                    while ((line = reader.readLine()) != null) {
                        if (this.skipRow(line))
                            continue;
                        this.writeLine(line, pw, n);
                        n++;
                    }
                    pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        item.setContent(str);
        try {
            if (writer != null)
                writer.addDocument(creator.createDocument(item));
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Writer getPrinter(File file, String fileName) {
        File fileOut = new File(BASE_DIR);
        if (!fileOut.exists())
            fileOut.mkdirs();
        FileUtil.copyFile(file, fileOut);
        fileOut = new File(fileOut, fileName);
//		if (fileOut.delete())
//			return null;
        try {
            FileWriter pw = new FileWriter(fileOut);
            return pw;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String formatString(String str) {
        if (StringUtils.isEmpty(str))
            return str;
        char[] chars = str.toCharArray();
        int len = chars.length - 1;
        int nLen = chars.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nLen; i++) {
            char n = chars[i];
            if (this.isIgnoreChar(n))
                continue;
            if ('☆' == n) {
                sb.append('2');
                break;
            } else if ('一' == n || '－' == n) {
                sb.append('-');
                continue;
            } else if (n == ',') {
                if (i >= len || i == 0) {
                    sb.append(n);
                    continue;
                } else if (Character.isDigit(chars[i - 1]) && Character.isDigit(chars[i + 1])) {
                    sb.append(",");
                    continue;
                }
            } else if (n == '，') {
                sb.append("-");
            }
//            else if (n == '-') {
//                if (i >= len || i == 0) {
//                    sb.append(n);
//                    continue;
//                } else if (Character.isDigit(chars[i - 1]) && Character.isDigit(chars[i + 1])) {
//                    int idx = i;
//                    for (; idx < nLen; idx++) {
//                        if (chars[idx] == ' ')
//                            break;
//                    }
//                    i = idx;
//                    sb.append(" ");
//                    continue;
//                }
//            }
            sb.append(n);
        }
        return sb.toString();
    }

    protected boolean isIgnoreChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.HIGH_SURROGATES || ub == Character.UnicodeBlock.LOW_SURROGATES)
            return true;
        if (Character.isSpaceChar(c) || Character.isWhitespace(c))
            return true;
        if (c == ' ' || c == '〰' || c == '❀' || c == '\t')
            return true;
        return false;
    }
}
