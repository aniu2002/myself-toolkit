package com.sparrow.app.services.provider;

import javax.xml.bind.annotation.*;

/**
 * Created by yuanzc on 2015/8/24.
 */
@XmlRootElement(name = "Item")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "source", "type", "desc", "script" })
public class ProviderItem {
    @XmlElement
    private String name;

    @XmlElement
    private String source;

    @XmlElement
    private String type;

    @XmlElement
    private String desc;

    @XmlElement
    private String script;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
