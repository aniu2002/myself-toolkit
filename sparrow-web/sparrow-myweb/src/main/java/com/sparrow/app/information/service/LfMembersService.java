package com.sparrow.app.information.service;

import com.sparrow.app.information.domain.LfMembers;
import com.sparrow.orm.dao.simple.NormalDao;
import com.sparrow.orm.page.PageResult;

import java.util.Date;
import java.util.List;

/**
 * 完成数据(lf_members-)的增删改查功能<br/>
 * <p/>
 * lf_members:
 *
 * @author YZC
 * @version 2.0
 *          date: 2016-02-22 18:19:18
 */
public class LfMembersService {
    private NormalDao baseDao;

    public void setBaseDao(NormalDao baseDao) {
        this.baseDao = baseDao;
    }

    public void updateMembersImg(Long id, String imgs) {
        this.baseDao.execute("update lf_members set images=?,update_date=? where id=?",
                new Object[]{imgs, new Date(), id});
    }

    public boolean hasQqIn(String qq) {
        int n = this.baseDao.querySimple("select count(1) from lf_members where qq=?",
                new Object[]{qq},
                Integer.class);
        return n > 0;
    }

    /**
     * 根据lfMembersId获取()实例信息<br/>
     *
     * @param lfMembersId 的ID值
     * @return 返回对应ID的()实体
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public LfMembers get(Long lfMembersId) {
        return this.baseDao.getById(LfMembers.class, lfMembersId);
    }

    /**
     * 增加保存()信息 <br/>
     *
     * @param lfMembers
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer add(LfMembers lfMembers) {
        if (this.hasQqIn(lfMembers.getQq())) return 0;
        return this.baseDao.save(lfMembers);
    }

    /**
     * 根据lfMembersId删除()信息<br/>
     *
     * @param lfMembersId 的ID值
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer delete(Long lfMembersId) {
        return this.baseDao.delete(LfMembers.class, lfMembersId);
    }

    /**
     * 更新()信息 <br/>
     *
     * @param lfMembers
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer update(LfMembers lfMembers) {
        return this.baseDao.update(lfMembers);
    }

    /**
     * 分页查询()列表信息 <br/>
     *
     * @param lfMembers 查询条件
     * @return 返回分页包装信息
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public PageResult pageQuery(LfMembers lfMembers, int page, int limit) {
        return this.baseDao.pageQuery(lfMembers, page, limit);
    }

    /**
     * 批量删除()信息<br/>
     *
     * @param lfMembersIds 的ID值
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer batchDelete(List<Long> lfMembersIds) {
        return this.baseDao.batchDelete(LfMembers.class, lfMembersIds);
    }

    /**
     * 批量增加()信息<br/>
     *
     * @param lfMemberss 批量值
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer batchAdd(List<LfMembers> lfMemberss) {
        return this.baseDao.batchAdd(lfMemberss);
    }

    /**
     * 批量更新()信息<br/>
     *
     * @param lfMemberss 批量值
     * @return
     * @throws Exception 可能抛出数据库操作异常
     * @author YZC
     */
    public Integer batchUpdate(List<LfMembers> lfMemberss) {
        return this.baseDao.batchUpdate(lfMemberss);
    }
}