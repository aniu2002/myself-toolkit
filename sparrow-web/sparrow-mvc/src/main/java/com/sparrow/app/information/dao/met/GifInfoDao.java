package com.sparrow.app.information.dao.met;

import java.util.List;
import com.sparrow.orm.page.PageResult;


import com.sparrow.app.information.domain.met.GifInfo;

/**
 * 完成数据库表(gif_info-)的增删改查功能<br/>  
 *
 * gif_info:  
 * @author YZC
 * @version 2.0 
 * date: 2017-08-03 01:32:20
 */
public interface GifInfoDao{

	/**
	 * 
	 * 插入一条()记录 <br/>    
	 *  
	 * @author YZC  
	 * @param gifInfo 
	 *        
	 * @return
	 * @throws Exception
	 *         可能抛出数据库操作异常 
	 */
    public void insertGifInfo(GifInfo gifInfo) throws Exception ;
    
    /**
	 * 
	 * 查询()记录 <br/>      
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
	 * 分页查询()记录 <br/>   
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
	 * 更新()记录 <br/>      
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
	 * 根据gifInfoId获取()记录<br/>      
	 *  
	 * @author YZC  
	 * @param gifInfoId 
	 *        的ID值
	 * @return 返回对应ID的()实体 
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public GifInfo getGifInfoById(Long gifInfoId) throws Exception;
   
       /**
	 * 
	 * 根据gifInfoId删除()记录<br/>      
	 *  
	 * @author YZC  
	 * @param gifInfoId 
	 *        的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public void deleteGifInfoById(Long gifInfoId) throws Exception;
    /**
	 * 
	 * 根据Ids批量删除()记录<br/>      
	 *  
	 * @author YZC  
	 * @param gifInfoId 
	 *        的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public void batchDelete(List<Long> ids) throws Exception ;
}