package com.sparrow.service.context;

import com.sparrow.service.bean.BeanInitialize;
import com.sparrow.service.bean.ContextAware;

public class AnnotationBean implements BeanInitialize, ContextAware {
    private ServiceContext context;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void initialize() {
        //new ControllerAnnotationHelper().scanBean(this.context.getConfiguration(), this.path);
    }

    @Override
    public void setContext(ServiceContext context) {
        this.context = context;
    }

}
