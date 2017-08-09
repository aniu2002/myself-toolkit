package com.sparrow.app.information.service.met.impl;

import java.util.List;
import com.sparrow.service.annotation.Autowired;
import com.sparrow.service.annotation.Service;
import com.sparrow.service.annotation.Transaction;

import com.sparrow.orm.page.PageResult;


import com.sparrow.app.information.service.met.LfMembersService;
import com.sparrow.app.information.dao.met.LfMembersDao;
import com.sparrow.app.information.domain.met.LfMembers;

/**
 * 完成数据库表(lf_members-)的增删改查功能<br/>  
 *
 * lf_members:  
 * @author YZC
 * @version 2.0 
 * date: 2017-08-03 01:32:20
 */
@Service(lazy = true, value = "lfMembersService")
public class LfMembersServiceImpl implements LfMembersService {
    /** 自动注入 操作DAO */
    @Autowired
    private LfMembersDao lfMembersDao; 
    
    public void setLfMembersDao(LfMembersDao lfMembersDao){
       this.lfMembersDao=lfMembersDao;
    }
    
    public LfMembersDao getLfMembersDao(){
       return this.lfMembersDao;
    }
    
	/**
	 * 
	 * 增加保存()信息 <br/>    
	 *  
	 * @author YZC  
	 * @param lfMembers 
	 *        
	 * @return
	 * @throws Exception
     *         可能抛出数据库操作异常 
	 */
	@Transaction
    public void saveLfMembers(LfMembers lfMembers) throws Exception {
       this.lfMembersDao.insertLfMembers(lfMembers);
    }
    
    /**
	 * 
	 * 查询()列表信息 <br/>      
	 *  
	 * @author YZC  
	 * @param lfMembers 
	 *        查询条件
	 * @return 返回列表
	 * @throws Exception 
	 *         可能抛出数据库操作异常  
	 */
    public PageResult listLfMembers(LfMembers lfMembers) throws Exception {
       return this.lfMembersDao.listLfMembers(lfMembers);
    }
    
    /**
	 * 
	 * 分页查询()列表信息 <br/>   
	 *  
	 * @author YZC  
	 * @param lfMembers 
	 *        查询条件
	 * @return 返回分页包装信息
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public PageResult pageListLfMembers(LfMembers lfMembers,int page, int limit) throws Exception {
       return this.lfMembersDao.pageListLfMembers(lfMembers,page,limit);
    }
    
    /**
	 * 
	 * 更新()信息 <br/>      
	 *  
	 * @author YZC  
	 * @param lfMembers 
	 *        
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常  
	 */
    @Transaction
    public void updateLfMembers(LfMembers lfMembers) throws Exception {
       this.lfMembersDao.updateLfMembers(lfMembers);
    }
  
    /**
	 * 
	 * 根据lfMembersId获取()实例信息<br/>      
	 *  
	 * @author YZC  
	 * @param lfMembersId 
	 *        的ID值
	 * @return 返回对应ID的()实体
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public LfMembers getLfMembersById(Long lfMembersId) throws Exception {
       return this.lfMembersDao.getLfMembersById(lfMembersId);
    }
  
    /**
	 * 
	 * 根据lfMembersId删除()信息<br/>      
	 *  
	 * @author YZC  
	 * @param lfMembersId 
	 *        的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    @Transaction
    public void deleteLfMembersById(Long lfMembersId) throws Exception {
       this.lfMembersDao.deleteLfMembersById(lfMembersId);
    }
    
    /**
	 * 
	 * 根据lfMembersId删除()信息<br/>      
	 *  
	 * @author YZC  
	 * @param lfMembersId 
	 *        的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    @Transaction
    public void batchDeleteLfMembers(List<Long> ids) throws Exception {
       this.lfMembersDao.batchDelete(ids);
    }
}