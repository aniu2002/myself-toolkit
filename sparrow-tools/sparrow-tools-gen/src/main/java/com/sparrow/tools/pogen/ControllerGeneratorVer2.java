package com.sparrow.tools.pogen;

import com.sparrow.tools.cmd.eggs.CmdPojoGenerator;
import com.sparrow.tools.utils.FreeMarkerUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YZC
 * @version 0.1 (2013-11-5)
 * @modify
 */
public class ControllerGeneratorVer2 extends ControllerGeneratorVer1 {

    protected boolean generatePojo() {
        return false;
    }

    @Override
    protected boolean ignoreLocalSet() {
        return true;
    }

    protected boolean generatePojox() {
        CmdPojoGenerator poGenerator = new CmdPojoGenerator();
        poGenerator.setBasePath(this.getBasePath());
        poGenerator.setProperty(this.getProperty());
        //poGenerator.setModuleName("dynaModule");
        poGenerator.setModuleLabel(" ");
        poGenerator.setPackageName(this.getPackageName());
        poGenerator.setTableFilter(this.getTableFilter());
        poGenerator.setClearBefore(this.isClearBefore());
        poGenerator.setGenerateApi(this.isGenerateApi());
        try {
            poGenerator.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void afterGenerate(String packageName) {
        File configFile = MapperGenerator.getPackFile(this.getBasePath(),
                "eggs", "scan.properties");
        Map mp = new HashMap();
        mp.put("packageName", packageName);
        FreeMarkerUtils.getInstance().writeFile("scanConfig", mp, configFile);
    }

    public static void main(String args[]) {
        start(new ControllerGeneratorVer2());
    }
}
