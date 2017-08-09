package ${controllerPackage};

import java.util.List;
import com.sparrow.http.common.QueryTool;
import com.sparrow.service.annotation.Autowired;
import com.sparrow.server.web.OpResult;
import com.sparrow.server.web.annotation.PathVariable;
import com.sparrow.server.web.annotation.ReqMapping;
import com.sparrow.server.web.annotation.WebController;

import com.sparrow.orm.page.PageResult;

<#if (pojoIdType??)>import ${pojoIdType};</#if>
import ${servicePackName}.${serviceName};
import ${pojoClass};

/**
 * 控制器，完成数据库表(${table})的增删改查功能<br/>  
 *
 * ${table}: ${tableDesc?if_exists} 
 *
 * @author YZC
 * @version ${version?if_exists} 
 * date: ${dateTime?if_exists}
 */
@WebController(value = "/${subModule}/${pojo4LowerCase}")
public class ${pojoClassName}Controller {
    /** 自动注入(${tableDesc?if_exists})服务  */ 
    @Autowired(value = "${fServiceName}")
    private ${serviceName} ${fServiceName}; 
    
    public void set${serviceName}(${serviceName} ${fServiceName}){
       this.${fServiceName}=${fServiceName};
    }
    
    public ${serviceName} get${serviceName}(){
       return this.${fServiceName};
    }
	
    /**
	 * 
	 * 增加一条(${tableDesc?if_exists})记录 <br/>    
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'} 
	 * @throws Exception  
	 *         可能抛出业务操作异常
	 */
	@ReqMapping(method = ReqMapping.POST)
    public OpResult save${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception {
       this.${fServiceName}.save${pojoClassName}(${regularPojoName});
       return OpResult.OK;
    } 
    
     /**
	 * 
	 * 分页查询(${tableDesc?if_exists})记录 <br/>   
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}查询条件
	 * @return 返回分页包装信息, 如:<br/>
	 *         {total:1,rows:[{id:'2',name:'haha'}]} 
	 * @throws Exception 
	 *         可能抛出业务操作异常
	 */
    @ReqMapping(method = ReqMapping.GET)
    public PageResult pageList${pojoClassName}(${pojoClassName} ${regularPojoName},int page, int limit ) throws Exception {
       return this.${fServiceName}.pageList${pojoClassName}(${regularPojoName},page,limit);
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
	 *         可能抛出业务操作异常
	 */
    @ReqMapping(value = "/df/",method = ReqMapping.GET)
    public PageResult list${pojoClassName}(${pojoClassName} ${regularPojoName}) throws Exception {
       return this.${fServiceName}.list${pojoClassName}(${regularPojoName});
    }
    
    @ReqMapping(value = "/edit",method = ReqMapping.GET)
    public String editPage() throws Exception {
       return "view:${subModule}/${pojo4LowerCase}_edit";
    } 
    
     @ReqMapping(value = "/delete",method = ReqMapping.DELETE)
    public OpResult batchDelete(String id) throws Exception {
       List<${pojoIdSimpleType}> ids = QueryTool.toList(id,${pojoIdSimpleType}.class);
       this.${fServiceName}.batchDelete${pojoClassName}(ids);
       return OpResult.OK;
    }
    /**
	 * 
	 * 更新(${tableDesc?if_exists})记录 <br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName} 
	 *        ${tableDesc?if_exists}
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'} 
	 * @throws Exception 
	 *         可能抛出业务操作异常  
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.PUT)
    public OpResult update${pojoClassName}(@PathVariable("id") ${pojoIdSimpleType} ${regularPojoName}Id,${pojoClassName} ${regularPojoName}) throws Exception {
       this.${fServiceName}.update${pojoClassName}(${regularPojoName});
       return OpResult.OK;
    }
  
    /**
	 * 
	 * 根据${regularPojoName}Id获取(${tableDesc?if_exists})记录<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id 
	 *        ${tableDesc?if_exists}的ID值
	 * @return 返回${tableDesc?if_exists}
	 * @throws Exception 
	 *         可能抛出业务操作异常 
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.GET)
    public ${pojoClassName} get${pojoClassName}ById(@PathVariable("id") ${pojoIdSimpleType} ${regularPojoName}Id) throws Exception {
       return this.${fServiceName}.get${pojoClassName}ById(${regularPojoName}Id);
    }
  
    /**
	 * 
	 * 根据${regularPojoName}Id删除(${tableDesc?if_exists})记录<br/>      
	 *  
	 * @author YZC  
	 * @param ${regularPojoName}Id 
	 *        ${tableDesc?if_exists}的ID值
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'}
	 * @throws Exception 
	 *         可能抛出业务操作异常 
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.DELETE)
    public OpResult delete${pojoClassName}ById(@PathVariable("id") ${pojoIdSimpleType} ${regularPojoName}Id) throws Exception {
       this.${fServiceName}.delete${pojoClassName}ById(${regularPojoName}Id);
       return OpResult.OK;
    }
}