package com.sparrow.tools.pogen.generator;

import com.sparrow.tools.pogen.check.StrCheck;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-10-21
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
public interface GeneratorWrap {
    public boolean check(String table, String column);

    public IdGeneratorDefine getGenerateDefine();

    public StrCheck getStrCheck();
}
