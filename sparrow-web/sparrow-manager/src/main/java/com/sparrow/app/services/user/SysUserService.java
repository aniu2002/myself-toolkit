package com.sparrow.app.services.user;

import com.sparrow.common.source.OptionItem;
import com.sparrow.orm.dao.simple.Checker;
import com.sparrow.orm.dao.simple.NormalDao;
import com.sparrow.orm.page.PageResult;
import com.sparrow.orm.session.Session;

import java.util.List;

public class SysUserService {
	private NormalDao baseDao;

	public void setBaseDao(NormalDao baseDao) {
		this.baseDao = baseDao;
	}

	public SysUser get(Long id) {
		return this.baseDao.getById(SysUser.class, id);
	}

	public Integer add(SysUser sysUser) {
		return this.baseDao.save(sysUser, userChecker);
	}

	public Integer delete(Long id) {
		return this.baseDao.delete(SysUser.class, id);
	}

	public Integer update(SysUser sysUser) {
		return this.baseDao.update(sysUser);
	}

	public PageResult pageQuery(SysUser sysUser, int page, int limit) {
		return this.baseDao.pageQuery(sysUser, page, limit);
	}

	public Integer batchDelete(List<Long> ids) {
		return this.baseDao.batchDelete(SysUser.class, ids);
	}

	public Integer batchAdd(List<SysUser> sysUsers) {
		return this.baseDao.batchAdd(sysUsers);
	}

	public SysUser findUser(String loginName, String pwd) {
		String sql = "SELECT * FROM sysUser WHERE login_name=? AND login_pwd=?";
		return this.baseDao.queryObject(sql, new Object[] { loginName, pwd },
				SysUser.class);
	}

	public SysUser findUser(String loginName) {
		String sql = "SELECT * FROM sysUser WHERE login_name=? ";
		return this.baseDao.queryObject(sql, new Object[] { loginName },
				SysUser.class);
	}

	public List<OptionItem> getUsers() {
		String sql = "SELECT login_name as 'code',user_name as 'name' from sysUser";
		return this.baseDao.queryList(sql, OptionItem.class);
	}

	private Checker userChecker = new Checker() {
		@Override
		public boolean check(Object bean, Session session) throws Exception {
			SysUser sysUser = (SysUser) bean;
			Integer n = session.querySimple(
					"SELECT COUNT(1) FROM sysUser WHERE login_name=?",
					new Object[] { sysUser.getLoginName() }, Integer.class);
			if (n > 0)
				throw new Exception("用户登录名[" + sysUser.getLoginName() + "]重复");
			return true;
		}
	};
}