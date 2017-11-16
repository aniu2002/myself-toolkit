package com.szl.icu.miner.tools;

import com.szl.icu.miner.tools.log.DefaultLog;
import com.szl.icu.miner.tools.template.swagger.ServiceRespDefinition;

/**
 * Created by yzc on 2016/9/28.
 */
public class SwaggerJsonGeneratorExt extends SwaggerJsonGenerator {

    final static ServiceRespDefinition INT_RESP;
    final static ServiceRespDefinition LONG_RESP;
    final static ServiceRespDefinition DOUBLE_RESP;
    final static ServiceRespDefinition FLOAT_RESP;
    final static ServiceRespDefinition BOOL_RESP;
    final static ServiceRespDefinition BYTE_RESP;
    final static ServiceRespDefinition BINARY_RESP;
    final static ServiceRespDefinition DATE_RESP;
    final static ServiceRespDefinition TIME_RESP;
    final static ServiceRespDefinition PASS_RESP;

    static {
        INT_RESP = loadPrimitiveRespDefine("int");
        LONG_RESP = loadPrimitiveRespDefine("long");
        DOUBLE_RESP = loadPrimitiveRespDefine("double");
        FLOAT_RESP = loadPrimitiveRespDefine("float");
        BOOL_RESP = loadPrimitiveRespDefine("boolean");
        BYTE_RESP = loadPrimitiveRespDefine("byte");
        BINARY_RESP = loadPrimitiveRespDefine("binary");
        DATE_RESP = loadPrimitiveRespDefine("date");
        TIME_RESP = loadPrimitiveRespDefine("time");
        PASS_RESP = loadPrimitiveRespDefine("password");
    }


    @Override
    ServiceRespDefinition wrapResponseDefinition(String name) {
        if ("integer".equals(name) || "int".equals(name)) {
            return INT_RESP;
        } else if ("long".equals(name)) {
            return LONG_RESP;
        } else if ("double".equals(name) || "number".equals(name)) {
            return DOUBLE_RESP;
        } else if ("float".equals(name)) {
            return FLOAT_RESP;
        } else if ("string".equals(name)) {
            return STRING_RESP;
        } else if ("boolean".equals(name)) {
            return BOOL_RESP;
        } else if ("byte".equals(name)) {
            return BYTE_RESP;
        } else if ("binary".equals(name)) {
            return BINARY_RESP;
        } else if ("date".equals(name)) {
            return DATE_RESP;
        } else if ("dateTime".equals(name) || "time".equals(name)) {
            return TIME_RESP;
        } else if ("password".equals(name)) {
            return PASS_RESP;
        } else {
            return super.wrapResponseDefinition(name);
        }
    }

    public static void main(String args[]) {
        try {
            new SwaggerJsonGeneratorExt()
                    .setLog(new DefaultLog())
                    .setRestConfig("agent-rest.conf")
                    .setModules(new String[]{"test" })
                    .setCodePath("D:/sources")
                    .setGenerateMarkDown(true)
                    .setGenerateHtmlDocs(true)
                    .setGenerateSwagger(true)
                    .generate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
