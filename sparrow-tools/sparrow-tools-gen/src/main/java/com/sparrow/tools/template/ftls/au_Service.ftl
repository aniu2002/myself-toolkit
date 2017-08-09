package ${servicePackName};

import java.util.List;

<#if (pojoIdType??)>import ${pojoIdType};</#if>

import com.sparrow.core.orm.page.PageResult;

import ${pojoClass};

/**
 * 
 * 完成数据库表(${table}-${tableDesc?if_exists})的增删改查功能<br/>  
 *
 * ${table}: ${tableDesc?if_exists} 
 *
 * @author YZC  
 * @version ${version?if_exists}
 * date: ${dateTime?if_exists}
 */
public interface ${serviceName} {

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
    public void save${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception ;
    
    /**
	 * 
	 * 查询(${tableDesc?if_exists})列表信息 <br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}查询条件
	 * @return 返回${tableDesc?if_exists}列表
	 * @throws Exception 
	 *         可能抛出数据库操作异常  
	 */
    public PageResult list${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception ;
  
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
    public PageResult pageList${pojoClassName}(${pojoClassName} ${regularPojoName},int page, int limit) throws Exception ;
  
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
    public void update${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception ;
  
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
    public ${pojoClassName} get${pojoClassName}ById(${pojoIdSimpleType} ${regularPojoName}Id) throws Exception ;
    
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
    public void delete${pojoClassName}ById(${pojoIdSimpleType} ${regularPojoName}Id) throws Exception ;
    
    public void batchDelete${pojoClassName}(List<${pojoIdSimpleType}> ids) throws Exception;
}