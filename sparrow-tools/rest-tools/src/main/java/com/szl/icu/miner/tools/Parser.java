package com.szl.icu.miner.tools;

import com.szl.icu.miner.tools.template.swagger.*;
import com.szl.icu.miner.tools.utils.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */
public abstract class Parser {
    static final String POST = "post";
    static final String GET = "get";

    public static RequestMapMeta parse(String express) {
        RequestMapMeta requestMapMeta = new RequestMapMeta();
        parseMethod(express, requestMapMeta);
        return requestMapMeta;
    }

    static void parseMethod(String express, RequestMapMeta mapMeta) {
        if (StringUtils.isEmpty(express))
            return;
        String nextToken;
        char c = express.charAt(0);
        if (c == '~') {
            mapMeta.setMethod(POST);
            mapMeta.setFormRequest(true);
            nextToken = express.substring(1);
        } else {
            int idx = express.indexOf('>');
            if (idx != -1) {
                mapMeta.setMethod(express.substring(0, idx).toLowerCase());
                mapMeta.setJson(true);
                nextToken = express.substring(idx + 1);
            } else {
                mapMeta.setMethod(GET);
                nextToken = express;
            }
        }
        parseRequestWrap(nextToken, mapMeta);
    }

    static String convertType(String str) {
        if (StringUtils.isEmpty(str) || "-".equals(str) || "_".equals(str))
            return "String";
        else
            return str;
    }

    static void parseRequestWrap(String express, RequestMapMeta mapMeta) {
        int idx = express.indexOf('(');
        if (idx != -1) {
            mapMeta.setRequestWrap(convertType(express.substring(0, idx)));
            String nextToken = express.substring(idx + 1);
            parseRequestWrapArgs(nextToken, mapMeta);
        } else {
            idx = express.indexOf("=>");
            if (idx != -1) {
                mapMeta.setRequestWrap(convertType(express.substring(0, idx).trim()));
                mapMeta.setResponseWrap(express.substring(idx + 2).trim());
            } else {
                mapMeta.setRequestWrap(convertType(express));
            }
        }
    }

    static boolean hasChar(String str, char cs[]) {
        for (char c : cs) {
            if (str.indexOf(c) != -1) return true;
        }
        return false;
    }

    static final char[] FORM_CHAR_FLAG = new char[]{'?', '!'};

    static void parseRequestWrapArgs(String express, RequestMapMeta mapMeta) {
        int idx = express.indexOf(')');
        if (idx != -1) {
            String str = express.substring(0, idx);
            mapMeta.setRequestWrapArgs(str);
            if (hasChar(str, FORM_CHAR_FLAG) && !mapMeta.isFormRequest()) {
                if (!StringUtils.equalsIgnoreCase(mapMeta.getMethod(), "post"))
                    throw new RuntimeException("Contain form para char '?|!', need post form data , but method is get");
                mapMeta.setFormRequest(true);
            }
            parseResponseWrap(express.substring(idx + 1), mapMeta);
        } else
            mapMeta.setRequestWrapArgs(express);
    }

    static void parseResponseWrap(String express, RequestMapMeta mapMeta) {
        int idx = express.indexOf("=>");
        if (idx != -1)
            mapMeta.setResponseWrap(express.substring(idx + 2).trim());
        else
            mapMeta.setResponseWrap(express);
    }

    static ServiceParamDefinition fetchParam(String str, List<ServiceParamDefinition> list, String type, boolean isPost) {
        ServiceParamDefinition paramDefine = null;
        char c = str.charAt(0);
        switch (c) {
            case '#':
                if (isPost)
                    paramDefine = createBodyParam(str.substring(1), type, null);
                else
                    System.out.println(" method is not POST , can not fetch request body");
                break;
            case '$':
                paramDefine = createQueryParam(str.substring(1), type);
                break;
            case '`':
                paramDefine = createPathParam(str.substring(1), type);
                break;
            case '!':
                if (isPost)
                    paramDefine = createFormParam(str.substring(1), type);
                else
                    paramDefine = createFormParam(str.substring(1), type);
                paramDefine.setRequired(false);
                break;
            case '?':
                paramDefine = createFormParam(str.substring(1), type);
                break;
            default:
                paramDefine = null;
        }
        if (paramDefine != null)
            list.add(paramDefine);
        return paramDefine;
    }

    static ServiceParamDefinition fetchParam(String str, List<ServiceParamDefinition> list, ObjectDefinition reqDef, boolean isPost) {
        ServiceParamDefinition paramDefine = null;
        char c = str.charAt(0);
        String param[] = parseParam(str);
        switch (c) {
            case '#':
                if (isPost) {
                    if (StringUtils.isEmpty(param[1]))
                        paramDefine = createBodyParam(param[0], param[1], reqDef);
                    else
                        paramDefine = createBodyParam(param[0], param[1], null);
                } else
                    System.out.println(" method is not POST , can not fetch request body");
                break;
            case '$':
                paramDefine = createQueryParam(param[0], param[1]);
                break;
            case '`':
                paramDefine = createPathParam(param[0], param[1]);
                break;
            case '~':
                String params[] = splitQueryParam(str.substring(1));
                for (String p : params)
                    fetchParam(p, list, reqDef, isPost);
                break;
            case '!':
                if (isPost)
                    paramDefine = createFormParam(param[0], param[1]);
                else
                    paramDefine = createQueryParam(param[0], param[1]);
                paramDefine.setRequired(false);
                break;
            case '?':
                paramDefine = createFormParam(param[0], param[1]);
                break;
            default:
                paramDefine = null;
        }
        if (paramDefine != null)
            list.add(paramDefine);
        return paramDefine;
    }

