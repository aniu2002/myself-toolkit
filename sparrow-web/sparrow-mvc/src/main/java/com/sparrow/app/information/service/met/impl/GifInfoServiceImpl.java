package com.sparrow.app.information.service.met.impl;

import java.util.List;
import com.sparrow.service.annotation.Autowired;
import com.sparrow.service.annotation.Service;
import com.sparrow.service.annotation.Transaction;

import com.sparrow.orm.page.PageResult;


import com.sparrow.app.information.service.met.GifInfoService;
import com.sparrow.app.information.dao.met.GifInfoDao;
import com.sparrow.app.information.domain.met.GifInfo;

/**
 * 完成数据库表(gif_info-)的增删改查功能<br/>  
 *
 * gif_info:  
 * @author YZC
 * @version 2.0 
 * date: 2017-08-03 01:32:20
 */
@Service(lazy = true, value = "gifInfoService")
public class GifInfoServiceImpl implements GifInfoService {
    /** 自动注入 操作DAO */
    @Autowired
    private GifInfoDao gifInfoDao; 
    
    public void setGifInfoDao(GifInfoDao gifInfoDao){
       this.gifInfoDao=gifInfoDao;
    }
    
    public GifInfoDao getGifInfoDao(){
       return this.gifInfoDao;
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
	@Transaction
    public void saveGifInfo(GifInfo gifInfo) throws Exception {
       this.gifInfoDao.insertGifInfo(gifInfo);
    }
    
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
    public PageResult listGifInfo(GifInfo gifInfo) throws Exception {
       return this.gifInfoDao.listGifInfo(gifInfo);
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
    public PageResult pageListGifInfo(GifInfo gifInfo,int page, int limit) throws Exception {
       return this.gifInfoDao.pageListGifInfo(gifInfo,page,limit);
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
    @Transaction
    public void updateGifInfo(GifInfo gifInfo) throws Exception {
       this.gifInfoDao.updateGifInfo(gifInfo);
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
    public GifInfo getGifInfoById(Long gifInfoId) throws Exception {
       return this.gifInfoDao.getGifInfoById(gifInfoId);
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
    @Transaction
    public void deleteGifInfoById(Long gifInfoId) throws Exception {
       this.gifInfoDao.deleteGifInfoById(gifInfoId);
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
    @Transaction
    public void batchDeleteGifInfo(List<Long> ids) throws Exception {
       this.gifInfoDao.batchDelete(ids);
    }
}