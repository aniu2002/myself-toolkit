package com.sparrow.app.information.service.school.impl;

import java.util.List;
import com.sparrow.service.annotation.Autowired;
import com.sparrow.service.annotation.Service;
import com.sparrow.service.annotation.Transaction;

import com.sparrow.orm.page.PageResult;


import com.sparrow.app.information.service.school.PrimarySchoolService;
import com.sparrow.app.information.dao.school.PrimarySchoolDao;
import com.sparrow.app.information.domain.school.PrimarySchool;

/**
 * 完成数据库表(primary_school-)的增删改查功能<br/>  
 *
 * primary_school:  
 * @author YZC
 * @version 2.0 
 * date: 2017-08-03 01:32:20
 */
@Service(lazy = true, value = "primarySchoolService")
public class PrimarySchoolServiceImpl implements PrimarySchoolService {
    /** 自动注入 操作DAO */
    @Autowired
    private PrimarySchoolDao primarySchoolDao; 
    
    public void setPrimarySchoolDao(PrimarySchoolDao primarySchoolDao){
       this.primarySchoolDao=primarySchoolDao;
    }
    
    public PrimarySchoolDao getPrimarySchoolDao(){
       return this.primarySchoolDao;
    }
    
	/**
	 * 
	 * 增加保存()信息 <br/>    
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        
	 * @return
	 * @throws Exception
     *         可能抛出数据库操作异常 
	 */
	@Transaction
    public void savePrimarySchool(PrimarySchool primarySchool) throws Exception {
       this.primarySchoolDao.insertPrimarySchool(primarySchool);
    }
    
    /**
	 * 
	 * 查询()列表信息 <br/>      
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        查询条件
	 * @return 返回列表
	 * @throws Exception 
	 *         可能抛出数据库操作异常  
	 */
    public PageResult listPrimarySchool(PrimarySchool primarySchool) throws Exception {
       return this.primarySchoolDao.listPrimarySchool(primarySchool);
    }
    
    /**
	 * 
	 * 分页查询()列表信息 <br/>   
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        查询条件
	 * @return 返回分页包装信息
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public PageResult pageListPrimarySchool(PrimarySchool primarySchool,int page, int limit) throws Exception {
       return this.primarySchoolDao.pageListPrimarySchool(primarySchool,page,limit);
    }
    
    /**
	 * 
	 * 更新()信息 <br/>      
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常  
	 */
    @Transaction
    public void updatePrimarySchool(PrimarySchool primarySchool) throws Exception {
       this.primarySchoolDao.updatePrimarySchool(primarySchool);
    }
  
    /**
	 * 
	 * 根据primarySchoolId获取()实例信息<br/>      
	 *  
	 * @author YZC  
	 * @param primarySchoolId 
	 *        的ID值
	 * @return 返回对应ID的()实体
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public PrimarySchool getPrimarySchoolById(Long primarySchoolId) throws Exception {
       return this.primarySchoolDao.getPrimarySchoolById(primarySchoolId);
    }
  
    /**
	 * 
	 * 根据primarySchoolId删除()信息<br/>      
	 *  
	 * @author YZC  
	 * @param primarySchoolId 
	 *        的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    @Transaction
    public void deletePrimarySchoolById(Long primarySchoolId) throws Exception {
       this.primarySchoolDao.deletePrimarySchoolById(primarySchoolId);
    }
    
    /**
	 * 
	 * 根据primarySchoolId删除()信息<br/>      
	 *  
	 * @author YZC  
	 * @param primarySchoolId 
	 *        的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    @Transaction
    public void batchDeletePrimarySchool(List<Long> ids) throws Exception {
       this.primarySchoolDao.batchDelete(ids);
    }
}