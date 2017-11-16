package com.szl.icu.miner.tools;

import com.szl.icu.miner.tools.data.ContentType;
import com.szl.icu.miner.tools.data.Module;
import com.szl.icu.miner.tools.data.Modules;
import com.szl.icu.miner.tools.log.Log;
import com.szl.icu.miner.tools.template.FreeMarkerUtils;
import com.szl.icu.miner.tools.template.swagger.*;
import com.szl.icu.miner.tools.utils.ClassUtils;
import com.szl.icu.miner.tools.utils.FileIOUtil;
import com.szl.icu.miner.tools.utils.JsonFormat;
import com.szl.icu.miner.tools.utils.StringUtils;
import org.markdown4j.Markdown4jProcessor;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by yzc on 2016/9/28.
 */
public class SwaggerJsonGenerator extends AbstractGenerator {
    final static ObjectDefinition DEFAULT_OBJ = new ObjectDefinition();
    final static ServiceRespDefinition STRING_RESP;
    private Map<String, ObjectDefinition> objMap = new HashMap<String, ObjectDefinition>();
    private String restConfig;
    private String codePath;
    private String modules[];
    private Modules modulesConfig;
    private boolean generateSwagger;
    private boolean generateMarkDown;
    private boolean generateHtmlDocs;

    static {
        ServiceRespDefinition respDefinition = new ServiceRespDefinition();
        respDefinition.setDescription("String text");
        respDefinition.setComplex(false);
        respDefinition.setStatus("200");
        respDefinition.setType("string");
        respDefinition.setRef(respDefinition.getType());
        STRING_RESP = respDefinition;
    }

    public boolean isGenerateSwagger() {
        return generateSwagger;
    }

    public SwaggerJsonGenerator setGenerateSwagger(boolean generateSwagger) {
        this.generateSwagger = generateSwagger;
        return this;
    }

    public boolean isGenerateMarkDown() {
        return generateMarkDown;
    }

    public SwaggerJsonGenerator setGenerateMarkDown(boolean generateMarkDown) {
        this.generateMarkDown = generateMarkDown;
        return this;
    }

    public boolean isGenerateHtmlDocs() {
        return generateHtmlDocs;
    }

    public SwaggerJsonGenerator setGenerateHtmlDocs(boolean generateHtmlDocs) {
        this.generateHtmlDocs = generateHtmlDocs;
        return this;
    }

    public SwaggerJsonGenerator setLog(Log log) {
        super.setLogger(log);
        return this;
    }

    public String getCodePath() {
        return codePath;
    }

    public SwaggerJsonGenerator setCodePath(String codePath) {
        this.codePath = codePath;
        return this;
    }

    public String[] getModules() {
        return modules;
    }

    public SwaggerJsonGenerator setModules(String modules[]) {
        this.modules = modules;
        return this;
    }

    public String getRestConfig() {
        return restConfig;
    }

    public SwaggerJsonGenerator setRestConfig(String restConfig) {
        this.restConfig = restConfig;
        return this;
    }

