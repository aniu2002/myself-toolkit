package com.sparrow.app.information.service.met;

import java.util.List;



import com.sparrow.orm.page.PageResult;

import com.sparrow.app.information.domain.met.GifInfo;

/**
 * 
 * 完成数据库表(gif_info-)的增删改查功能<br/>  
 *
 * gif_info:  
 *
 * @author YZC  
 * @version 2.0
 * date: 2017-08-03 01:32:20
 */
public interface GifInfoService {

	/**
	 * 
	 * 增加保存()信息 <br/>    
	 *  
	 * @author YZC  
	 * @param gifInfo 
	 *        
	 * @return
	 * @throws Exception  
	 *         可能抛出数据库操作异常 
	 */
    public void saveGifInfo(GifInfo gifInfo) throws Exception ;
    
    /**
	 * 
	 * 查询()列表信息 <br/>      
	 *  
	 * @author YZC  
	 * @param gifInfo 
	 *        查询条件
	 * @return 返回列表
	 * @throws Exception 
	 *         可能抛出数据库操作异常  
	 */
    public PageResult listGifInfo(GifInfo gifInfo) throws Exception ;
  
  	/**
	 * 
	 * 分页查询()列表信息 <br/>   
	 *  
	 * @author YZC  
	 * @param gifInfo 
	 *        查询条件
	 * @return 返回分页包装信息
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public PageResult pageListGifInfo(GifInfo gifInfo,int page, int limit) throws Exception ;
  
  	/**
	 * 
	 * 更新()信息 <br/>      
	 *  
	 * @author YZC  
	 * @param gifInfo
	 *        
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常  
	 */
    public void updateGifInfo(GifInfo gifInfo) throws Exception ;
  
  	/**
	 * 
	 * 根据gifInfoId获取()实例信息<br/>      
	 *  
	 * @author YZC  
	 * @param gifInfoId 
	 *        的ID值
	 * @return 返回对应ID的()实体 
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public GifInfo getGifInfoById(Long gifInfoId) throws Exception ;
    
    /**
	 * 
	 * 根据gifInfoId删除()信息<br/>      
	 *  
	 * @author YZC  
	 * @param gifInfoId 
	 *        的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public void deleteGifInfoById(Long gifInfoId) throws Exception ;
    
    public void batchDeleteGifInfo(List<Long> ids) throws Exception;
}