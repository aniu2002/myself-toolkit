package ${serviceImplPackage};

import java.util.List;
import com.sparrow.core.service.annotation.Autowired;
import com.sparrow.core.service.annotation.Service;
import com.sparrow.core.service.annotation.Transaction;

import com.sparrow.core.orm.page.PageResult;

<#if (pojoIdType??)>import ${pojoIdType};</#if>
import ${servicePackName}.${serviceName};
import ${pojoDaoImport};
import ${pojoClass};

/**
 * 完成数据库表(${table}-${tableDesc?if_exists})的增删改查功能<br/>  
 *
 * ${table}: ${tableDesc?if_exists} 
 * @author YZC
 * @version ${version?if_exists} 
 * date: ${dateTime?if_exists}
 */
@Service(lazy = true, value = "${fServiceName}")
public class ${serviceName}Impl implements ${serviceName} {
    /** 自动注入 ${tableDesc?if_exists}操作DAO */
    @Autowired
    private ${pojoDaoName} ${simplePojoDaoName}; 
    
    public void set${pojoDaoName}(${pojoDaoName} ${simplePojoDaoName}){
       this.${simplePojoDaoName}=${simplePojoDaoName};
    }
    
    public ${pojoDaoName} get${pojoDaoName}(){
       return this.${simplePojoDaoName};
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
	@Transaction
    public void save${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception {
       this.${simplePojoDaoName}.insert${pojoClassName}(${regularPojoName});
    }
    
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
    public PageResult list${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception {
       return this.${simplePojoDaoName}.list${pojoClassName}(${regularPojoName});
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
    public PageResult pageList${pojoClassName}(${pojoClassName} ${regularPojoName},int page, int limit) throws Exception {
       return this.${simplePojoDaoName}.pageList${pojoClassName}(${regularPojoName},page,limit);
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
    @Transaction
    public void update${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception {
       this.${simplePojoDaoName}.update${pojoClassName}(${regularPojoName});
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
    public ${pojoClassName} get${pojoClassName}ById(${pojoIdSimpleType} ${regularPojoName}Id) throws Exception {
       return this.${simplePojoDaoName}.get${pojoClassName}ById(${regularPojoName}Id);
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
    @Transaction
    public void delete${pojoClassName}ById(${pojoIdSimpleType} ${regularPojoName}Id) throws Exception {
       this.${simplePojoDaoName}.delete${pojoClassName}ById(${regularPojoName}Id);
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
    @Transaction
    public void batchDelete${pojoClassName}(List<${pojoIdSimpleType}> ids) throws Exception {
       this.${simplePojoDaoName}.batchDelete(ids);
    }
}