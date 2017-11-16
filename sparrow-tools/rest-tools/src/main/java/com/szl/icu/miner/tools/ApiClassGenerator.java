package com.szl.icu.miner.tools;

import com.szl.icu.miner.tools.data.Module;
import com.szl.icu.miner.tools.data.Modules;
import com.szl.icu.miner.tools.log.Log;
import com.szl.icu.miner.tools.resouces.ClassSearch;
import com.szl.icu.miner.tools.template.FreeMarkerUtils;
import com.szl.icu.miner.tools.template.spring.ModuleWrap;
import com.szl.icu.miner.tools.template.spring.RequestClassWrap;
import com.szl.icu.miner.tools.template.spring.RequestField;
import com.szl.icu.miner.tools.utils.ArrayUtils;
import com.szl.icu.miner.tools.utils.ClassUtils;
import com.szl.icu.miner.tools.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yzc on 2016/9/28.
 */
public class ApiClassGenerator extends AbstractGenerator {
    private String scanPackages[];
    private String restPackage;
    private String replyPackage;
    private String restConfig;
    private String codePath;
    private String modules[];
    private Class<?> parentClasses[];
    private Class<?> requestClasses[];
    private Class<?> copiedClasses[];
    private Modules modulesConfig;
    private boolean ignoreConfig;

    public String getReplyPackage() {
        return replyPackage;
    }

    public ApiClassGenerator setReplyPackage(String replyPackage) {
        this.replyPackage = replyPackage;
        return this;
    }

    public boolean isIgnoreConfig() {
        return ignoreConfig;
    }

    public ApiClassGenerator setIgnoreConfig(boolean ignoreConfig) {
        this.ignoreConfig = ignoreConfig;
        return this;
    }

    public ApiClassGenerator setLog(Log log) {
        super.setLogger(log);
        return this;
    }

    public String getCodePath() {
        return codePath;
    }

    public ApiClassGenerator setCodePath(String codePath) {
        this.codePath = codePath;
        return this;
    }

    public String[] getModules() {
        return modules;
    }

    public ApiClassGenerator setModules(String modules[]) {
        this.modules = modules;
        return this;
    }

    public String[] getScanPackages() {
        return scanPackages;
    }

    public ApiClassGenerator setScanPackages(String scanPackage[]) {
        this.scanPackages = scanPackage;
        return this;
    }

    public String getRestPackage() {
        return restPackage;
    }

    public ApiClassGenerator setRestPackage(String restPackage) {
        this.restPackage = restPackage;
        return this;
    }

    public String getRestConfig() {
        return restConfig;
    }

    public ApiClassGenerator setRestConfig(String restConfig) {
        this.restConfig = restConfig;
        return this;
    }

    public Class<?>[] getParentClasses() {
        return parentClasses;
    }

    public Class<?>[] getCopiedClasses() {
        return copiedClasses;
    }

    public ApiClassGenerator setCopiedClasses(Class<?>[] copiedClasses) {
        this.copiedClasses = copiedClasses;
        return this;
    }

    public ApiClassGenerator setParentClasses(Class<?>[] parentClasses) {
        this.parentClasses = parentClasses;
        return this;
    }

    public Class<?>[] getRequestClasses() {
        return requestClasses;
    }

    public ApiClassGenerator setRequestClasses(Class<?>[] requestClasses) {
        this.requestClasses = requestClasses;
        return this;
    }

    String doCopyClassPlusExt(Class<?> cls, File targetDir, String newPack) {
        return this.doCopyClassPlus(cls, targetDir, newPack);
    }

