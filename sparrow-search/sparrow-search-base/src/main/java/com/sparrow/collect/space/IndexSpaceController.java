package com.sparrow.collect.space;

import com.sparrow.collect.Contants;
import com.sparrow.collect.utils.PathResolver;
import com.sparrow.collect.website.SearchConfig;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.resource.PathMatchingResourceResolver;
import com.sparrow.core.resource.source.Resource;
import com.sparrow.core.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class IndexSpaceController {
    private static Log log = LogFactory.getLog(IndexSpaceController.class);
    private static Map<String, IndexSpace> spacers = new ConcurrentHashMap<>();

    static {
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IndexSpaceController() {

    }

    public static IndexSpace getSpace(String name) {
        return spacers.get(name);
    }

    static String getSearchIDs(SearchConfig config) {
        String searchIDS = config.get(Contants.SEARCH_LIST_TAG);
        return searchIDS;
    }

    static SearchConfig getConfig(InputStream stream) {
        return new SearchConfig(SystemConfig.processYml(stream));
    }

    static void load() throws IOException {
        Resource[] resources = new PathMatchingResourceResolver().getResources("searcher/*.yml");
        log.info("start init space:");
        for (Resource resource : resources) {
            SearchConfig cfg = getConfig(resource.getInputStream());
            if (cfg.isEmpty())
                continue;
            String name = PathResolver.trimExtension(resource.getFilename());
            String type = cfg.get("index.type", "disk");
            String alias = cfg.get("index.alias", name);
            String indexPath = cfg.get("index.path");
            if (StringUtils.isEmpty(indexPath))
                indexPath = String.format("%s/%s", System.getProperty("home.dir"), new Random(10).nextInt());
            if (StringUtils.equals("ram", type))
                spacers.put(name, new RamIndexSpace(name, indexPath, cfg, alias));
            else
                spacers.put(name, new DiskIndexSpace(name, indexPath, cfg, alias));
        }
    }
}
