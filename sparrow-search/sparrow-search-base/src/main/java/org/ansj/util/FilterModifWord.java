package org.ansj.util;

import org.ansj.domain.Nature;
import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Yzc
 * - Date: 2019/3/6 14:33
 */
public class FilterModifWord {

    private static Set<String> FILTER = new HashSet<String>();

    private static String TAG = "#";

    private static boolean isTag = false;

    public static void insertStopWords(List<String> filterWords) {
        FILTER.addAll(filterWords);
    }

    public static void insertStopWord(String... filterWord) {
        for (String word : filterWord) {
            FILTER.add(word);
        }
    }

    public static void insertStopNatures(String... filterNatures) {
        isTag = true;
        for (String natureStr : filterNatures) {
            FILTER.add(TAG + natureStr);
        }

    }

    /*
     * 停用词过滤并且修正词性
     */
    public static List<Term> modifResult(List<Term> all) {
        List<Term> result = new ArrayList<Term>();
        try {
            for (Term term : all) {
                if (FILTER.size() > 0 && (FILTER.contains(term.getName()) || (isTag && FILTER.contains(TAG + term.natrue().natureStr)))) {
                    continue;
                }
                String[] params = UserDefineLibrary.getParams(term.getName());
                if (params != null) {
                    term.setNature(new Nature(params[0]));
                }
                result.add(term);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.err.println("FilterStopWord.updateDic can not be null , " + "you must use set FilterStopWord.setUpdateDic(map) or use method set map");
        }
        return result;
    }
}