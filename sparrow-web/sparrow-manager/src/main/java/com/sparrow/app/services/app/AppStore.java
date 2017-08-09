package com.sparrow.app.services.app;

import com.sparrow.common.source.OptionItem;
import com.sparrow.common.source.SourceHandler;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.app.store.DataStore;
import com.sparrow.app.store.StoreFacade;

import java.util.Collections;
import java.util.List;

/**
 * Created by yuanzc on 2015/8/19.
 */
public class AppStore implements SourceHandler {
    private DataStore<AppInfo> store = StoreFacade.createStore(AppInfo.class);

    long longVal(String key) {
        if (StringUtils.isEmpty(key))
            return 0;
        //return System.nanoTime();
        return key.hashCode();
    }

    public void addAppInfo(AppInfo appInfo) {
        appInfo.setId(this.longVal(appInfo.getName()));
        this.store.put(appInfo.getId(), appInfo);
        this.store.sync();
    }

    public void updateAppInfo(AppInfo appInfo) {
        this.store.put(appInfo.getId(), appInfo);
        this.store.sync();
    }

    public AppInfo getAppInfo(String name) {
        return this.store.get(this.longVal(name));
    }

    public AppInfo getAppInfo(Long id) {
        return this.store.get(id);
    }

    public void removeAppInfo(String name) {
        this.store.remove(this.longVal(name));
    }

    public void removeAppInfo(Long id) {
        this.store.remove(id);
    }

    public List<AppInfo> list() {
        return this.store.list();
    }

    public void sync() {
        this.store.sync();
    }

    public void close() {
        this.store.close();
    }

    @Override
    public List<OptionItem> getSource(String query) {
        return Collections.EMPTY_LIST;
    }
}
