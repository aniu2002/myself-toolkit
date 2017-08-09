package com.sparrow.app.information.controller.met;

import java.util.List;
import com.sparrow.http.common.QueryTool;
import com.sparrow.service.annotation.Autowired;
import com.sparrow.server.web.OpResult;
import com.sparrow.server.web.annotation.PathVariable;
import com.sparrow.server.web.annotation.ReqMapping;
import com.sparrow.server.web.annotation.WebController;

import com.sparrow.orm.page.PageResult;


import com.sparrow.app.information.service.met.GifInfoService;
import com.sparrow.app.information.domain.met.GifInfo;

/**
 * 控制器，完成数据库表(gif_info)的增删改查功能<br/>  
 *
 * gif_info:  
 *
 * @author YZC
 * @version 2.0 
 * date: 2017-08-03 01:32:20
 */
@WebController(value = "/met/gif_info")
public class GifInfoController {
    /** 自动注入()服务  */ 
    @Autowired(value = "gifInfoService")
    private GifInfoService gifInfoService; 
    
    public void setGifInfoService(GifInfoService gifInfoService){
       this.gifInfoService=gifInfoService;
    }
    
    public GifInfoService getGifInfoService(){
       return this.gifInfoService;
    }
	
    /**
	 * 
	 * 增加一条()记录 <br/>    
	 *  
	 * @author YZC  
	 * @param gifInfo 
	 *        
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'} 
	 * @throws Exception  
	 *         可能抛出业务操作异常
	 */
	@ReqMapping(method = ReqMapping.POST)
    public OpResult saveGifInfo(GifInfo gifInfo) throws Exception {
       this.gifInfoService.saveGifInfo(gifInfo);
       return OpResult.OK;
    } 
    
     /**
	 * 
	 * 分页查询()记录 <br/>   
	 *  
	 * @author YZC  
	 * @param gifInfo 
	 *        查询条件
	 * @return 返回分页包装信息, 如:<br/>
	 *         {total:1,rows:[{id:'2',name:'haha'}]} 
	 * @throws Exception 
	 *         可能抛出业务操作异常
	 */
    @ReqMapping(method = ReqMapping.GET)
    public PageResult pageListGifInfo(GifInfo gifInfo,int page, int limit ) throws Exception {
       return this.gifInfoService.pageListGifInfo(gifInfo,page,limit);
    } 
    
    /**
	 * 
	 * 查询()记录 <br/>      
	 *  
	 * @author YZC  
	 * @param gifInfo 
	 *        查询条件
	 * @return 返回列表
	 * @throws Exception 
	 *         可能抛出业务操作异常
	 */
    @ReqMapping(value = "/df/",method = ReqMapping.GET)
    public PageResult listGifInfo(GifInfo gifInfo) throws Exception {
       return this.gifInfoService.listGifInfo(gifInfo);
    }
    
    @ReqMapping(value = "/edit",method = ReqMapping.GET)
    public String editPage() throws Exception {
       return "view:met/gif_info_edit";
    } 
    
     @ReqMapping(value = "/delete",method = ReqMapping.DELETE)
    public OpResult batchDelete(String id) throws Exception {
       List<Long> ids = QueryTool.toList(id,Long.class);
       this.gifInfoService.batchDeleteGifInfo(ids);
       return OpResult.OK;
    }
    /**
	 * 
	 * 更新()记录 <br/>      
	 *  
	 * @author YZC  
	 * @param gifInfo 
	 *        
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'} 
	 * @throws Exception 
	 *         可能抛出业务操作异常  
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.PUT)
    public OpResult updateGifInfo(@PathVariable("id") Long gifInfoId,GifInfo gifInfo) throws Exception {
       this.gifInfoService.updateGifInfo(gifInfo);
       return OpResult.OK;
    }
  
    /**
	 * 
	 * 根据gifInfoId获取()记录<br/>      
	 *  
	 * @author YZC  
	 * @param gifInfoId 
	 *        的ID值
	 * @return 返回
	 * @throws Exception 
	 *         可能抛出业务操作异常 
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.GET)
    public GifInfo getGifInfoById(@PathVariable("id") Long gifInfoId) throws Exception {
       return this.gifInfoService.getGifInfoById(gifInfoId);
    }
  
    /**
	 * 
	 * 根据gifInfoId删除()记录<br/>      
	 *  
	 * @author YZC  
	 * @param gifInfoId 
	 *        的ID值
	 * @return 返回操作信息, 如:<br/>
	 *         {flag:1,message:'成功'}
	 * @throws Exception 
	 *         可能抛出业务操作异常 
	 */
    @ReqMapping(value = "/{id}", method = ReqMapping.DELETE)
    public OpResult deleteGifInfoById(@PathVariable("id") Long gifInfoId) throws Exception {
       this.gifInfoService.deleteGifInfoById(gifInfoId);
       return OpResult.OK;
    }
}