    static ServiceParamDefinition createPathParam(String paraName, String type) {
        return createRequestParam(paraName, type, "path");
    }

    static ServiceParamDefinition createQueryParam(String paraName, String type) {
        return createRequestParam(paraName, type, "query");
    }

    static ServiceParamDefinition createFormParam(String paraName, String type) {
        return createRequestParam(paraName, type, "formData");
    }

    static ServiceParamDefinition createBodyParam(String paraName, String type, ObjectDefinition reqDef) {
        ServiceParamDefinition paramDefine = new ServiceParamDefinition();
        paramDefine.setRequired(true);
        if (reqDef != null) {
            paramDefine.setName("");
            paramDefine.setDescription(reqDef.getName());
            paramDefine.setRef(reqDef.getName());
            paramDefine.setComplex(true);
        } else {
            paramDefine.setName(paraName);
            paramDefine.setDescription(paraName);
            fillPropType(type, paramDefine);
            paramDefine.setRef(paramDefine.getType());
        }
        paramDefine.setType("body");
        return paramDefine;
    }

    static ServiceParamDefinition createRequestParam(String paraName, String type, String reqType) {
        return createRequestParam(paraName, type, reqType, isObject(type));
    }

    static ServiceParamDefinition createRequestParam(ObjectPropDefinition pf) {
        return createRequestParam(pf.getName(), pf.getType(), "query", pf.isComplex());
    }

    private static ServiceParamDefinition createRequestParam(String paraName, String type, String reqType, boolean isObject) {
        ServiceParamDefinition paramDefine = new ServiceParamDefinition();
        paramDefine.setName(paraName);
        paramDefine.setRequired(true);
        paramDefine.setComplex(isObject);
        if (isObject) {
            paramDefine.setRef(getClassSimpleName(type));
            paramDefine.setDescription(paramDefine.getRef());
        } else {
            fillPropType(type, paramDefine);
            paramDefine.setDescription(paraName);
            paramDefine.setRef(paramDefine.getType());
        }
        paramDefine.setType(reqType);
        return paramDefine;
    }

    static String getClassSimpleName(String name) {
        int idx = name.lastIndexOf('.');
        if (idx != -1)
            name = name.substring(idx + 1);
        if ("int".equalsIgnoreCase(name))
            name = "Integer";
        return name;
    }

    static void fillPropType(String name, ObjectPropType propType) {
        name = name.toLowerCase();
        if ("integer".equals(name) || "int".equals(name)) {
            propType.setType("integer");
            propType.setFormat("int32");
        } else if ("long".equals(name)) {
            propType.setType("integer");
            propType.setFormat("int64");
        } else if ("double".equals(name) || "number".equals(name)) {
            propType.setType("number");
            propType.setFormat("double");
        } else if ("float".equals(name)) {
            propType.setType("number");
            propType.setFormat("float");
        } else if ("string".equals(name)) {
            propType.setType("string");
        } else if ("boolean".equals(name)) {
            propType.setType("boolean");
        } else if ("byte".equals(name)) {
            propType.setType("string");
            propType.setFormat("byte");
        } else if ("binary".equals(name)) {
            propType.setType("string");
            propType.setFormat("binary");
        } else if ("date".equals(name)) {
            propType.setType("string");
            propType.setFormat("date");
        } else if ("dateTime".equals(name) || "time".equals(name)) {
            propType.setType("string");
            propType.setFormat("date-time");
        } else if ("password".equals(name)) {
            propType.setType("string");
            propType.setFormat("password");
        } else {
            System.out.println(" unkown type " + name);
        }
    }

    static String[] parseParam(String para) {
        String var = "", type = "string";
        int idx = para.indexOf('[');
        if (idx == -1) {
            if (para.length() > 1)
                var = para.substring(1);
        } else {
            var = para.substring(1, idx);
            String tpStr = para.substring(idx + 1);
            idx = tpStr.indexOf(']');
            if (idx == -1)
                type = tpStr;
            else
                type = tpStr.substring(0, idx);
        }
        return new String[]{var, type};
    }

    static String[] splitQueryParam(String paras) {
        return StringUtils.split(paras, ',');
    }

    static boolean isObject(String name) {
        int idx = name.indexOf('.');
        if (idx == -1) return false;
        if (name.startsWith("java.lang"))
            return false;
        return true;
    }

    static boolean isPrimitive(String str) {
        str = (str != null ? str.toLowerCase() : null);
        return "string".equals(str) || "int".equals(str)
                || "integer".equals(str) || "long".equals(str)
                || "double".equals(str) || "float".equals(str)
                || "boolean".equals(str) || "bool".equals(str)
                || "number".equals(str);
    }

    static void parsePathParameter(String str, RequestMapMeta mapMeta) {
        int idx = str.indexOf('{');
        if (idx == -1)
            return  ;
        StringBuilder paraSb = new StringBuilder();
        String paraName;
        boolean flag = false;
        char[] arr = str.toCharArray();
        for (char c : arr) {
            if (c == '{') {
                paraSb.setLength(0);
                flag = true;
            } else if (c == '}') {
                paraName = paraSb.toString();
                mapMeta.addParamDefine(createPathParam(paraName, "String"));
                flag = false;
            } else if (flag)
                paraSb.append(c);
        }
        if (!mapMeta.pathParamsEmpty())
            mapMeta.setPathVariable(true);
    }
}
