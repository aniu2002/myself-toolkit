package com.sparrow.collect.website.format;

import com.sparrow.collect.website.Configs;
import com.sparrow.collect.data.search.SearchBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2018/7/31.
 */
public class KeywordFormatManager {

    private Log log = LogFactory.getLog(KeywordFormatManager.class);

    private static KeywordFormatManager instance = new KeywordFormatManager();

    private Map<String, KeywordFormat> keyWordFormats = new ConcurrentHashMap<>();

    // format别名到format类全名的映射
    private Map<String, String[]> searchersFieldsFormatKeys = new ConcurrentHashMap<>();

    // <searchId,格式化器列表>
    private Map<String, List<KeywordFormat>> searchersFieldsFormat = new ConcurrentHashMap<>();

    public static KeywordFormatManager getInstance() {
        return instance;
    }


    @SuppressWarnings("rawtypes")
    public void init() throws Exception {

        try {

            Map<String, String> formatsClass = getFormatsClass();
            keyWordFormats = new ConcurrentHashMap<>();

            for (String identify : formatsClass.keySet()) {

                String className = formatsClass.get(identify);
                Class c = Class.forName(className);
                KeywordFormat format = (KeywordFormat) c.newInstance();
                keyWordFormats.put(identify, format);

            }

            searchersFieldsFormat = new ConcurrentHashMap<>();

            searchersFieldsFormatKeys = getSearchersFormatKeys();

            for (String searchIdentify : searchersFieldsFormatKeys.keySet()) {

                String[] fieldsFormatKeys = searchersFieldsFormatKeys.get(searchIdentify);
                List<KeywordFormat> keyWordFormatListTmp = new LinkedList<>();
                if (null != fieldsFormatKeys) {

                    for (String fieldFormatKey : fieldsFormatKeys) {

                        KeywordFormat keyWordFormat = keyWordFormats.get(fieldFormatKey);
                        keyWordFormatListTmp.add(keyWordFormat);
                    }

                }

                searchersFieldsFormat.put(searchIdentify, keyWordFormatListTmp);
            }
        } catch (ReflectiveOperationException e) {
            log.fatal(e);
            throw e;
        }
    }

    private Map<String, String> getFormatsClass() {
        Map<String, String> formatsClass = new HashMap<String, String>();
        String[] formatsName = Configs.get("searcher.format.list").split(",");
        if (formatsName != null) {
            for (String formatName : formatsName) {
                String formatClass = Configs.get(String.format("searcher.format.%s.class", formatName));
                formatsClass.put(formatName, formatClass);
            }
        }
        return formatsClass;
    }

    private Map<String, String[]> getSearchersFormatKeys() {
        Map<String, String[]> ret = new HashMap<>();
        String[] searchIds = Configs.get("searcher.format.searchId.list").split(",");
        if (null != searchIds) {
            for (String searchId : searchIds) {
                String[] formatKeys = Configs.get(String.format("searcher.basesearch.format.%s.process.list", searchId)).split(",");
                ret.put(searchId, formatKeys);
            }
        }
        return ret;
    }

    @SuppressWarnings({"unused", "rawtypes"})
    public String keywordFormat(SearchBean searchBean, String searchId, String searchKeyWord) throws Exception {
        if (StringUtils.isBlank(searchKeyWord)) {
            return searchKeyWord;
        }
        List<KeywordFormat> fieldsFormats = searchersFieldsFormat.get(searchId);
        if (CollectionUtils.isNotEmpty(fieldsFormats)) {
            for (KeywordFormat fieldFormats : fieldsFormats) {
                if (fieldFormats != null) {
                    searchKeyWord = fieldFormats.format(searchKeyWord);
                }
            }
        }
        final int MAX_LENGTH = 15;
        if (searchKeyWord.length() > MAX_LENGTH) {
            searchKeyWord = searchKeyWord.substring(0, MAX_LENGTH);
        }
        searchBean.setSearchCondStr(searchKeyWord);
        return searchKeyWord;
    }
}