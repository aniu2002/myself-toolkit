package com.sparrow.collect.store;

import com.sparrow.collect.orm.jdbc.DataSourceConnectionFactory;
import com.sparrow.collect.orm.mapping.MapConfig;
import com.sparrow.collect.orm.session.DataSourceSession;
import com.sparrow.collect.orm.session.DefaultSession;
import com.sparrow.collect.store.check.BdbDataCheck;
import com.sparrow.collect.store.check.DataTagCheck;
import com.sparrow.collect.store.check.DefaultDataCheck;
import com.sparrow.collect.store.check.TagCheck;
import com.sparrow.collect.utils.ConvertUtils;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/5.
 */
public class DatabaseStore implements DataStore {
    protected DefaultSession session;
    protected TagCheck tagCheck;
    protected boolean needCheck = true;
    protected boolean needUpdate = true;

    public DatabaseStore(Map<String, String> storeSet) {
        this.session = new DataSourceSession(
                new DataSourceConnectionFactory(storeSet),
                new MapConfig("classpath:map/mapConfig.xml"));
        this.needCheck = "true".equalsIgnoreCase(storeSet.get("need.checkData"));
        this.needUpdate = "true".equalsIgnoreCase(storeSet.get("need.updateData"));
        if (this.needCheck) {
            String checkType = storeSet.get("data.check.type");
            if ("db".equalsIgnoreCase(checkType))
                this.tagCheck = new DataTagCheck(this.session, storeSet.get("check.field"));
            else if ("cache".equalsIgnoreCase(checkType))
                this.tagCheck = new BdbDataCheck(storeSet.get("temp.data.dir")
                        , storeSet.get("check.field")
                        , ConvertUtils.toInt(storeSet.get("check.index"), 0));
            else
                this.tagCheck = new DefaultDataCheck();
        }
    }

    @Override
    public void save(Object object) {
        session.save(object);
    }

    @Override
    public int checkAndSave(Object object) {
        if (this.needCheck && this.exists(object)) {
            if (this.needUpdate)
                this.update(object);
            return 0;
        } else {
            this.save(object);
            if (this.needCheck)
                this.saveTag(object);
            return 1;
        }
    }

    protected void saveTag(Object object) {
        this.tagCheck.saveTag(object);
    }

    @Override
    public void update(Object object) {
        session.update(object);
    }

    @Override
    public boolean exists(Object object) {
        return this.tagCheck.checkTag(object);
    }

    @Override
    public void close() {
        session.close();
        if (this.needCheck)
            this.tagCheck.close();
    }
}
