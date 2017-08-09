package com.sparrow.collect.store.check;

import com.sparrow.collect.cache.bdb.DataTag;
import com.sparrow.collect.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Project Name: sparrow-egg
 * Package Name: com.sparrow.collect.store.check
 * Author : YZC
 * Date: 2016/12/12
 * Time: 18:45
 */
public class BdbDataCheck implements TagCheck {
    private final DataTag dataTag;
    private final String checkField;
    private final int checkIndex;

    public BdbDataCheck(String dir, String checkField, int checkIndex) {
        this.dataTag = new DataTag(dir);
        if (StringUtils.isEmpty(checkField))
            this.checkField = "uuid";
        else
            this.checkField = checkField;
        this.checkIndex = checkIndex;
    }

    String getCheckFieldString(Object object) {
        Object objects[] = (Object[]) object;
        return (String) objects[this.checkIndex];
    }

    @Override
    public boolean checkTag(Object object) {
        if (object.getClass().isArray())
            return this.dataTag.exists(this.getCheckFieldString(object));
        else {
            Object target = PropertyUtils.property(object, this.checkField);
            if (target == null)
                return false;
            return this.dataTag.exists(target.toString());
        }
    }

    @Override
    public void saveTag(Object object) {
        if (object.getClass().isArray()) {
            String n = this.getCheckFieldString(object);
            if (n != null)
                this.dataTag.put(n);
        } else {
            Object target = PropertyUtils.property(object, this.checkField);
            if (target == null)
                return;
            this.dataTag.put(target.toString());
        }
    }

    @Override
    public void close() {
        this.dataTag.close();
    }
}
