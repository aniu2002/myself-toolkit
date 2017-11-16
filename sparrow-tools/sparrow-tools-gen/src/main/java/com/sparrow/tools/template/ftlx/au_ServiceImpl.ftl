package ${serviceImplPackage};

<#if (pojoIdType??)>import ${pojoIdType};</#if>

import ${servicePackName}.${serviceName};
import com.dili.dd.logistics.common.page.Pagination;
import ${pojoClass}Dto;
import com.dili.dd.logistics.entity.${subPack}.${pojoClassName};
import com.dili.dd.logistics.entity.${subPack}.${pojoClassName}Example;
import com.dili.dd.logistics.repository.dao.BaseDao;
import com.dili.dd.logistics.repository.mapper.${subPack}.${pojoClassName}Mapper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanzc on 2015/2/17.
 */
@com.alibaba.dubbo.config.annotation.Service
@org.springframework.stereotype.Service("${regularPojoName}Service")
public class ${serviceName}Impl implements ${serviceName} {

    @Autowired
    private BaseDao baseDao;
    @Resource
    private ${pojoClassName}Mapper ${regularPojoName}Mapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ${pojoClassName}Dto save${pojoClassName}(${pojoClassName}Dto ${regularPojoName}) throws Exception {
        if (${regularPojoName} == null)
            return null;
        ${pojoClassName} po = this.dto2pojo(${regularPojoName});
        int n = 0;
        if (${regularPojoName}.getId() == null) {
            n = this.${regularPojoName}Mapper.insert(po);
            ${regularPojoName}.setId(po.getId());
        } else {
            n = this.${regularPojoName}Mapper.updateByPrimaryKey(po);
        }
        if (n > 0)
            return ${regularPojoName};
        else
            return null;
    }

    ${pojoClassName} dto2pojo(${pojoClassName}Dto ${regularPojoName}) throws InvocationTargetException, IllegalAccessException {
        ${pojoClassName} pojo = new ${pojoClassName}();
        BeanUtils.copyProperties(pojo, ${regularPojoName});
        return pojo;
    }

    ${pojoClassName}Dto pojo2dto(${pojoClassName} ${regularPojoName}) throws InvocationTargetException, IllegalAccessException {
        ${pojoClassName}Dto dto = new ${pojoClassName}Dto();
        BeanUtils.copyProperties(dto, ${regularPojoName});
        return dto;
    }

    List<${pojoClassName}Dto> pojo2dtoList(List<${pojoClassName}> ${regularPojoName}s) throws InvocationTargetException, IllegalAccessException {
        List<${pojoClassName}Dto> list = new ArrayList<${pojoClassName}Dto>();
        for (${pojoClassName} ${regularPojoName} : ${regularPojoName}s)
            list.add(this.pojo2dto(${regularPojoName}));
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int batchSave${pojoClassName}(List<${pojoClassName}Dto> ${regularPojoName}s) throws Exception {
        int n = 0;
        for (${pojoClassName}Dto dto : ${regularPojoName}s) {
            n += this.${regularPojoName}Mapper.insert(this.dto2pojo(dto));
        }
        return n;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<${pojoClassName}Dto> find${pojoClassName}() throws Exception {
        return this.pojo2dtoList(this.${regularPojoName}Mapper.selectByExample(null));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Pagination<${pojoClassName}Dto> page${pojoClassName}(${pojoClassName}Dto ${regularPojoName}, int pageNo, int pageSize) throws Exception {
        ${pojoClassName}Example example = new ${pojoClassName}Example();
        Pagination page = this.baseDao.pagedQuery(this.${regularPojoName}Mapper, example, pageNo, pageSize);
        List<${pojoClassName}> ${regularPojoName}s = page.getData();
        page.setData(this.pojo2dtoList(${regularPojoName}s));
        return page;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int update${pojoClassName}(${pojoClassName}Dto ${regularPojoName}) throws Exception {
        ${pojoClassName} record = this.dto2pojo(${regularPojoName});
        return this.${regularPojoName}Mapper.updateByPrimaryKey(record);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int batchUpdate${pojoClassName}(List<${pojoClassName}Dto> ${regularPojoName}s) throws Exception {
        int n = 0;
        for (${pojoClassName}Dto dto : ${regularPojoName}s) {
            n += this.${regularPojoName}Mapper.updateByPrimaryKey(this.dto2pojo(dto));
        }
        return n;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ${pojoClassName}Dto get${pojoClassName}(Long ${regularPojoName}Id) throws Exception {
        return this.pojo2dto(this.${regularPojoName}Mapper.selectByPrimaryKey(${regularPojoName}Id));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int delete${pojoClassName}(Long ${regularPojoName}Id) throws Exception {
        return this.${regularPojoName}Mapper.deleteByPrimaryKey(${regularPojoName}Id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int batchDelete${pojoClassName}(List<Long> ids) throws Exception {
        int n = 0;
        for (Long id : ids) {
            n += this.${regularPojoName}Mapper.deleteByPrimaryKey(id);
        }
        return n;
    }
}
