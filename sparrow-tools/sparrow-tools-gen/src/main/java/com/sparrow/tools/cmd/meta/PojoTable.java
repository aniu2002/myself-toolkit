package com.sparrow.tools.cmd.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class PojoTable {
    private String primaryKeys;
    private String name;
    private String objName;
    private String desc;
    private String pakage;
    private String mapperPakage;
    private String sequence;
    private String keyGenerator;
    private String keyJavaType;
    private String keySimpleJavaType;
    private List<PojoTableColumn> items;
    private Collection<String> imports;

    public PojoTable() {

    }

    public String getKeyJavaType() {
        return keyJavaType;
    }

    public void setKeyJavaType(String keyJavaType) {
        this.keyJavaType = keyJavaType;
    }

    public String getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(String keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public String getKeySimpleJavaType() {
        return keySimpleJavaType;
    }

    public void setKeySimpleJavaType(String keySimpleJavaType) {
        this.keySimpleJavaType = keySimpleJavaType;
    }

    public PojoTable(String name) {
        this.name = name;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getMapperPakage() {
        return mapperPakage;
    }

    public void setMapperPakage(String mapperPakage) {
        this.mapperPakage = mapperPakage;
    }

    public String getPakage() {
        return pakage;
    }

    public void setPakage(String pakage) {
        this.pakage = pakage;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public String getPrimaryKeys() {
        return primaryKeys;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPrimaryKeys(String primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public String getName() {
        return name;
    }

    public void setNamex(String name) {
        this.name = name;
    }

    public List<PojoTableColumn> getItems() {
        return items;
    }

    public void setItems(List<PojoTableColumn> items) {
        this.items = items;
    }

    public void addItem(PojoTableColumn item) {
        if (this.items == null)
            this.items = new ArrayList<PojoTableColumn>();
        this.items.add(item);

    }

    public void caculate() {
        if (this.items != null) {
            Map<String, String> cache = new HashMap<String, String>();
            for (PojoTableColumn tc : this.items) {
                String type = tc.getJavaType();
                if (type.startsWith("java.lang"))
                    continue;
                cache.put(type, type);
                // list.add(type);
            }
            this.imports = cache.values();
        }
    }

    public Collection<String> getImports() {
        return imports;
    }

    public void setImports(Collection<String> imports) {
        this.imports = imports;
    }

    public void removeItem(PojoTableColumn item) {
        if (this.items != null)
            this.items.remove(item);
    }

    public String getSelectSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ").append(this.name).append(" WHERE ")
                .append(this.primaryKeys).append("=?");
        return sb.toString();
    }

    public String getQuerySql() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ").append(this.name);
        return sb.toString();
    }

    public String getCountSql() {
        return "SELECT COUNT(1) FROM " + this.name;
    }

    public String getInsertSql() {
        if (this.items != null && !this.items.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(this.name).append("(");
            boolean isf = true;
            for (PojoTableColumn tc : this.items) {
                if (StringUtils.equals(this.primaryKeys, tc.getFieldName())
                        && StringUtils.equals(tc.getSampleType(), "int"))
                    continue;
                if (!isf) {
                    sb.append(",");
                } else
                    isf = false;
                sb.append(tc.getName());
            }
            sb.append(") VALUES(");
            isf = true;
            for (PojoTableColumn tc : this.items) {
                if (StringUtils.equals(this.primaryKeys, tc.getFieldName())
                        && StringUtils.equals(tc.getSampleType(), "int"))
                    continue;
                if (!isf) {
                    sb.append(",");
                } else
                    isf = false;
                sb.append(":").append(tc.getFieldName());
            }
            sb.append(")");
            return sb.toString();
        }
        return null;
    }

    public String getUpdateSql() {
        if (this.items != null && !this.items.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ").append(this.name).append(" SET ");
            boolean isf = true;
            for (PojoTableColumn tc : this.items) {
                if (StringUtils.equalsIgnoreCase(this.primaryKeys, tc.getFieldName()))
                    continue;
                if (!isf) {
                    sb.append(",");
                } else
                    isf = false;
                sb.append(tc.getName()).append("=:").append(tc.getFieldName());
            }
            sb.append(" WHERE ").append(primaryKeys).append("=:")
                    .append(this.primaryKeys);
            return sb.toString();
        }
        return null;
    }

    public String getDeleteSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(this.name).append(" WHERE ")
                .append(this.primaryKeys).append("=?");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\r\n  table  : \"").append(this.name).append("\",\r\n")
                .append("  object : \"").append(this.objName).append("\",\r\n")
                .append("  desc   : \"").append(this.desc).append("\",\r\n")
                .append("  primaryKeys : \"").append(this.primaryKeys)
                .append("\",\r\n").append("  items:[ \r\n");
        if (this.items != null) {
            boolean isf = true;
            for (PojoTableColumn tc : this.items) {
                if (!isf) {
                    sb.append(",\r\n");
                } else
                    isf = false;
                sb.append("    ").append(tc);
            }
        }
        sb.append("\r\n  ]\r\n}");
        return sb.toString();
    }

}