    public void generate(String generateRootDir, String[] modules) {
        File baseFile = new File(generateRootDir);
        if (!baseFile.exists())
            baseFile.mkdirs();
        Swagger swagger = generateController(modules);

        if (this.isGenerateSwagger()) {
            File file = new File(baseFile, "swagger.json");
            String content = FreeMarkerUtils.getInstance().writeString("swagger-json", swagger);
            FileIOUtil.writeFile(file, JsonFormat.clearBlankLine(content), FileIOUtil.DEFAULT_ENCODING);
        }
        String markDown;
        try {
            if (this.isGenerateHtmlDocs()) {
                markDown = FreeMarkerUtils.getInstance().writeString("swagger-html", swagger);
                String style = FileIOUtil.readString("classpath:markdown.css");
                String html = new StringBuilder().append("<!DOCTYPE html><html><head><title>test</title>")
                        .append("<meta charset=\"utf-8\">")
                        .append("<style>").append(style).append("</style>")
                        .append("</head><body class=\"markdown-preview\" data-use-github-style>")
                        .append(new Markdown4jProcessor().addHtmlAttribute("style", "text-align:left", "td").process(markDown))
                        .append("</body></html>").toString();
                FileIOUtil.writeFile(new File(baseFile, String.format("%s.html", this.getProjectName())), html, FileIOUtil.DEFAULT_ENCODING);
            }
            if (this.isGenerateMarkDown()) {
                markDown = FreeMarkerUtils.getInstance().writeString("swagger-markdown", swagger);
                FileIOUtil.writeFile(new File(baseFile, String.format("%s.md", this.getProjectName())), markDown, FileIOUtil.DEFAULT_ENCODING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Swagger generateController(String[] modules) {
        Swagger swagger = new Swagger();
        swagger.setDescription(this.getDescription());
        swagger.setAuthor(this.getAuthor());
        swagger.setContext(this.getContext());
        swagger.setEmail(this.getEmail());
        swagger.setHost(this.getHost());
        swagger.setSite(this.getSite());
        swagger.setTitle(this.getTitle());
        swagger.setVersion(this.getVersion());
        for (String module : modules) {
            if (this.modulesConfig != null && this.modulesConfig.contain(module)) {
                Module m = this.modulesConfig.getModule(module);
                if (m.getReqMaps() != null && !m.getReqMaps().isEmpty()) {
                    generateRestReqMap(m, m.getReqMaps(), swagger);
                    continue;
                }
            }
        }
        return swagger;
    }

    ObjectPropDefinition extractProp(Field field) {
        ObjectPropDefinition propDef = new ObjectPropDefinition();
        String name = field.getType().getSimpleName();
        boolean isObj = Parser.isObject(field.getType().getName());
        propDef.setName(field.getName());
        propDef.setComplex(isObj);
        if (isObj)
            propDef.setType(name);
        else
            Parser.fillPropType(name, propDef);
        return propDef;
    }

    ObjectDefinition extractObject(Class<?> cls, Swagger swagger) {
        String name = cls.getName();
        ObjectDefinition objectDefine = DEFAULT_OBJ;
        if (this.hasCopyClass(name))
            return this.objMap.get(name);
        try {
            Field[] fields = cls.getDeclaredFields();
            objectDefine = new ObjectDefinition();
            objectDefine.setClazz(cls.getName());
            objectDefine.setName(cls.getSimpleName());
            objectDefine.setType("object");
            for (Field field : fields) {
                if ("this$0".equals(field.getName()))
                    continue;
                boolean skip = this.ignoreFieldType(field.getType().getName());
                if (skip)
                    continue;
                ObjectPropDefinition propDef = this.extractProp(field);
                if (propDef.isComplex())
                    this.extractObject(field.getType(), swagger);
                objectDefine.addProp(propDef);
            }
            swagger.addObjectDefine(objectDefine);
            return objectDefine;
        } finally {
            this.objMap.put(name, objectDefine);
            this.setClassCopied(name);
        }
    }

    void parseReqParams(RequestMapMeta mapMeta, List<ServiceParamDefinition> list, ObjectDefinition reqDef) {
        String para = mapMeta.getRequestWrapArgs();
        boolean isPost = StringUtils.equals(Parser.POST, mapMeta.getMethod());
        if (StringUtils.isEmpty(para)) {
            if (isPost) {
                Parser.fetchParam("#", list, reqDef, isPost);
                reqDef.getProps();
            } else if (reqDef.getProps() != null) {
                for (ObjectPropDefinition pf : reqDef.getProps()) {
                    list.add(Parser.createRequestParam(pf));
                }
                //    Parser.fetchParam("!", list, reqDef, isPost);
            }
            return;
        }
        String paras[] = StringUtils.split(para, ',');
        for (String p : paras) {
            ServiceParamDefinition df = Parser.fetchParam(p, list, reqDef, isPost);
            this.info(String.format("\t - extract arguments for %s , paramDefinition = %s", p, df));
            this.clearPathParamDef(mapMeta, df);
        }
        mapMeta.addPathPramToList(list);
    }

    void clearPathParamDef(RequestMapMeta mapMeta, ServiceParamDefinition def) {
        if (def == null)
            return;
        if (StringUtils.equals(def.getType(), "path")) {
            if (mapMeta.contains(def.getName()))
                mapMeta.remove(def.getName());
        }
    }

    void parseReqParams(RequestMapMeta mapMeta, List<ServiceParamDefinition> list) {
        String para = mapMeta.getRequestWrapArgs();
        String type = mapMeta.getRequestWrap();
        boolean isPost = StringUtils.equals(Parser.POST, mapMeta.getMethod());
        if (StringUtils.isEmpty(para)) {
            if (isPost)
                Parser.fetchParam("#", list, type, isPost);
            else
                Parser.fetchParam("$param", list, type, isPost);
            return;
        }
        String paras[] = StringUtils.split(para, ',');
        for (String p : paras) {
            this.clearPathParamDef(mapMeta, Parser.fetchParam(p, list, type, isPost));
        }
        mapMeta.addPathPramToList(list);
    }

    String genClassName(String clz, Module module) {
        int idx = clz.indexOf('.');
        if (idx == -1)
            return String.format("%s$%s", module.getMessage(), clz);
        else
            return clz;
    }

    void generateRestReqMap(Module md, Map<String, String> reqMap, Swagger swagger) {
        if (reqMap == null || reqMap.isEmpty())
            return;
        String serviceName = this.firstCharUppercase(md.getName()) + " Service";
        swagger.addServiceTag(new ServiceTag(serviceName));
        String  key , respWrap;
        Iterator<Map.Entry<String, String>> ite = reqMap.entrySet().iterator();
        Map.Entry<String, String> entry;
        while (ite.hasNext()) {
            entry = ite.next();
            key = entry.getKey();
            RequestMapMeta mapMeta = Parser.parse(entry.getValue());
            mapMeta.setModule(md.getName());
            Parser.parsePathParameter(key, mapMeta);

            this.info(mapMeta);

            String summary = swagger.isRootContext() ? String.format("/%s/%s", mapMeta.getModule(), key)
                    : String.format("%s/%s/%s", swagger.getContext(), mapMeta.getModule(), key);
            ServiceDefinition srvDef = new ServiceDefinition();
            srvDef.setMethod(mapMeta.getMethod());
            if (mapMeta.isPathVariable()) {
                srvDef.setOperationId(String.format("%s_%s", mapMeta.getModule(), Math.abs(key.hashCode())));
                if (md.containsDescMapKey(key))
                    srvDef.setDescription(String.format("%s Service Endpoint : %s",
                            md.getDescMapValue(key), summary));
                else
                    srvDef.setDescription(String.format("Service Endpoint : %s", summary));
            } else {
                srvDef.setOperationId(String.format("%s_%s", mapMeta.getModule(), key));
                if (md.containsDescMapKey(key))
                    srvDef.setDescription(
                            String.format("%s Service Endpoint : %s",
                                    md.getDescMapValue(key),
                                    this.firstCharUppercase(summary)));
                else
                    srvDef.setDescription(String.format("Service Endpoint : %s",
                            this.firstCharUppercase(summary)));
            }
            srvDef.setPath(String.format("/%s/%s", md.getName(), key));
            srvDef.setTag(serviceName);
            srvDef.setSummary(summary);

            this.info(serviceName + " - " + summary);

            boolean isFormReq = mapMeta.isFormRequest();
            if (isFormReq)
                srvDef.setConsume(ContentType.FORM);
            if (Parser.isPrimitive(mapMeta.getRequestWrap())) {
                srvDef.setParams(this.wrapServiceParams(mapMeta, swagger));
            } else {
                Class<?> clazz = ClassUtils.loadClassNoException(this.genClassName(mapMeta.getRequestWrap(), md));
                if (clazz != null && !Modifier.isAbstract(clazz.getModifiers())) {
                    this.info(String.format("swagger extract class : %s for reqmap key = %s", clazz.getName(), key));
                    srvDef.setParams(this.wrapServiceParams(clazz, mapMeta, swagger));
                }
            }
            respWrap = md.containsRespMapKey(key) ? md.getRespMapValue(key) : mapMeta.getResponseWrap();
            if (StringUtils.isNotEmpty(respWrap)) {
                if (StringUtils.equals(respWrap, "^json")) {
                    srvDef.setResponse(STRING_RESP);
                    srvDef.setProduce(ContentType.JSON);
                } else if (StringUtils.equals(respWrap, "^xml")) {
                    srvDef.setResponse(STRING_RESP);
                    srvDef.setProduce(ContentType.XML);
                } else if (StringUtils.equals(respWrap, "^html")) {
                    srvDef.setResponse(STRING_RESP);
                    srvDef.setProduce(ContentType.HTML);
                } else if (Parser.isPrimitive(respWrap)) {
                    srvDef.setResponse(this.wrapResponseDefinition(respWrap));
                    srvDef.setProduce(ContentType.TEXT);
                } else {
                    Class<?> respClazz = ClassUtils.loadClassNoException(this.buildClassName(respWrap, md));
                    if (respClazz != null && !Modifier.isAbstract(respClazz.getModifiers())) {
                        srvDef.setResponse(this.wrapResponseDefinition(respClazz, swagger));
                    }
                }
            } else {
                srvDef.setResponse(STRING_RESP);
                srvDef.setProduce(ContentType.TEXT);
            }
            swagger.addServiceDefine(srvDef);
        }
    }

    List<ServiceParamDefinition> wrapServiceParams(Class<?> reqClazz, RequestMapMeta mapMeta, Swagger swagger) {
        ObjectDefinition reqDef = this.extractObject(reqClazz, swagger);
        List<ServiceParamDefinition> list = new ArrayList<ServiceParamDefinition>();
        this.parseReqParams(mapMeta, list, reqDef);
        return list;
    }

    List<ServiceParamDefinition> wrapServiceParams(RequestMapMeta mapMeta, Swagger swagger) {
        List<ServiceParamDefinition> list = new ArrayList<ServiceParamDefinition>();
        this.parseReqParams(mapMeta, list);
        return list;
    }

    static ServiceRespDefinition loadPrimitiveRespDefine(String name) {
        ServiceRespDefinition respDefinition = new ServiceRespDefinition();
        respDefinition.setDescription(name + " - type");
        respDefinition.setComplex(false);
        respDefinition.setStatus("200");
        Parser.fillPropType(name, respDefinition);
        respDefinition.setRef(respDefinition.getType());
        return respDefinition;
    }

    ServiceRespDefinition wrapResponseDefinition(String name) {
        return loadPrimitiveRespDefine(name);
    }

    ServiceRespDefinition wrapResponseDefinition(Class<?> respClazz, Swagger swagger) {
        ObjectDefinition respDef = this.extractObject(respClazz, swagger);
        ServiceRespDefinition respDefinition = new ServiceRespDefinition();
        respDefinition.setDescription(respDef.getName());
        respDefinition.setRef(respDef.getName());
        respDefinition.setComplex(true);
        respDefinition.setStatus("200");
        return respDefinition;
    }

    @Override
    public void generate() {
        this.modulesConfig = Modules.parse(this.restConfig);
        this.generate(this.codePath, this.modules);
    }
}
