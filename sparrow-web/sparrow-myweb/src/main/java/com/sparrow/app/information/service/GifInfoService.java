package com.sparrow.app.information.service;

import com.sparrow.app.information.domain.GifInfo;
import com.sparrow.orm.dao.simple.NormalDao;
import com.sparrow.orm.page.PageResult;

import java.util.List;


/**
 * 
 * 完成数据(gif_info-)的增删改查功能<br/>  
 *
 * gif_info:  
 *
 * @author YZC  
 * @version 2.0
 * date: 2017-07-26 24:59:04
 */
public class GifInfoService{
	private NormalDao baseDao;

	public void setBaseDao(NormalDao baseDao) {
	    this.baseDao = baseDao;
	}

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
	public GifInfo get(Long gifInfoId) {
	    return this.baseDao.getById(GifInfo.class, gifInfoId);
	}

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
	public Integer add(GifInfo gifInfo) {
	    return this.baseDao.save(gifInfo);
	}

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
	public Integer delete(Long gifInfoId) {
	    return this.baseDao.delete(GifInfo.class, gifInfoId);
	}

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
	public Integer update(GifInfo gifInfo) {
	    return this.baseDao.update(gifInfo);
	}

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
	public PageResult pageQuery(GifInfo gifInfo, int page, int limit) {
	    return this.baseDao.pageQuery(gifInfo, page, limit);
	}

    /**
	 * 
	 * 批量删除()信息<br/>      
	 *  
	 * @author YZC  
	 * @param gifInfoIds 
	 *        的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
	public Integer batchDelete(List<Long> gifInfoIds) {
	    return this.baseDao.batchDelete(GifInfo.class, gifInfoIds);
	}

 	/**
	 * 
	 * 批量增加()信息<br/>      
	 *  
	 * @author YZC  
	 * @param gifInfos
	 *        批量值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
	public Integer batchAdd(List<GifInfo> gifInfos) {
	    return this.baseDao.batchAdd(gifInfos);
	}
	
	/**
	 * 
	 * 批量更新()信息<br/>      
	 *  
	 * @author YZC  
	 * @param gifInfos
	 *        批量值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
	public Integer batchUpdate(List<GifInfo> gifInfos) {
	    return this.baseDao.batchUpdate(gifInfos);
	}
}