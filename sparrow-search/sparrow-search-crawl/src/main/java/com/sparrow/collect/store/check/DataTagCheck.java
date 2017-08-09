package com.sparrow.collect.store.check;

import com.sparrow.collect.orm.session.DefaultAbstractSession;
import com.sparrow.collect.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Project Name: sparrow-egg
 * Package Name: com.sparrow.collect.store.check
 * Author : YZC
 * Date: 2016/12/12
 * Time: 18:42
 */
public class DataTagCheck implements TagCheck {
    private final DefaultAbstractSession session;
    private final String checkField;

    public DataTagCheck(DefaultAbstractSession session, String checkField) {
        this.session = session;
        if (StringUtils.isEmpty(checkField))
            this.checkField = "uuid";
        else
            this.checkField = checkField;
    }

    @Override
    public boolean checkTag(Object object) {
        return session.hasObject(object.getClass(), PropertyUtils.property(object, this.checkField));
    }

    @Override
    public void saveTag(Object object) {

    }

    @Override
    public void close() {

    }
}
