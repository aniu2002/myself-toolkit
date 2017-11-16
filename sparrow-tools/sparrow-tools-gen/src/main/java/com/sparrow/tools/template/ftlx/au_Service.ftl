package ${servicePackName};

import java.util.List;

<#if (pojoIdType??)>import ${pojoIdType};</#if>

import com.dili.dd.logistics.common.page.Pagination;

import ${pojoClass}Dto;

/**
 * 
 * (${table}-${tableDesc?if_exists})基本操作<br/>  
 *
 * @author YZC  
 * @version ${version?if_exists}
 * date: ${dateTime?if_exists}
 */
public interface ${serviceName} {

	/**
	 * 
	 * 保存(${tableDesc?if_exists})信息 <br/>    
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}
	 * @return ${pojoClassName}Dto 为空不成功
	 * @throws Exception  
	 *         可能抛出异常 
	 */
    public ${pojoClassName}Dto save${pojoClassName}(${pojoClassName}Dto ${regularPojoName}) throws Exception ;
    
    /**
	 * 
	 * 批量保存(${tableDesc?if_exists})信息 <br/>    
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}s 
	 *        ${tableDesc?if_exists}
	 * @return
	 * @throws Exception  
	 *         可能抛出异常 
	 */
    public int batchSave${pojoClassName}(List<${pojoClassName}Dto> ${regularPojoName}s) throws Exception;
    
    /**
	 * 
	 * 查询(${tableDesc?if_exists})列表信息 <br/>      
	 *  
	 * @author YZC  
	 * @return 返回${tableDesc?if_exists}列表
	 * @throws Exception 
	 *         可能抛出操作异常  
	 */
    public List<${pojoClassName}Dto> find${pojoClassName}() throws Exception ;
  
  	/**
	 * 
	 * 分页查询(${tableDesc?if_exists})列表信息 <br/>   
	 *  
	 * @author YZC  
	 * @param  ${regularPojoName} 
	 *         ${tableDesc?if_exists}查询条件
	 * @param  pageNo
	 *         查询页码
	 * @param  pageSize 
	 *         查询页记录大小
	 * @return 返回分页包装信息
	 * @throws Exception 
	 *         可能抛出操作异常 
	 */
    public Pagination<${pojoClassName}Dto> page${pojoClassName}(${pojoClassName}Dto ${regularPojoName}, int pageNo, int pageSize) throws Exception ;
  
  	/**
	 * 
	 * 更新(${tableDesc?if_exists})信息 <br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}
	 *        ${tableDesc?if_exists}
	 * @return
	 * @throws Exception 
	 *         可能抛出操作异常  
	 */
    public int update${pojoClassName}(${pojoClassName}Dto ${regularPojoName}) throws Exception ;
  
    /**
	 * 
	 * 批量更新(${tableDesc?if_exists})信息 <br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}s
	 *        ${tableDesc?if_exists}
	 * @return
	 * @throws Exception 
	 *         可能抛出操作异常  
	 */
    public int batchUpdate${pojoClassName}(List<${pojoClassName}Dto> ${regularPojoName}s) throws Exception;
  
  	/**
	 * 
	 * 根据${regularPojoName}Id获取(${tableDesc?if_exists})实例信息<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id 
	 *        ${tableDesc?if_exists}的ID
	 * @return 返回(${tableDesc?if_exists}) 
	 * @throws Exception 
	 *         可能抛出操作异常 
	 */
    public ${pojoClassName}Dto get${pojoClassName}(${pojoIdSimpleType} ${regularPojoName}Id) throws Exception ;
    
    /**
	 * 
	 * 根据${regularPojoName}Id删除(${tableDesc?if_exists})信息<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id 
	 *        ${tableDesc?if_exists}的ID
	 * @return
	 * @throws Exception 
	 *         可能抛出操作异常 
	 */
    public int delete${pojoClassName}(${pojoIdSimpleType} ${regularPojoName}Id) throws Exception ;
    
    /**
	 * 
	 * 批量删除(${tableDesc?if_exists})信息<br/>      
	 *  
	 * @author YZC  
	 * @param ids
	 *        ${tableDesc?if_exists}的ID
	 * @return
	 * @throws Exception 
	 *         可能抛出操作异常 
	 */
    public int batchDelete${pojoClassName}(List<${pojoIdSimpleType}> ids) throws Exception;
}