    RequestClassWrap copyClass(Class<?> cls, File targetDir, String reqPath, String modulePack) {
        String name = cls.getName();
        if (hasCopyClass(name))
            return null;
        try {
            Field[] fields = cls.getDeclaredFields();
            String relativePath;
            RequestClassWrap requestWrap = new RequestClassWrap();
            int lastPointPos = name.lastIndexOf('.');
            if (lastPointPos == -1)
                return null;
            String packX = this.mergePack(modulePack, name.substring(0, lastPointPos));
            requestWrap.setReqPack(packX);
            requestWrap.setReqClass(getReqClass(name.substring(lastPointPos + 1)));
            if (StringUtils.isEmpty(reqPath))
                requestWrap.setReqPath(getReqPath(requestWrap.getReqClass()));
            else
                requestWrap.setReqPath(firstCharUppercase(reqPath));
            requestWrap.setReqClassL(firstCharLowercase(requestWrap.getReqClass()));
            requestWrap.setReqPathL(firstCharLowercase(requestWrap.getReqPath()));
            requestWrap.setDesc(requestWrap.getReqMapL());

            for (Field field : fields) {
                String tn = field.getType().getName();
                boolean skip = ignoreFieldType(tn);
                if (skip)
                    continue;
                String fn = field.getName();
                RequestField requestField = new RequestField();
                requestField.setDesc(fn);
                requestField.setFieldName(firstCharLowercase(fn));
                requestField.setFieldNameX(firstCharUppercase(fn));
                requestField.setFieldType(firstCharUppercase(getReqClass(getClassSimpleName(tn))));
                if (checkTypeName(tn)) {
                    String newPk = this.doCopyClassPlusExt(field.getType(), targetDir, modulePack);
                    info(String.format(" modulePack : %s , classPack: %s , newPack : %s", modulePack, tn, newPk));
                    if (newPk != null) {
                        if (!StringUtils.equals(packX, newPk)) {
                            String realClazzName = newPk.concat(".").concat(requestField.getFieldType());
                            requestWrap.addFieldImports(realClazzName);
                        }
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
            return requestWrap;
        } finally {
            setClassCopied(name);
        }
    }

    public void generate(String generateRootDir, String[] modules) {
        File baseFile = new File(generateRootDir);
        if (!baseFile.exists())
            baseFile.mkdirs();
        if (!ArrayUtils.isEmpty(this.copiedClasses)) {
            for (Class<?> clz : this.copiedClasses) {
                copyClass(clz, baseFile, null, null);
                this.info(String.format("copy class - %s  to file: %s", clz.getName(), baseFile.getPath()));
            }
            if (StringUtils.isEmpty(this.replyPackage))
                this.replyPackage = getPackName(this.copiedClasses[0].getName());
        }
        generateController(baseFile, modules);
    }

    void generateModule(String generateRootDir, String... modules) {
        generate(generateRootDir, modules);
    }


    void generateController(File baseFile, String[] modules) {
        ClassSearch ns = ClassSearch.createInstance(Thread.currentThread().getContextClassLoader());
        for (String module : modules) {
            if (this.ignoreConfig) {
                for (String scanPackage : this.scanPackages) {
                    String basePack = String.format("%s.%s", scanPackage, module);
                    generateRequestModel(module, basePack, ns, baseFile);
                }
            } else if (this.modulesConfig != null && this.modulesConfig.contain(module)) {
                Module m = this.modulesConfig.getModule(module);
                if (m.getReqMaps() != null && !m.getReqMaps().isEmpty()) {
                    generateRestReqMap(module, this.modulesConfig.getBasePack(), baseFile, m, m.getReqMaps());
                    continue;
                }
            }
        }
    }


    void generateRequestModel(String module, String basePack, ClassSearch ns, File baseFile) {
        String patterns = "classpath:" + basePack + ".**.*";
        boolean emptyClass = ArrayUtils.isEmpty(this.parentClasses);
        if (emptyClass)
            this.info("parent class filter is empty");
        Class<?>[] classes = emptyClass ? ns.searchClass(patterns) : ns.searchClassX(this.parentClasses, patterns);
        int size = classes == null ? 0 : classes.length;
        this.info(String.format("scan base package is '%s' , class size is %s", patterns, size));
        if (size > 0)
            generateRequestModel(module, baseFile, classes);
    }

    void generateRestReqMap(String module, String basePack, File baseFile, Module md, Map<String, String> reqMap) {
        if (reqMap == null || reqMap.isEmpty())
            return;
        File file;
        ModuleWrap moduleWrap = new ModuleWrap();
        moduleWrap.setModelPack(String.format("%s.%s", this.restPackage, module));
        moduleWrap.setModule(module);
        moduleWrap.setModuleX(firstCharUppercase(module));
        String relativePath;
        boolean ft = true;
        Iterator<Map.Entry<String, String>> iterator = reqMap.entrySet().iterator();
        Map.Entry<String, String> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            String key = entry.getKey();
            String val = entry.getValue();
            Class<?> clazz = ClassUtils.loadClassNoException(this.buildClassName(val, md));
            if (clazz == null)
                continue;
            if (Modifier.isAbstract(clazz.getModifiers()))
                continue;
            this.info(String.format("extract class : %s for reqmap key = %s", clazz.getName(), key));
            String clazzPack = getClassPack(clazz.getName());
            String packX = this.mergePack(moduleWrap.getModelPack(), clazzPack);
            if (StringUtils.equals(packX, moduleWrap.getModelPack()))
                packX = String.format("%s.%s", moduleWrap.getModelPack(), cutString(clazzPack));
            RequestClassWrap requestWrap = this.copyClass(clazz, baseFile, key, packX);
            if (requestWrap == null)
                continue;
            //set para for request
            resetReqClassParam(val, requestWrap);
            moduleWrap.addFieldImports(String.format("%s.%s", requestWrap.getReqPack(), requestWrap.getReqClass()));
            String clazzVal = md.getRespMapValue(key);
            if (StringUtils.isNotEmpty(clazzVal)) {
                Class<?> respClazz = ClassUtils.loadClassNoException(this.buildClassName(clazzVal, md));
                if (respClazz != null && !Modifier.isAbstract(respClazz.getModifiers())) {
                    String npk = requestWrap.getReqPack();
                    this.copyClassPlus(respClazz, baseFile, npk);
                    String repName = getReqClass(respClazz.getSimpleName());
                    String nClz = npk.concat(".").concat(repName);
                    // response
                    requestWrap.setRespClass(repName);
                    requestWrap.addFieldImports(nClz);
                    requestWrap.setReplyValid(true);
                }
            }
            // has request ,设置pack , is first set
            if (ft) {
                moduleWrap.setReqPack(requestWrap.getReqPack());
                ft = false;
            }
            // set wrap
            moduleWrap.addRequestWrap(requestWrap);
        }
        // has no valid request , no
        if (ft)
            return;
        boolean needImportResult = false;
        for (RequestClassWrap itm : moduleWrap.getRequestWraps()) {
            if (!itm.isReplyValid()) {
                String clazzName = itm.getReqPack() + "." + itm.getReqPath() + "Reply";
                boolean hasIt = hasCopyClass(clazzName);
                itm.setReplyValid(hasIt);
                if (hasIt)
                    itm.setRespClass(clazzName);
                else
                    needImportResult = true;
            }
            if (itm.isReplyValid()) {
                for (String it : itm.getFieldImports())
                    moduleWrap.addFieldImports(it);
            }
        }
        moduleWrap.setImportResult(needImportResult);
        moduleWrap.setResultPack(this.replyPackage);
        relativePath = moduleWrap.getModelPack().replace('.', '/');
        file = new File(baseFile, relativePath);
        if (!file.exists())
            file.mkdirs();
        file = new File(file, moduleWrap.getModuleX() + "RestService.java");
        info(String.format("generate REST service for module = %s", moduleWrap.getModule()));
        FreeMarkerUtils.getInstance().writeFile("web-plus", moduleWrap, file);
    }

    void generateRequestModel(String module, File baseFile, Class<?>[] classes) {
        if (ArrayUtils.isEmpty(classes))
            return;
        File file;
        ModuleWrap moduleWrap = new ModuleWrap();
        moduleWrap.setModelPack(String.format("%s.%s", this.restPackage, module));
        moduleWrap.setModule(module);
        moduleWrap.setModuleX(firstCharUppercase(module));

        String relativePath;
        boolean first = true;
        for (Class<?> cls : classes) {
            if (Modifier.isAbstract(cls.getModifiers()))
                continue;
            this.info(String.format("extract class : %s", cls.getName()));
            RequestClassWrap requestWrap = this.copyClass(cls, baseFile, null, moduleWrap.getModelPack());
            if (requestWrap == null)
                continue;
            // has request ,设置pack
            if (isRequest(cls)) {
                if (first) {
                    moduleWrap.setReqPack(requestWrap.getReqPack());
                    first = false;
                }
                moduleWrap.addRequestWrap(requestWrap);
            }
        }
        // has no valid request , no
        if (first)
            return;
        boolean needImportResult = false;
        for (RequestClassWrap itm : moduleWrap.getRequestWraps()) {
            itm.setReplyValid(hasCopyClass(itm.getReqPack() + "." + itm.getReqPath() + "Reply"));
            if (!itm.isReplyValid()) needImportResult = true;
        }
        moduleWrap.setImportResult(needImportResult);
        moduleWrap.setResultPack(this.replyPackage);
        relativePath = moduleWrap.getModelPack().replace('.', '/');
        file = new File(baseFile, relativePath);
        if (!file.exists())
            file.mkdirs();
        file = new File(file, moduleWrap.getModuleX() + "RestService.java");
        info(String.format("generate REST service for module = %s", moduleWrap.getModule()));
        FreeMarkerUtils.getInstance().writeFile("web", moduleWrap, file);
    }

    boolean isRequest(Class<?> clazz) {
        if (ArrayUtils.isEmpty(requestClasses))
            return true;
        for (Class<?> clz : requestClasses) {
            if (clz.isAssignableFrom(clazz))
                return true;
        }
        return false;
    }

    @Override
    public void generate() {
        if (!this.ignoreConfig)
            this.modulesConfig = Modules.parse(this.restConfig);
        this.generate(this.codePath, this.modules);
    }
}
