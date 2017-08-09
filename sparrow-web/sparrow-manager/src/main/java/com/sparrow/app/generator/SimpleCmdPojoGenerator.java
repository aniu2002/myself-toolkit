package com.sparrow.app.generator;

import com.sparrow.app.common.InfoHolder;
import com.sparrow.app.common.SpeEnvironment;
import com.sparrow.tools.cmd.XmdMapperGenerator;
import com.sparrow.tools.cmd.eggs.CmdPojoGenerator;

import java.io.File;

/**
 * @author YZC
 * @version 0.1 (2013-11-5)
 * @modify
 */
public class SimpleCmdPojoGenerator extends CmdPojoGenerator {
    @Override
    protected File getConfigPath(String basePath) {
        InfoHolder holder = SpeEnvironment.getInfoHolder();
        if (holder == null)
            return XmdMapperGenerator.getNormalFile(basePath,
                    "eggs", "mapConfig.xml");
        else {
            File f = XmdMapperGenerator.getNormalFile(holder.getAppInfo().getConfigPath(),
                    "eggs", "mapConfig.xml");
            holder.setMapConfigPath(f.getPath().replace('\\', '/'));
            return f;
        }
    }
}
