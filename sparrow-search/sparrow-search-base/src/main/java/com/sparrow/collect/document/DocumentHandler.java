package com.sparrow.collect.document;

import org.apache.lucene.document.Document;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/24.
 */
public class DocumentHandler {
    Document create(){

    }
    private String getDocValue(Field fb, Map<String, List<CharSequence>> recordValue) {
        String[] strs = fb.getSourceDataKeys();
        List<CharSequence> list = null;
        StringBuilder sb = new StringBuilder();
        for (String str: strs) {
            list = recordValue.get(str);
            if (list == null || list.isEmpty()) {
                continue;
            }
            for (CharSequence cs : list) {
                sb.append(cs).append('\t');
            }
        }
        if (sb.length() > 0) {
            return sb.toString().trim();
        }
        return sb.toString();
    }
}
