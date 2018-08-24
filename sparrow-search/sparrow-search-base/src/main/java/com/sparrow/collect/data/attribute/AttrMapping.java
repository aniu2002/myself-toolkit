package com.sparrow.collect.data.attribute;

import com.alibaba.fastjson.JSON;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 一个属性项:属性值 映射 的其他属性项:属性值
 * Created by yaobo on 2014/6/9.
 */
public class AttrMapping {

    private AttrKey attrKey;

    /**
     * 属性映射
     * attrId:attrValues
     */
    private Map<Integer, AttrValue> relAttributes = new HashMap<>();

    public AttrMapping() {
    }

    public AttrMapping(AttrKey attrKey) {
        this.attrKey = attrKey;
    }

    public AttrMapping(Integer category, Integer attribute, Integer value) {
        attrKey = new AttrKey(category, attribute, value);
    }

    /**
     * 增加一个属性值
     * @param attr
     * @param value
     * @return
     */
    public boolean addRelAttr(Integer attr, Integer value) {
        AttrValue attrValue = relAttributes.get(attr);
        if (attrValue == null) {
            attrValue = new AttrValue();
            relAttributes.put(attr, attrValue);
        }
        return attrValue.addValue(value);
    }

    /**
     * 增加一个属性值
     * @param attr
     * @param value
     * @return
     */
    public boolean addRelAttr(Integer attr, AttrValue value) {
        AttrValue attrValue = relAttributes.get(attr);
        if (attrValue == null) {
            attrValue = new AttrValue();
            relAttributes.put(attr, attrValue);
        }
        return attrValue.addAttrValue(value);
    }

    /**
     * 合并属性映射
     * @param relAttributes
     * @return
     */
    public boolean mergeRelAttr(Map<Integer, AttrValue> relAttributes) {
        boolean set = false;
        for (Map.Entry<Integer, AttrValue> entry : relAttributes.entrySet()) {
            set |= this.addRelAttr(entry.getKey(), entry.getValue());
        }
        return set;
    }

    public AttrKey getAttrKey() {
        return attrKey;
    }

    public Map<Integer, AttrValue> getRelAttributes() {
        return relAttributes;
    }

    public void setAttrKey(AttrKey attrKey) {
        this.attrKey = attrKey;
    }

    public void setRelAttributes(Map<Integer, AttrValue> relAttributes) {
        this.relAttributes = relAttributes;
    }

    public String toKey() {
        return this.attrKey.toString();
    }

    public String toValue() {
        return JSON.toJSONString(relAttributes);
    }

    public String toJson(){
        return JSON.toJSONString(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(attrKey.toString()).append("  ");
        for (Map.Entry<Integer, AttrValue> entry : relAttributes.entrySet()) {
            String str = Arrays.toString(entry.getValue().getValues().toArray());
            sb.append(entry.getKey()).append(":").append(str).append(" ");
        }
        return sb.toString();
    }
}

