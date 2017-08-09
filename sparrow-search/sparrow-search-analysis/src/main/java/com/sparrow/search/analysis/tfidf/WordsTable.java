package com.sparrow.search.analysis.tfidf;

import java.util.Iterator;

/**
 * Project Name: icloudunion
 * Package Name: com.sparrow.search.analysis
 * Author : YZC
 * Date: 2017/1/24
 * Time: 11:27
 */
public class WordsTable {
    public boolean isStopWord(String word) {
        return false;
    }

    public String getStem(String word) {
        return word;
    }

    public void stat(String word) {
    }

    public Iterator<WordCount> getWords() {
        return null;
    }
}
