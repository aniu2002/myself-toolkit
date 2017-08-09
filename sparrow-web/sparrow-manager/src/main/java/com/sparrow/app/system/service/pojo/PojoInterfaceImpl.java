package com.sparrow.app.system.service.pojo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.sparrow.common.clazz.ClassField;
import com.sparrow.common.clazz.ClassObj;
import com.sparrow.core.resource.clazz.ClassSearch;
import com.sparrow.service.annotation.Service;
import com.sparrow.service.annotation.Transaction;
import com.sparrow.core.utils.StringUtils;


@Service(lazy = true, value = "objectMetaService")
public class PojoInterfaceImpl implements PojoInterface {

    @Override
    public Object getClassInfo(String className) {
        try {
            Class<?> clz = Class.forName(className);
            ClassObj obj = new ClassObj(clz.getName());
            Field[] fields = clz.getDeclaredFields();
            ClassField cfld;
            List<ClassField> list = new ArrayList<ClassField>();
            for (Field fid : fields) {
                cfld = new ClassField();
                cfld.setName(fid.getName());
                cfld.setType(fid.getType().getName());
                list.add(cfld);
            }
            obj.setItems(list);
            return obj;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transaction
    public List<String> getClassNames(String path) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isEmpty(path)) {
            list.add("");
            return list;
        }
        String newPath = "classpath:" + path + ".class";
        Class<?> clazzs[] = ClassSearch.getInstance().searchClass(newPath);
        for (Class<?> claz : clazzs) {
            list.add(claz.getName());
        }
        return list;
    }
}
