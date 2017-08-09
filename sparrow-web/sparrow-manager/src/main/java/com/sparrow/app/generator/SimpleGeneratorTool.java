package com.sparrow.app.generator;

import com.sparrow.tools.cmd.eggs.CmdPojoGenerator;
import com.sparrow.tools.cmd.eggs.GeneratorTool;
import com.sparrow.tools.cmd.eggs.ProviderGenerator;
import com.sparrow.tools.cmd.eggs.ServiceGenerator;
import com.sparrow.tools.utils.FileUtil;

import java.io.File;

/**
 * Created by Administrator on 2015/6/1 0001.
 */
public class SimpleGeneratorTool extends GeneratorTool {

    public static void main(String args[]) {
        SimpleGeneratorTool gcd = new SimpleGeneratorTool();
        gcd.setBasePath("D:\\workspace\\_code"); //SystemConfig.SOURCE_DIR
        gcd.setModule("members");
        gcd.setLabel("成员信息");
        gcd.setPackPath("com.sparrow.app.play");

        FileUtil.clearSub(new File(gcd.getBasePath()));

        gcd.genPojo();
        gcd.genServiceCode();
    }

    protected CmdPojoGenerator getCmdPojoGenerator() {
        return new SimpleCmdPojoGenerator();
    }

    protected ServiceGenerator getServiceGenerator() {
        return new SimpleServiceGenerator();
    }

    protected ProviderGenerator getProviderGenerator() {
        return new SimpleProviderGenerator();
    }
}
