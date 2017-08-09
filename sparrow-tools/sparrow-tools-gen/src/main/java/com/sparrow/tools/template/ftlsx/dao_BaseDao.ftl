package ${daoImplPackage};

import java.util.List;
import com.sparrow.core.service.annotation.Repository;
import com.sparrow.core.orm.page.PageResult;

<#if (pojoIdType??)>import ${pojoIdType};</#if>
import ${packageName}.${pojoDaoName};
import ${parentDaoImport};
import ${pojoClass};

/**
 * 完成数据库表(${table}-${tableDesc?if_exists})的增删改查功能<br/>  
 *
 * ${table}: ${tableDesc?if_exists} 
 * @author YZC
 * @version ${version?if_exists} 
 * date: ${dateTime?if_exists}
 */
@Repository(lazy = true, value = "${simplePojoDaoName}")
public class ${pojoDaoName}Impl extends ${parentDao} implements ${pojoDaoName} {

	/**
	 * 
	 * 插入一条(${tableDesc?if_exists})记录 <br/>    
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}
	 * @return
	 * @throws Exception
     *         可能抛出数据库操作异常 
	 */
    public void insert${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception {
       this.save(${regularPojoName});
    }
  
    /**
	 * 
	 * 查询(${tableDesc?if_exists})记录 <br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}查询条件
	 * @return 返回${tableDesc?if_exists}列表
	 * @throws Exception 
	 *         可能抛出数据库操作异常  
	 */
    public PageResult list${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception {
       return this.findPojoByPage(${regularPojoName},0,200);
    }
    
    /**
	 * 
	 * 分页查询(${tableDesc?if_exists})记录 <br/>   
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}查询条件
	 * @return 返回分页包装信息
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public PageResult pageList${pojoClassName}(${pojoClassName} ${regularPojoName},int page, int limit) throws Exception {
       return this.findPojoByPage(${regularPojoName},page,limit);
    }
   
    /**
	 * 
	 * 更新(${tableDesc?if_exists})记录 <br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}
	 * @return
	 * @throws Exception
     *         可能抛出数据库操作异常  
	 */
    public void update${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception {
       this.update(${regularPojoName});
    }
  
    /**
	 * 
	 * 根据${regularPojoName}Id获取(${tableDesc?if_exists})记录<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id 
	 *        ${tableDesc?if_exists}的ID值
	 * @return 返回对应ID的(${tableDesc?if_exists})实体 
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public ${pojoClassName} get${pojoClassName}ById(${pojoIdSimpleType} ${regularPojoName}Id) throws Exception {
       return this.get(${pojoClassName}.class,${regularPojoName}Id);
    }
  
    /**
	 * 
	 * 根据${regularPojoName}Id删除(${tableDesc?if_exists})记录<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id 
	 *        ${tableDesc?if_exists}的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public void delete${pojoClassName}ById(${pojoIdSimpleType} ${regularPojoName}Id) throws Exception {
       this.remove(${pojoClassName}.class,${regularPojoName}Id);
    }
    
    /**
	 * 
	 * 根据${regularPojoName}Id删除(${tableDesc?if_exists})记录<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id 
	 *        ${tableDesc?if_exists}的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public void batchDelete(List<${pojoIdSimpleType}> ids) throws Exception {
       this.removeBatch(${pojoClassName}.class,ids);
    }
}