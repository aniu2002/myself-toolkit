package com.sparrow.tools.pogen.generator;

import com.sparrow.tools.pogen.check.StrCheck;

/**
 * Created with IntelliJ IDEA.
 * User: YZC
 * Date: 13-10-21
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public class DefaultGeneratorWrap implements GeneratorWrap {
    private final StrCheck check;
    private final IdGeneratorDefine idGeneratorDefine;

    public DefaultGeneratorWrap(StrCheck check, IdGeneratorDefine idGeneratorDefine) {
        this.check = check;
        this.idGeneratorDefine = idGeneratorDefine;
    }

    @Override
    public boolean check(String table, String column) {
        String v = table + "." + column;
        if (this.check == null)
            return false;
        return this.check.check(v);
    }

    @Override
    public IdGeneratorDefine getGenerateDefine() {
        return this.idGeneratorDefine;
    }

    @Override
    public StrCheck getStrCheck() {
        return this.check;
    }
}
