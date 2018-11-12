package com.sparrow.collect.document;

import com.sparrow.collect.document.parse.FieldParseStrategy;
import com.sparrow.collect.document.parse.FieldStrategyFactory;
import com.sparrow.collect.document.strpro.IStringProcessor;
import com.sparrow.collect.document.strpro.StringProcessFactory;
import com.sparrow.core.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Administrator on 2018/8/24.
 */
public class DocumentHandler {

    Document create(IdxField[] fbs, Object recordValue) {
        String parseStr;
        String[] strategies;
        IStringProcessor sp;
        FieldParseStrategy fps;
        IndexableField indexField;
        Document doc = new Document();
        for (IdxField fb : fbs) {
            parseStr = getDocValue(fb, recordValue);
            if (StringUtils.isBlank(parseStr)) {
                continue;
            }
            for (String strPro : fb.getTextProcessor()) {
                sp = StringProcessFactory.getStrProcess(strPro);
                if (sp != null) {
                    parseStr = sp.process(parseStr);
                }
            }
            strategies = fb.getStrategies();
            for (String strategy : strategies) {
                fps = FieldStrategyFactory.getFieldParser(strategy);
                if (fps != null) {
                    indexField = fps.parse(fb.getField(), parseStr);
                    if (indexField != null) {
                        doc.add(indexField);
                    }
                }
            }
        }
        return doc;
    }

    private String getDocValue(IdxField fb, Object object) {
        String[] keys = fb.getDataKeys();
        Object v;
        StringBuilder sb = new StringBuilder();
        try {
            for (String str : keys) {
                v = PropertyUtils.getProperty(object, str);
                sb.append(v).append('\t');
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (sb.length() > 0) {
            return sb.toString().trim();
        }
        return sb.toString();
    }
}
