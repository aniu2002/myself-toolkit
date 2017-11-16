package com.szl.icu.miner.tools.data;

import com.szl.icu.miner.tools.utils.StringUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/19.
 */
public class Modules {
    private Map<String, Module> modules = new HashMap<String, Module>();
    private String basePack;

    public String getBasePack() {
        return basePack;
    }

    public void setBasePack(String basePack) {
        this.basePack = basePack;
    }

    public Map<String, Module> getModules() {
        return modules;
    }

    public void addModule(String key, Module module) {
        this.modules.put(key, module);
    }

    public Module getModule(String key) {
        return this.modules.get(key);
    }

    public boolean contain(String key) {
        return this.modules.containsKey(key);
    }

    public boolean isEmptyReq(String key) {
        return this.modules.containsKey(key);
    }

    static void parseModules(String key, String value, Modules modules) {
        String m = null;
        String ck = null;
        String rk = null;
        int idx = key.indexOf('.');
        if (idx != -1) {
            m = key.substring(0, idx);
            ck = key.substring(idx + 1);
            idx = ck.indexOf('.');
            if (idx != -1) {
                rk = ck.substring(idx + 1);
                ck = ck.substring(0, idx);
            }
        }
        rk = removeQuotes(rk);
        Module module = modules.getModule(m);
        if (module == null) {
            module = new Module();
            modules.addModule(m, module);
        }
        module.setName(m);
        if ("actor".equals(ck))
            module.setActor(value);
        else if ("services".equals(ck))
            module.setMessage(value);
        else if ("reqmap".equals(ck))
            module.addReqMap(rk, value);
        else if ("respmap".equals(ck))
            module.addRespMap(rk, value);
        else if ("desc".equals(ck))
            module.addDescMap(rk, value);
    }

    static String removeQuotes(String str) {
        if (StringUtils.isEmpty(str))
            return str;
        StringBuilder sb = new StringBuilder();
        char[] arr = str.toCharArray();
        for (char c : arr) {
            if (c == '"')
                continue;
            sb.append(c);
        }
        return sb.toString();
    }

    public static Modules parse(String resources) {
        Modules modules = new Modules();
        Config config = ConfigFactory.load(resources);
        try {
            Iterator<Map.Entry<String, ConfigValue>> iterator = config.getConfig("icu.rest").entrySet().iterator();
            Map.Entry<String, ConfigValue> e;
            while (iterator.hasNext()) {
                e = iterator.next();
                if ("basePack".equals(e.getKey())) {
                    modules.setBasePack(e.getValue().unwrapped().toString());
                } else {
                    parseModules(e.getKey(), e.getValue().unwrapped().toString(), modules);
                }
            }
        } catch (ConfigException e) {
            e.printStackTrace();
        }
        return modules;
    }
}
