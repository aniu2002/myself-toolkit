package ${daoImplPackage};

import ${pojoIdType};
import ${packageName}.${pojoDaoName};
import ${parentDaoImport};
import ${pojoClass};

/**
 * 完成数据库表（${table}-${tableDesc?if_exists}）的增删改查功能<br/>  
 *
 * ${table}: ${tableDesc?if_exists} 
 * @author YZC
 * @version ${version?if_exists} 
 * date: ${dateTime?if_exists}
 */
public class ${pojoDaoName}Impl extends ${parentDao} implements ${pojoDaoName} {

	/**
	 * 
	 * 插入一条${tableDesc?if_exists}记录 <br/>    
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} ${tableDesc?if_exists}
	 * @return
	 * @throws Exception  可能抛出数据库操作异常 
	 */
    public int insert${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception {
       return this.saveData(${regularPojoName});
    }
  
    /**
	 * 
	 * 查询${tableDesc?if_exists}记录 <br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} ${tableDesc?if_exists}查询条件
	 * @return List<${pojoClassName}> 返回${tableDesc?if_exists}列表
	 * @throws Exception 可能抛出数据库操作异常  
	 */
    public int update${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception {
       return this.updateData(${regularPojoName});
    }
  
    /**
	 * 
	 * 根据${regularPojoName}Id获取${tableDesc?if_exists}记录<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id ${tableDesc?if_exists}的ID值
	 * @return ${pojoClassName} 返回${tableDesc?if_exists} 
	 * @throws Exception 可能抛出数据库操作异常 
	 */
    public ${pojoClassName} select${pojoClassName}ById(${pojoIdSimpleType} ${regularPojoName}Id) throws Exception {
       return this.findObjectByKey(${pojoClassName}.class,${regularPojoName}Id);
    }
  
    /**
	 * 
	 * 根据${regularPojoName}Id删除${tableDesc?if_exists}记录<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id ${tableDesc?if_exists}的ID值
	 * @return
	 * @throws Exception 可能抛出数据库操作异常 
	 */
    public int delete${pojoClassName}ById(${pojoIdSimpleType} ${regularPojoName}Id) throws Exception {
       return this.deleteByKey(${pojoClassName}.class,${regularPojoName}Id);
    }
}