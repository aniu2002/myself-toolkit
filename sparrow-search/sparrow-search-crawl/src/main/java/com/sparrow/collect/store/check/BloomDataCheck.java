package com.sparrow.collect.store.check;

import com.sparrow.collect.cache.bloom.DuplicateUrlCheck;
import com.sparrow.collect.utils.PropertyUtils;

/**
 * Project Name: sparrow-egg
 * Package Name: com.sparrow.collect.store.check
 * Author : YZC
 * Date: 2016/12/12
 * Time: 18:45
 */
public class BloomDataCheck implements TagCheck {
    private DuplicateUrlCheck urlCheck;

    public BloomDataCheck(DuplicateUrlCheck urlCheck) {
        this.urlCheck = urlCheck;
    }

    @Override
    public boolean checkTag(Object object) {
        return this.urlCheck.check(PropertyUtils.property(object, "url").toString());
    }

    @Override
    public void saveTag(Object object) {
    }

    @Override
    public void close() {
        this.urlCheck.close();
    }
}
