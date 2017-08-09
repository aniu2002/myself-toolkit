package com.sparrow.app.services.provider;

import com.sparrow.common.source.OptionItem;
import com.sparrow.common.source.SourceHandler;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.file.FileToolHelper;
import com.sparrow.tools.utils.JAXB2Util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanzc on 2015/8/24.
 */
public class ProviderStore implements SourceHandler {
    private static File file = new File(SystemConfig.getProperty("provider.store.path", System.getProperty("user.home") + "/.provider"));

    private Map<String, ProviderSource> providerMap = new HashMap<String, ProviderSource>();
    private Map<String, SourceConfigWrapper> sourceMap = new HashMap<String, SourceConfigWrapper>();
    //    private static File file = new File(System.getProperty("provider.store.path", System.getProperty("user.home") + "/cfg/data-provider.xml"));
//    private static File sourceConfig = new File(System.getProperty("source.config.path", System.getProperty("user.home") + "/cfg/source-config.xml"));

    private final OptionItem defaultItem;

    {
        File fileDirs[] = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        if (fileDirs != null && fileDirs.length > 0) {
            File pFile, sFile;
            for (File f : fileDirs) {
                pFile = new File(f, "provider.xml");
                sFile = new File(f, "sourceCfg.xml");

                if (pFile.exists() && pFile.length() > 0) {
                    try {
                        this.providerMap.put(f.getName(), JAXB2Util.translateXML2Object(pFile, ProviderSource.class));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                if (sFile.exists() && sFile.length() > 0) {
                    try {
                        this.sourceMap.put(f.getName(), JAXB2Util.translateXML2Object(sFile, SourceConfigWrapper.class));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        defaultItem = new OptionItem();
        defaultItem.setCode("@");
        defaultItem.setName("@");
    }

    public ProviderSource getProviderSource(String name) {
//        try {
        ProviderSource providerSource = this.providerMap.get(name);
//            if (providerSource == null) {
//                File tmp = new File(file, name + "/provider.xml");
//                if (!tmp.exists())
//                    return null;
//                providerSource = JAXB2Util.translateXML2Object(tmp, ProviderSource.class);
//                if (providerSource != null)
//                    this.providerMap.put(name, providerSource);
//            }
        return providerSource;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    @Override
    public List<OptionItem> getSource(String query) {
        List<OptionItem> opts = new ArrayList<OptionItem>();
        SourceConfigWrapper wrapper = this.sourceMap.get(query);
        if (wrapper != null) {
            List<SourceConfig> sources = wrapper.getSources();
            opts.add(defaultItem);
            for (SourceConfig s : sources) {
                OptionItem op = new OptionItem();
                op.setCode(s.getName());
                op.setName(s.getDesc());
                op.setExtra(s.getName());
                opts.add(op);
            }
        }
        return opts;
    }

    public void removeProviderSource(String name) {
        File tmp = new File(file, name + "/provider.xml");
        if (tmp.exists())
            tmp.delete();
        this.providerMap.remove(name);
    }

    public boolean saveProviderSource(ProviderSource providerSource) {
        String name = providerSource.getName();
        File tmp = new File(file, name + "/provider.xml");
        if (!tmp.getParentFile().exists())
            tmp.getParentFile().mkdirs();
        return JAXB2Util.translateObject2XML(providerSource, tmp);
    }

    public boolean saveProviderSource(String name) {
        ProviderSource providerSource = this.getProviderSource(name);
        if (providerSource == null || !providerSource.isChanged())
            return false;
        File tmp = new File(file, name + "/provider.xml");
        if (!tmp.getParentFile().exists())
            tmp.getParentFile().mkdirs();
        providerSource.setChanged(false);
        return JAXB2Util.translateObject2XML(providerSource, tmp);
    }

    public void addProviderItem(String name, ProviderItem providerItem) {
        ProviderSource providerSource = this.getProviderSource(name);
        if (providerSource == null) {
            providerSource = new ProviderSource();
            providerSource.setName(name);
            this.providerMap.put(name, providerSource);
        }
        providerSource.addItem(providerItem);
    }

    public ProviderItem getProviderItem(String name, String itemName) {
        ProviderSource providerSource = this.getProviderSource(name);
        if (providerSource != null)
            return providerSource.getItem(itemName);
        return null;
    }

    public void updateProviderItem(String name, ProviderItem providerItem) {
        ProviderSource providerSource = this.getProviderSource(name);
        if (providerSource != null)
            providerSource.updateItem(providerItem);
    }

    public void deleteProviderItem(String sourName, String itemName) {
        ProviderSource providerSource = this.getProviderSource(sourName);
        if (providerSource != null)
            providerSource.remove(itemName);
    }

    public static void moveProviderTo(String name, String file) {
        File tmp = new File(file, name + "/provider.xml");
        File f = new File(file);

        FileToolHelper.copyFile(tmp, f);
    }

    public SourceConfigWrapper getSourceConfig(String name) {
//        try {
//            File tmp = new File(file, name + "/sourceCfg.xml");
//            if (!tmp.exists())
//                return null;
//            return JAXB2Util.translateXML2Object(tmp, SourceConfigWrapper.class);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        return this.sourceMap.get(name);
    }

    public void removeSourceConfig(String name) {
        File tmp = new File(file, name + "/sourceCfg.xml");
        if (tmp.exists())
            tmp.delete();
        this.sourceMap.remove(name);
    }

    public boolean saveSourceConfig(SourceConfigWrapper sourceConfigWrapper) {
        String name = sourceConfigWrapper.getName();
        File tmp = new File(file, name + "/sourceCfg.xml");
        if (!tmp.getParentFile().exists())
            tmp.getParentFile().mkdirs();
        return JAXB2Util.translateObject2XML(sourceConfigWrapper, tmp);

    }

    public boolean saveSourceConfig(String name) {
        SourceConfigWrapper sourceConfigWrapper = this.getSourceConfig(name);
        if (sourceConfigWrapper == null || !sourceConfigWrapper.isChanged())
            return false;
        File tmp = new File(file, name + "/sourceCfg.xml");
        if (!tmp.getParentFile().exists())
            tmp.getParentFile().mkdirs();
        sourceConfigWrapper.setChanged(false);
        return JAXB2Util.translateObject2XML(sourceConfigWrapper, tmp);
    }

    SourceConfig getAppSourceConfig(String name) {
        SourceConfig defaultSourceConfig = new SourceConfig();
        defaultSourceConfig.setName("local");
        defaultSourceConfig.setType("@");
        defaultSourceConfig.setDesc("app本地数据源");
        defaultSourceConfig.setProps(name);
        return defaultSourceConfig;
    }

    public void addSourceConfig(String name, SourceConfig sourceConfig) {
        SourceConfigWrapper sourceConfigWrapper = this.getSourceConfig(name);
        if (sourceConfigWrapper == null) {
            sourceConfigWrapper = new SourceConfigWrapper();
            sourceConfigWrapper.setName(name);
            this.sourceMap.put(name, sourceConfigWrapper);
            // sourceConfigWrapper.addItem(this.getAppSourceConfig(name));
        }
        sourceConfigWrapper.addItem(sourceConfig);
    }

    public SourceConfig getSourceConfig(String name, String itemName) {
        SourceConfigWrapper sourceConfigWrapper = this.getSourceConfig(name);
        if (sourceConfigWrapper != null)
            return sourceConfigWrapper.getItem(itemName);
        return null;
    }

    public void updateSourceConfig(String name, SourceConfig sourceConfig) {
        SourceConfigWrapper sourceConfigWrapper = this.getSourceConfig(name);
        if (sourceConfigWrapper != null)
            sourceConfigWrapper.updateItem(sourceConfig);
    }

    public void deleteSourceConfig(String sourName, String itemName) {
        SourceConfigWrapper sourceConfigWrapper = this.getSourceConfig(sourName);
        if (sourceConfigWrapper != null)
            sourceConfigWrapper.remove(itemName);
    }

    public static void moveSourceTo(String name, String file) {
        File tmp = new File(file, name + "/sourceCfg.xml");
        File f = new File(file);

        FileToolHelper.copyFile(tmp, f);
    }
}
