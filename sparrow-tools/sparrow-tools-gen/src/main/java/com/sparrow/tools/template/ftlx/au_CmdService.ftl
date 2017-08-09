package ${servicePackName};

import java.util.List;
<#if (pojoIdType??)>import ${pojoIdType};</#if>

import com.sparrow.orm.dao.simple.NormalDao;
import com.sparrow.orm.page.PageResult;
import ${basePackage}.domain.${pojoClassName};

/**
 * 
 * 完成数据(${table}-${tableDesc?if_exists})的增删改查功能<br/>  
 *
 * ${table}: ${tableDesc?if_exists} 
 *
 * @author YZC  
 * @version ${version?if_exists}
 * date: ${dateTime?if_exists}
 */
public class ${pojoClassName}Service{
	private NormalDao baseDao;

	public void setBaseDao(NormalDao baseDao) {
	    this.baseDao = baseDao;
	}

	/**
	 * 
	 * 根据${regularPojoName}Id获取(${tableDesc?if_exists})实例信息<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id 
	 *        ${tableDesc?if_exists}的ID值
	 * @return 返回对应ID的(${tableDesc?if_exists})实体 
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
	public ${pojoClassName} get(${pojoIdSimpleType} ${regularPojoName}Id) {
	    return this.baseDao.getById(${pojoClassName}.class, ${regularPojoName}Id);
	}

    /**
	 * 
	 * 增加保存(${tableDesc?if_exists})信息 <br/>    
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}
	 * @return
	 * @throws Exception  
	 *         可能抛出数据库操作异常 
	 */
	public Integer add(${pojoClassName} ${regularPojoName}) {
	    return this.baseDao.save(${regularPojoName});
	}

 	/**
	 * 
	 * 根据${regularPojoName}Id删除(${tableDesc?if_exists})信息<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id 
	 *        ${tableDesc?if_exists}的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
	public Integer delete(${pojoIdSimpleType} ${regularPojoName}Id) {
	    return this.baseDao.delete(${pojoClassName}.class, ${regularPojoName}Id);
	}

	/**
	 * 
	 * 更新(${tableDesc?if_exists})信息 <br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}
	 *        ${tableDesc?if_exists}
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常  
	 */
	public Integer update(${pojoClassName} ${regularPojoName}) {
	    return this.baseDao.update(${regularPojoName});
	}

	/**
	 * 
	 * 分页查询(${tableDesc?if_exists})列表信息 <br/>   
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}查询条件
	 * @return 返回分页包装信息
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
	public PageResult pageQuery(${pojoClassName} ${regularPojoName}, int page,int limit) {
	    return this.baseDao.pageQuery(${regularPojoName}, page, limit);
	}

    /**
	 * 
	 * 批量删除(${tableDesc?if_exists})信息<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Ids 
	 *        ${tableDesc?if_exists}的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
	public Integer batchDelete(List<${pojoIdSimpleType}> ${regularPojoName}Ids) {
	    return this.baseDao.batchDelete(${pojoClassName}.class, ${regularPojoName}Ids);
	}

 	/**
	 * 
	 * 批量增加(${tableDesc?if_exists})信息<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}s
	 *        ${tableDesc?if_exists}批量值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
	public Integer batchAdd(List<${pojoClassName}> ${regularPojoName}s) {
	    return this.baseDao.batchAdd(${regularPojoName}s);
	}
	
	/**
	 * 
	 * 批量更新(${tableDesc?if_exists})信息<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}s
	 *        ${tableDesc?if_exists}批量值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
	public Integer batchUpdate(List<${pojoClassName}> ${regularPojoName}s) {
	    return this.baseDao.batchUpdate(${regularPojoName}s);
	}
}