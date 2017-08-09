package com.sparrow.tools.test;

import java.util.Map;

/**
 * Created by Administrator on 2016/11/30.
 */
public class DefaultIHandler implements IHandler {
    @Override
    public void doExecute(Map<String, String> param) {
        System.out.println(param);
    }
}
