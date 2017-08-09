package com.sparrow.data.service.imports.config;

import com.sparrow.common.source.OptionItem;
import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.data.service.MessageException;
import com.sparrow.data.service.imports.dao.ImportTemplateDao;
import com.sparrow.data.service.imports.data.DataType;
import com.sparrow.data.service.imports.data.ImportTemplate;
import com.sparrow.data.service.imports.data.ImportTemplateItem;
import com.sparrow.data.tools.store.FileStore;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.FileFilter;
import java.util.*;


/**
 * @author YZC
 * @version 1.0 (2014-3-14)
 * @modify
 */
public class ImportConfigurationImpl implements ImportConfiguration {
    final Map<String, ImportTemplate> cache = new HashMap<String, ImportTemplate>();
    private List<OptionItem> impTemplates;
    private List<OptionItem> expTemplates;
    private ImportTemplateDao importTemplateDao;

    public ImportTemplateDao getImportTemplateDao() {
        return importTemplateDao;
    }

    public void setImportTemplateDao(ImportTemplateDao importTemplateDao) {
        this.importTemplateDao = importTemplateDao;
    }

    public List<OptionItem> getTemplates(boolean isImp) {
        if (isImp)
            return this.impTemplates;
        else
            return this.expTemplates;
    }

    @Override
    public void saveImportTemplate(String name, File importConfig,
                                   File excelTemplate) {
        this.importTemplateDao.saveImportTemplate(name, importConfig,
                excelTemplate);
    }

    static final String ALL = "all";
    static final String IMP = "import";
    static final String EXP = "export";

