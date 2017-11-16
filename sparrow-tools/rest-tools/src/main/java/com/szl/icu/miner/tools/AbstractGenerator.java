package com.szl.icu.miner.tools;

import com.szl.icu.miner.tools.data.Module;
import com.szl.icu.miner.tools.log.Log;
import com.szl.icu.miner.tools.template.FreeMarkerUtils;
import com.szl.icu.miner.tools.template.spring.ParameterDefine;
import com.szl.icu.miner.tools.template.spring.RequestClassWrap;
import com.szl.icu.miner.tools.template.spring.RequestField;
import com.szl.icu.miner.tools.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/3.
 */
public abstract class AbstractGenerator extends Deploy {
    private Set<String> set = new HashSet<String>();
    private Log log;

    public Log getLogger() {
        return log;
    }

    void setLogger(Log log) {
        this.log = log;
    }

    void info(Object message) {
        if (this.log != null)
            this.log.info(message);
    }

    void debug(Object message) {
        if (this.log != null)
            this.log.debug(message);
    }

    boolean hasCopyClass(String className) {
        return set.contains(className);
    }

    void setClassCopied(String className) {
        set.add(className);
    }

    boolean ignoreFieldType(String type) {
        return type.startsWith("scala") || type.startsWith("akka");
    }

    /**
     * 第一个字母大写
     *
     * @param str
     * @return
     */
    String firstCharUppercase(String str) {
        if (StringUtils.isEmpty(str))
            return str;
        else
            return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
    }

    /**
     * 第一个字母大写
     *
     * @param str
     * @return
     */
    String firstCharLowercase(String str) {
        if (StringUtils.isEmpty(str))
            return str;
        else
            return String.valueOf(str.charAt(0)).toLowerCase() + str.substring(1);
    }

    String getReqClass(String name) {
        int idx = name.indexOf('$');
        if (idx != -1)
            name = name.substring(idx + 1);
        return name;
    }

    String getClassPack(String name) {
        int idx = name.lastIndexOf('.');
        if (idx != -1)
            name = name.substring(0, idx);
        return name;
    }

    String getReqPath(String name) {
        if (name.endsWith("Request"))
            return name.substring(0, name.length() - 7);
        return name;
    }

    String getClassSimpleName(String name) {
        int idx = name.lastIndexOf('.');
        if (idx != -1)
            name = name.substring(idx + 1);
        if ("int".equalsIgnoreCase(name))
            name = "Integer";
        return name;
    }

    boolean checkTypeName(String name) {
        int idx = name.indexOf('.');
        if (idx == -1) return false;
        if (name.startsWith("java.lang"))
            return false;
        return true;
    }

    String getPackName(String className) {
        int lastPointPos = className.lastIndexOf('.');
        if (lastPointPos != -1)
            className = className.substring(0, lastPointPos);
        return className;
    }

    String cutString(String p) {
        int lastPointPos = p.lastIndexOf('.');
        if (lastPointPos == -1)
            return p;
        return p.substring(lastPointPos + 1);
    }

    String buildImport(String p) {
        int lastPointPos = p.lastIndexOf('.');
        if (lastPointPos == -1)
            return p;
        String pk = p.substring(0, lastPointPos);
        String sn = p.substring(lastPointPos + 1);
        return String.format("%s.%s", pk, getReqClass(sn));
    }

    String mergePack(String newPack, String oriPack) {
        if (StringUtils.isEmpty(oriPack))
            return newPack;
        else if (StringUtils.isEmpty(newPack))
            return oriPack;
        char point = StringUtils.POINT_CHAR;
        String nPacks[] = StringUtils.split(newPack, point);
        String oPacks[] = StringUtils.split(oriPack, point);
        if (nPacks.length < oPacks.length) {
            for (int i = 0; i < nPacks.length; i++)
                oPacks[i] = nPacks[i];
            return StringUtils.combine(oPacks, point);
        } else if (nPacks.length == oPacks.length)
            return StringUtils.combine(nPacks, point, oPacks[oPacks.length - 1]);
        else
            return StringUtils.combine(nPacks, point);
    }

