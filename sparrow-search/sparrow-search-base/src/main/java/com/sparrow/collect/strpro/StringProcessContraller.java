package com.dili.dd.searcher.basesearch.common.stringprocessor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import com.dili.dd.searcher.basesearch.common.config.ConfigIniter;
import com.dili.dd.searcher.basesearch.common.config.Contants;
import com.dili.dd.searcher.basesearch.common.strategy.StrategyController;


public class StringProcessContraller extends ConfigIniter {
    Map<String, IStringProcessor> strPros = new ConcurrentHashMap<String, IStringProcessor>();
    private Log log = LogFactory.getLog(StrategyController.class);
    
    
    private static StringProcessContraller spContraller = new StringProcessContraller();
    
    public static StringProcessContraller getContraller() {
        return spContraller;
    }
    
    public void addStrProcess(Configuration config) {
        try {
            parserConf(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void delStrProcessName(String strategyName) {
        strPros.remove(strategyName);
    }
    
    public IStringProcessor getStrProcess(String spName, Configuration config) {
        if (!strPros.containsKey(spName)) {
            try {
                parserConf(config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return strPros.get(spName);
    }

    @Override
    public void parserConf(Configuration config) throws IOException {
        //TODO  缺少具体的策略配置
        String[] strProTags = config.getStrings(Contants.STRING_PROCESS_LIST_TAG);
        assert strProTags==null;
        if (strProTags==null) {
            return;
        }
        log.info("init stringProcess:" + Arrays.asList(strProTags));
        for (String strPro : strProTags) {
            List<IStringProcessor> l = config.getInstances(Contants.getStringByArray(new String[]{Contants.STRING_PROCESS_TAG_PREFIX,strPro,"cla"}), IStringProcessor.class);
            if (l.size() > 0&&!strPros.containsKey(strPro)) {
                log.info("string Process :" + strPro + "; cla: " + l.get(0));
                strPros.put(strPro, l.get(0));
            }
        }
    
    }

    
}
