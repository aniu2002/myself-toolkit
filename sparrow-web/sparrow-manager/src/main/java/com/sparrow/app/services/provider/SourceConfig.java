package com.sparrow.app.services.provider;

import javax.xml.bind.annotation.*;

/**
 * Created by yuanzc on 2015/8/18.
 */
@XmlRootElement(name = "source")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "type", "desc", "props"})
public class SourceConfig {
    @XmlElement
    private String name;
    @XmlElement
    private String type;
    @XmlElement
    private String desc;
    @XmlElement
    private String props;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }
}