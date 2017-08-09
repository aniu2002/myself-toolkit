package com.sparrow.app.information.dao.school.impl;

import java.util.List;
import com.sparrow.service.annotation.Repository;
import com.sparrow.orm.page.PageResult;


import com.sparrow.orm.dao.BaseDao;
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
@Repository(lazy = true, value = "primarySchoolDao")
public class PrimarySchoolDaoImpl extends BaseDao implements PrimarySchoolDao {

	/**
	 * 
	 * 插入一条()记录 <br/>    
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        
	 * @return
	 * @throws Exception
     *         可能抛出数据库操作异常 
	 */
    public void insertPrimarySchool(PrimarySchool primarySchool) throws Exception {
       this.save(primarySchool);
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
	 *         可能抛出数据库操作异常  
	 */
    public PageResult listPrimarySchool(PrimarySchool primarySchool) throws Exception {
       return this.findPojoByPage(primarySchool,0,200);
    }
    
    /**
	 * 
	 * 分页查询()记录 <br/>   
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        查询条件
	 * @return 返回分页包装信息
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public PageResult pageListPrimarySchool(PrimarySchool primarySchool,int page, int limit) throws Exception {
       return this.findPojoByPage(primarySchool,page,limit);
    }
   
    /**
	 * 
	 * 更新()记录 <br/>      
	 *  
	 * @author YZC  
	 * @param primarySchool 
	 *        
	 * @return
	 * @throws Exception
     *         可能抛出数据库操作异常  
	 */
    public void updatePrimarySchool(PrimarySchool primarySchool) throws Exception {
       this.update(primarySchool);
    }
  
    /**
	 * 
	 * 根据primarySchoolId获取()记录<br/>      
	 *  
	 * @author YZC  
	 * @param primarySchoolId 
	 *        的ID值
	 * @return 返回对应ID的()实体 
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public PrimarySchool getPrimarySchoolById(Long primarySchoolId) throws Exception {
       return this.get(PrimarySchool.class,primarySchoolId);
    }
  
    /**
	 * 
	 * 根据primarySchoolId删除()记录<br/>      
	 *  
	 * @author YZC  
	 * @param primarySchoolId 
	 *        的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public void deletePrimarySchoolById(Long primarySchoolId) throws Exception {
       this.remove(PrimarySchool.class,primarySchoolId);
    }
    
    /**
	 * 
	 * 根据primarySchoolId删除()记录<br/>      
	 *  
	 * @author YZC  
	 * @param primarySchoolId 
	 *        的ID值
	 * @return
	 * @throws Exception 
	 *         可能抛出数据库操作异常 
	 */
    public void batchDelete(List<Long> ids) throws Exception {
       this.removeBatch(PrimarySchool.class,ids);
    }
}