    public void initialize() {
        File file = FileStore.BASE_PATH;
        if (file.isDirectory()) {
            File templateFiles[] = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isFile() && "xml".equals(PathResolver.getExtension(f.getName()));
                }
            });
            for (int i = 0; i < templateFiles.length; i++) {
                File tmp = templateFiles[i];
                this.getImportTemplate(PathResolver.trimExtension(tmp.getName()));
            }
            Iterator<Map.Entry<String, ImportTemplate>> iterator = this.cache.entrySet().iterator();
            Map.Entry<String, ImportTemplate> entry;
            ImportTemplate template;
            OptionItem item;
            String key;
            this.impTemplates = new ArrayList<OptionItem>();
            this.expTemplates = new ArrayList<OptionItem>();
            while (iterator.hasNext()) {
                entry = iterator.next();
                key = entry.getKey();
                if (key.indexOf(':') == -1)
                    continue;
                template = entry.getValue();
                item = new OptionItem();
                item.setCode(template.getName());
                item.setName(template.getLabel());
                if (StringUtils.isEmpty(template.getImp()) || StringUtils.equalsIgnoreCase(template.getImp(), ALL)) {
                    this.impTemplates.add(item);
                    this.expTemplates.add(item);
                } else if (StringUtils.equalsIgnoreCase(template.getImp(), IMP))
                    this.impTemplates.add(item);
                else if (StringUtils.equalsIgnoreCase(template.getImp(), EXP))
                    this.expTemplates.add(item);
            }
        }
    }

    String getTemplateName(String name) {
        String tName = name;
        int idx = name.indexOf(':');
        if (idx != -1)
            tName = name.substring(0, idx);
        return tName;
    }

    String getTemplateNameEx(String name) {
        String tName = name;
        int idx = name.indexOf(':');
        if (idx != -1)
            tName = name.replace(':', '/');
        else
            tName = name + "/" + name;
        return tName;
    }

    @Override
    public ImportTemplate getImportTemplate(String name) {
        if (StringUtils.isEmpty(name))
            return null;
        ImportTemplate template = this.cache.get(name);
        if (template == null) {
            String tmpName = this.getTemplateName(name);
            String content = this.importTemplateDao
                    .getImportTemplateContent(tmpName);
            if (StringUtils.isEmpty(content))
                throw new MessageException("import", "导入导出模板[" + name + "]不存在");
            this.extract(content, tmpName);
            template = this.cache.get(name);
        }
        if (template == null)
            throw new MessageException("import", "导入导出模板[" + name + "]不存在");
        return template;
    }

    void extract(String content, String baseName) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(content);
        } catch (DocumentException e) {
            e.printStackTrace();
            return;
        }
        // 获取根节点
        Element rootEle = doc.getRootElement();
        Iterator<?> iterator = rootEle.elementIterator("config");
        boolean first = true;
        while (iterator.hasNext()) {
            Element item = (Element) iterator.next();
            ImportTemplate template = this.extract(item, baseName);
            if (first) {
                first = false;
                this.cache.put(baseName, template);
            }
            this.cache.put(template.getName(), template);
        }
    }

    ImportTemplate extract(Element configItem, String baseName) {
        ImportTemplate template = new ImportTemplate();
        String simpleName = configItem.attributeValue("name");
        if (StringUtils.isEmpty(simpleName))
            template.setName(baseName);
        else
            template.setName(baseName + ":" + simpleName);
        template.setImp(configItem.attributeValue("type"));
        Element sqlEle = configItem.element("sql");
        if (sqlEle != null)
            template.setSql(sqlEle.getTextTrim());
        Element setEle = configItem.element("excel-setting");
        template.setLabel(configItem.elementTextTrim("description"));
        String startSheet = setEle.elementTextTrim("start-sheet");
        String startRow = setEle.elementTextTrim("start-row");
        String startCol = setEle.elementTextTrim("start-col");
        String colLimit = setEle.elementTextTrim("column-limit");
        String exportMax = setEle.elementTextTrim("export-max");
        String sheetRows = setEle.elementTextTrim("sheet-rows");
        template.setStartSheet(this.covertToInt(startSheet, 0));
        template.setStartRow(this.covertToInt(startRow, 0));
        template.setStartCol(this.covertToInt(startCol, 0));
        template.setLimit(this.covertToInt(colLimit, 0));
        template.setExportMax(this.covertToInt(exportMax, 65535));
        template.setSheetRows(this.covertToInt(sheetRows, 65535));
        template.setExportSql(configItem.elementTextTrim("exportSql"));
        this.extractItem(template, configItem, baseName);
        return template;
    }

    void extractItem(ImportTemplate template, Element configItem,
                     String baseName) {
        Map<String, ImportTemplateItem> itemsMap = new HashMap<String, ImportTemplateItem>();
        Map<String, ImportTemplateItem> expItemsMap = new HashMap<String, ImportTemplateItem>();
        Element element = configItem.element("items");
        Iterator<?> iterator = element.elementIterator("item");
        int maxIdx = 0;
        while (iterator.hasNext()) {
            Element item = (Element) iterator.next();
            String name = item.attributeValue("name");
            String type = item.attributeValue("type");
            String idx = item.attributeValue("idx");
            String flag = item.attributeValue("flag");
            String expName = item.attributeValue("expName");
            String label = item.attributeValue("label");
            String format = item.attributeValue("format");
            String render = item.attributeValue("render");
            String validate = item.attributeValue("validate");
            if (StringUtils.isEmpty(expName))
                expName = name;
            expName = ImportConfigHelper.underScoreName(expName);
            ImportTemplateItem exItem = new ImportTemplateItem();
            exItem.setName(name);
            exItem.setRender(render);
            if (StringUtils.isEmpty(label))
                exItem.setLabel(name);
            else
                exItem.setLabel(label);
            if ("true".equals(format))
                exItem.setFormat(true);
            exItem.setType(this.covertToDataType(type));
            exItem.setIndex(this.covertToInt(idx, -1));
            exItem.setValidate(validate);

            if (exItem.getIndex() > maxIdx)
                maxIdx = exItem.getIndex();
            // 默认既导出，也导入
            if (StringUtils.isEmpty(flag)) {
                itemsMap.put(name, exItem);
                expItemsMap.put(expName, exItem);
            } else if ("1".equals(flag)) {
                // 仅导入
                itemsMap.put(name, exItem);
            } else {
                // 仅导出
                expItemsMap.put(expName, exItem);
            }
        }
        if (template.getLimit() <= maxIdx)
            template.setLimit(maxIdx + 1);
        template.setParaItemMap(itemsMap);
        template.setExpMaxIdx(maxIdx);
        template.setExpParaItemMap(expItemsMap);
    }

    int covertToInt(String idxStr, int defaultV) {
        if (StringUtils.isEmpty(idxStr))
            return defaultV;
        int idx = 0;
        try {
            idx = Integer.parseInt(idxStr);
            if (idx < 0)
                return defaultV;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultV;
        }
        return idx;
    }

    DataType covertToDataType(String type) {
        DataType dataType = DataType.Str;
        if ("string".equals(type))
            dataType = DataType.Str;
        else if ("int".equals(type))
            dataType = DataType.Int;
        else if ("long".equals(type))
            dataType = DataType.Long;
        else if ("date".equals(type))
            dataType = DataType.Date;
        else if ("time".equals(type))
            dataType = DataType.Time;
        else if ("float".equals(type))
            dataType = DataType.Float;
        else if ("double".equals(type))
            dataType = DataType.Double;
        else if ("number".equals(type))
            dataType = DataType.Num;
        return dataType;
    }

    @Override
    public void deleteImportTemplate(String name) {
        this.importTemplateDao.deleteImportTemplate(name);
    }

    @Override
    public void updateImportTemplate(String name, File importConfig,
                                     File excelTemplate) {
        this.importTemplateDao.updateImportTemplate(name, importConfig,
                excelTemplate);
    }

    @Override
    public File getExcelTemplateFile(String name) throws Exception {
        String templateName = this.getTemplateNameEx(name);
        File file = FileStore.getExcelTemplateFile(templateName);
        if (!file.exists())
            this.importTemplateDao.loadExcelTemplate(templateName, file);
        return file;
    }

}
