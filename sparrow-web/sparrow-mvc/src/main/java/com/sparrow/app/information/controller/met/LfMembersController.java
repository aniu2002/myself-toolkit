package com.sparrow.app.information.controller.met;

import java.util.List;
import com.sparrow.http.common.QueryTool;
import com.sparrow.service.annotation.Autowired;
import com.sparrow.server.web.OpResult;
import com.sparrow.server.web.annotation.PathVariable;
import com.sparrow.server.web.annotation.ReqMapping;
import com.sparrow.server.web.annotation.WebController;

import com.sparrow.orm.page.PageResult;


import com.sparrow.app.information.service.met.LfMembersService;
import com.sparrow.app.information.domain.met.LfMembers;

/**
 * 控制器，完成数据库表(lf_members)的增删改查功能<br/>  
 *
 * lf_members:  
 *
 * @author YZC
 * @version 2.0 
 * date: 2017-08-03 01:32:20
 */
@WebController(value = "/met/lf_members")
public class LfMembersController {
    /** 自动注入()服务  */ 
    @Autowired(value = "lfMembersService")
    private LfMembersService lfMembersService; 
    
    public void setLfMembersService(LfMembersService lfMembersService){
       this.lfMembersService=lfMembersService;
    }
    
    public LfMembersService getLfMembersService(){
       return this.lfMembersService;
    }
	
    /**
	 * 
	 * 增加一条()记录 <br/>    
	 *  
	 * @author YZC  
	 * @param lfMembers 
	 *        
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'} 
	 * @throws Exception  
	 *         可能抛出业务操作异常
	 */
	@ReqMapping(method = ReqMapping.POST)
    public OpResult saveLfMembers(LfMembers lfMembers) throws Exception {
       this.lfMembersService.saveLfMembers(lfMembers);
       return OpResult.OK;
    } 
    
     /**
	 * 
	 * 分页查询()记录 <br/>   
	 *  
	 * @author YZC  
	 * @param lfMembers 
	 *        查询条件
	 * @return 返回分页包装信息, 如:<br/>
	 *         {total:1,rows:[{id:'2',name:'haha'}]} 
	 * @throws Exception 
	 *         可能抛出业务操作异常
	 */
    @ReqMapping(method = ReqMapping.GET)
    public PageResult pageListLfMembers(LfMembers lfMembers,int page, int limit ) throws Exception {
       return this.lfMembersService.pageListLfMembers(lfMembers,page,limit);
    } 
    
    /**
	 * 
	 * 查询()记录 <br/>      
	 *  
	 * @author YZC  
	 * @param lfMembers 
	 *        查询条件
	 * @return 返回列表
	 * @throws Exception 
	 *         可能抛出业务操作异常
	 */
    @ReqMapping(value = "/df/",method = ReqMapping.GET)
    public PageResult listLfMembers(LfMembers lfMembers) throws Exception {
       return this.lfMembersService.listLfMembers(lfMembers);
    }
    
    @ReqMapping(value = "/edit",method = ReqMapping.GET)
    public String editPage() throws Exception {
       return "view:met/lf_members_edit";
    } 
    
     @ReqMapping(value = "/delete",method = ReqMapping.DELETE)
    public OpResult batchDelete(String id) throws Exception {
       List<Long> ids = QueryTool.toList(id,Long.class);
       this.lfMembersService.batchDeleteLfMembers(ids);
       return OpResult.OK;
    }
    /**
	 * 
	 * 更新()记录 <br/>      
	 *  
	 * @author YZC  
	 * @param lfMembers 
	 *        
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'} 
	 * @throws Exception 
	 *         可能抛出业务操作异常  
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.PUT)
    public OpResult updateLfMembers(@PathVariable("id") Long lfMembersId,LfMembers lfMembers) throws Exception {
       this.lfMembersService.updateLfMembers(lfMembers);
       return OpResult.OK;
    }
  
    /**
	 * 
	 * 根据lfMembersId获取()记录<br/>      
	 *  
	 * @author YZC  
	 * @param lfMembersId 
	 *        的ID值
	 * @return 返回
	 * @throws Exception 
	 *         可能抛出业务操作异常 
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.GET)
    public LfMembers getLfMembersById(@PathVariable("id") Long lfMembersId) throws Exception {
       return this.lfMembersService.getLfMembersById(lfMembersId);
    }
  
    /**
	 * 
	 * 根据lfMembersId删除()记录<br/>      
	 *  
	 * @author YZC  
	 * @param lfMembersId 
	 *        的ID值
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'}
	 * @throws Exception 
	 *         可能抛出业务操作异常 
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.DELETE)
    public OpResult deleteLfMembersById(@PathVariable("id") Long lfMembersId) throws Exception {
       this.lfMembersService.deleteLfMembersById(lfMembersId);
       return OpResult.OK;
    }
}