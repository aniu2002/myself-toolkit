package com.sparrow.app.information.service;

import com.sparrow.app.information.domain.PrimarySchool;
import com.sparrow.orm.dao.simple.NormalDao;
import com.sparrow.orm.page.PageResult;

import java.util.List;
/**
 * 完成数据(primary_school-)的增删改查功能<br/>
 * <p/>
 * primary_school:
 *
 * @author YZC
 * @version 2.0
 *          date: 2016-03-02 17:50:20
 */
public class PrimarySchoolService {
    private NormalDao baseDao;

    public void setBaseDao(NormalDao baseDao) {
        this.baseDao = baseDao;
    }

    /**
     * 根据primarySchoolId获取()实例信息<br/>
     *
     * @param primarySchoolId 的ID值
     * @return 返回对应ID的()实体
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public PrimarySchool get(Long primarySchoolId) {
        return this.baseDao.getById(PrimarySchool.class, primarySchoolId);
    }

    public boolean hasOpenId(String openId) {
        int n = this.baseDao.querySimple("select count(1) from primary_school where open_id=?",
                new Object[]{openId},
                Integer.class);
        return n > 0;
    }

    public PrimarySchool getUser(String openId) {
        try {
            PrimarySchool n = this.baseDao.queryObject("select * from primary_school where open_id=?",
                    new Object[]{openId},
                    PrimarySchool.class);
            return n;
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }
        return null;
    }

    /**
     * 增加保存()信息 <br/>
     *
     * @param primarySchool
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer add(PrimarySchool primarySchool) {
        if (this.hasOpenId(primarySchool.getOpenId())) return 0;
        return this.baseDao.save(primarySchool);
    }

    public Integer updateInfo(String phone, String name, String openId) {
        return this.baseDao.execute("update primary_school set phone=?,name=? where open_id=?",
                new Object[]{phone, name, openId});
    }

    /**
     * 根据primarySchoolId删除()信息<br/>
     *
     * @param primarySchoolId 的ID值
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer delete(Long primarySchoolId) {
        return this.baseDao.delete(PrimarySchool.class, primarySchoolId);
    }

    /**
     * 更新()信息 <br/>
     *
     * @param primarySchool
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer update(PrimarySchool primarySchool) {
        return this.baseDao.update(primarySchool);
    }

    /**
     * 分页查询()列表信息 <br/>
     *
     * @param primarySchool 查询条件
     * @return 返回分页包装信息
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public PageResult pageQuery(PrimarySchool primarySchool, int page, int limit) {
        return this.baseDao.pageQuery(primarySchool, page, limit);
    }

    /**
     * 批量删除()信息<br/>
     *
     * @param primarySchoolIds 的ID值
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer batchDelete(List<Long> primarySchoolIds) {
        return this.baseDao.batchDelete(PrimarySchool.class, primarySchoolIds);
    }

    /**
     * 批量增加()信息<br/>
     *
     * @param primarySchools 批量值
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer batchAdd(List<PrimarySchool> primarySchools) {
        return this.baseDao.batchAdd(primarySchools);
    }

    /**
     * 批量更新()信息<br/>
     *
     * @param primarySchools 批量值
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer batchUpdate(List<PrimarySchool> primarySchools) {
        return this.baseDao.batchUpdate(primarySchools);
    }
}