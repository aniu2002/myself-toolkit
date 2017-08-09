package com.sparrow.app.information.service.school;

import java.util.List;



import com.sparrow.orm.page.PageResult;

import com.sparrow.app.information.domain.school.PrimarySchool;

/**
 * 
 * 完成数据库表(primary_school-)的增删改查功能<br/>  
 *
 * primary_school:  
 *
 * @author YZC  
 * @version 2.0
 * date: 2017-08-03 01:32:20
 */
public interface PrimarySchoolService {

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
    public void savePrimarySchool(PrimarySchool primarySchool) throws Exception ;
    
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
    public PageResult listPrimarySchool(PrimarySchool primarySchool) throws Exception ;
  
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
    public PageResult pageListPrimarySchool(PrimarySchool primarySchool,int page, int limit) throws Exception ;
  
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
    public void updatePrimarySchool(PrimarySchool primarySchool) throws Exception ;
  
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
    public PrimarySchool getPrimarySchoolById(Long primarySchoolId) throws Exception ;
    
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
    public void deletePrimarySchoolById(Long primarySchoolId) throws Exception ;
    
    public void batchDeletePrimarySchool(List<Long> ids) throws Exception;
}