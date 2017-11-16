package com.szl.icu.miner.tools;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

/**
 * Created by Administrator on 2016/11/8.
 */
public class Test {
    public static void main(String args[]) {
        try {
            String html = new Markdown4jProcessor().process("## <a name=\"/definitions/MyResponse\">MyResponse</a>");
            System.out.println(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