    void resetReqClassParam(String val, RequestClassWrap wrap) {
        String para = val;
        int idx = val.indexOf('(');
        if (idx == -1)
            return;
        para = val.substring(idx + 1);
        idx = para.indexOf(')');
        if (idx != -1)
            para = para.substring(0, idx);
        String paras[] = StringUtils.split(para, ',');
        for (String p : paras) {
            parseParam(p, wrap);
        }
    }

    void parseParam(String str, RequestClassWrap wrap) {
        if (str.equals("#"))
            return;
        else {
            String varName, typeName = "String";
            int idx = str.indexOf('[');
            if (idx == -1)
                varName = str.substring(1);
            else {
                varName = str.substring(1, idx);
                String tpStr = str.substring(idx + 1);
                idx = tpStr.indexOf(']');
                if (idx == -1)
                    typeName = tpStr;
                else
                    typeName = tpStr.substring(0, idx);
            }
            ParameterDefine define = new ParameterDefine();
            define.setParaType(typeName);
            define.setParaName(varName);
            wrap.addParameter(define);
        }
    }

    String buildClassName(String val, Module module) {
        String clz = val;
        int idx = val.indexOf('(');
        if (idx != -1)
            clz = val.substring(0, idx);
        idx = clz.indexOf('.');
        if (idx == -1)
            return String.format("%s$%s", module.getMessage(), clz);
        else
            return clz;
    }

    String doCopyClassPlus(Class<?> cls, File targetDir, String newPack) {
        String newPk = mergePack(newPack, getClassPack(cls.getName()));
        copyClassPlus(cls, targetDir, newPk);
        return newPk;
    }

    void copyClassPlus(Class<?> cls, File targetDir, String newPack) {
        String name = cls.getName();
        if (hasCopyClass(name))
            return;
        try {
            Field[] fields = cls.getDeclaredFields();
            String relativePath;
            RequestClassWrap requestWrap = new RequestClassWrap();
            int lastPointPos = name.lastIndexOf('.');
            if (lastPointPos == -1)
                return;
            requestWrap.setReqPack(newPack);
            requestWrap.setReqClass(getReqClass(name.substring(lastPointPos + 1)));
            requestWrap.setReqPath(getReqPath(requestWrap.getReqClass()));
            requestWrap.setReqClassL(firstCharLowercase(requestWrap.getReqClass()));
            requestWrap.setReqPathL(firstCharLowercase(requestWrap.getReqPath()));
            requestWrap.setDesc(requestWrap.getReqMapL());
            for (Field field : fields) {
                String tn = field.getType().getName();
                if (ignoreFieldType(tn))
                    continue;
                String fn = field.getName();
                RequestField requestField = new RequestField();
                requestField.setDesc(fn);
                requestField.setFieldName(firstCharLowercase(fn));
                requestField.setFieldNameX(firstCharUppercase(fn));
                requestField.setFieldType(firstCharUppercase(getReqClass(getClassSimpleName(tn))));
                if (checkTypeName(tn)) {
                    String npk = doCopyClassPlus(field.getType(), targetDir, newPack);
                    if (npk != null) {
                        String realClazz = npk.concat(".").concat(requestField.getFieldType());
                        if (!StringUtils.equals(newPack, npk))
                            requestWrap.addFieldImports(realClazz);
                    }
                }
                requestWrap.addField(requestField);
            }
            relativePath = requestWrap.getReqPack().replace('.', '/');
            File file = new File(targetDir, relativePath);
            if (!file.exists())
                file.mkdirs();
            file = new File(file, requestWrap.getReqClass() + ".java");
            FreeMarkerUtils.getInstance().writeFile("req", requestWrap, file);
            info(String.format(" copy class : %s - package : %s -  file : %s", requestWrap.getReqClass(), requestWrap.getReqPack(), file.getPath()));
        } finally {
            setClassCopied(name);
        }
    }
}
