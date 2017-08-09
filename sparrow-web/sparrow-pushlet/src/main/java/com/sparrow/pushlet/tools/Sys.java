package com.sparrow.pushlet.tools;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 13-3-18
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public class Sys {
    static public String forHTMLTag(String aTagFragment) {
        final StringBuffer result = new StringBuffer();

        final StringCharacterIterator iterator = new StringCharacterIterator(aTagFragment);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&#039;");
            } else if (character == '\\') {
                result.append("&#092;");
            } else if (character == '&') {
                result.append("&amp;");
            } else {
                //the char is not a special one
                //add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    static public long now() {
        return System.currentTimeMillis();
    }
}
