package com.sparrow.app.information.controller.school;

import java.util.List;
import com.sparrow.http.common.QueryTool;
import com.sparrow.service.annotation.Autowired;
import com.sparrow.server.web.OpResult;
import com.sparrow.server.web.annotation.PathVariable;
import com.sparrow.server.web.annotation.ReqMapping;
import com.sparrow.server.web.annotation.WebController;

import com.sparrow.orm.page.PageResult;


import com.sparrow.app.information.service.school.PrimarySchoolService;
import com.sparrow.app.information.domain.school.PrimarySchool;

/**
 * 控制器，完成数据库表(primary_school)的增删改查功能<br/>  
 *
 * primary_school:  
 *
 * @author YZC
 * @version 2.0 
 * date: 2017-08-03 01:32:20
 */
@WebController(value = "/school/primary_school")
public class PrimarySchoolController {
    /** 自动注入()服务  */ 
    @Autowired(value = "primarySchoolService")
    private PrimarySchoolService primarySchoolService; 
    
    public void setPrimarySchoolService(PrimarySchoolService primarySchoolService){
       this.primarySchoolService=primarySchoolService;
    }
    
    public PrimarySchoolService getPrimarySchoolService(){
       return this.primarySchoolService;
    }
	
    /**
	 * 
	 * 增加一条()记录 <br/>    
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'} 
	 * @throws Exception  
	 *         可能抛出业务操作异常
	 */
	@ReqMapping(method = ReqMapping.POST)
    public OpResult savePrimarySchool(PrimarySchool primarySchool) throws Exception {
       this.primarySchoolService.savePrimarySchool(primarySchool);
       return OpResult.OK;
    } 
    
     /**
	 * 
	 * 分页查询()记录 <br/>   
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        查询条件
	 * @return 返回分页包装信息, 如:<br/>
	 *         {total:1,rows:[{id:'2',name:'haha'}]} 
	 * @throws Exception 
	 *         可能抛出业务操作异常
	 */
    @ReqMapping(method = ReqMapping.GET)
    public PageResult pageListPrimarySchool(PrimarySchool primarySchool,int page, int limit ) throws Exception {
       return this.primarySchoolService.pageListPrimarySchool(primarySchool,page,limit);
    } 
    
    /**
	 * 
	 * 查询()记录 <br/>      
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        查询条件
	 * @return 返回列表
	 * @throws Exception 
	 *         可能抛出业务操作异常
	 */
    @ReqMapping(value = "/df/",method = ReqMapping.GET)
    public PageResult listPrimarySchool(PrimarySchool primarySchool) throws Exception {
       return this.primarySchoolService.listPrimarySchool(primarySchool);
    }
    
    @ReqMapping(value = "/edit",method = ReqMapping.GET)
    public String editPage() throws Exception {
       return "view:school/primary_school_edit";
    } 
    
     @ReqMapping(value = "/delete",method = ReqMapping.DELETE)
    public OpResult batchDelete(String id) throws Exception {
       List<Long> ids = QueryTool.toList(id,Long.class);
       this.primarySchoolService.batchDeletePrimarySchool(ids);
       return OpResult.OK;
    }
    /**
	 * 
	 * 更新()记录 <br/>      
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'} 
	 * @throws Exception 
	 *         可能抛出业务操作异常  
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.PUT)
    public OpResult updatePrimarySchool(@PathVariable("id") Long primarySchoolId,PrimarySchool primarySchool) throws Exception {
       this.primarySchoolService.updatePrimarySchool(primarySchool);
       return OpResult.OK;
    }
  
    /**
	 * 
	 * 根据primarySchoolId获取()记录<br/>      
	 *  
	 * @author YZC  
	 * @param primarySchoolId 
	 *        的ID值
	 * @return 返回
	 * @throws Exception 
	 *         可能抛出业务操作异常 
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.GET)
    public PrimarySchool getPrimarySchoolById(@PathVariable("id") Long primarySchoolId) throws Exception {
       return this.primarySchoolService.getPrimarySchoolById(primarySchoolId);
    }
  
    /**
	 * 
	 * 根据primarySchoolId删除()记录<br/>      
	 *  
	 * @author YZC  
	 * @param primarySchoolId 
	 *        的ID值
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'}
	 * @throws Exception 
	 *         可能抛出业务操作异常 
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.DELETE)
    public OpResult deletePrimarySchoolById(@PathVariable("id") Long primarySchoolId) throws Exception {
       this.primarySchoolService.deletePrimarySchoolById(primarySchoolId);
       return OpResult.OK;
    }
}