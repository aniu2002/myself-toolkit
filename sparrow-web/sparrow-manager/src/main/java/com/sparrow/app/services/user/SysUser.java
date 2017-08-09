package com.sparrow.app.services.user;

/**
 * 表模型: "系统用户" <br/>
 * ============================== <br/>
 * 选择: SELECT * FROM sysUser WHERE id=? <br/>
 * 统计: SELECT COUNT(1) FROM sysUser <br/>
 * 插入: INSERT INTO sysUser(user_name,login_name,login_pwd,user_phone,user_email)
 * VALUES(:userName,:loginName,:loginPwd,:userPhone,:userEmail) <br/>
 * 更新: UPDATE sysUser SET
 * user_name=:userName,login_name=:loginName,login_pwd=:loginPwd
 * ,user_phone=:userPhone,user_email=:userEmail WHERE id=:id <br/>
 * 删除: DELETE FROM sysUser WHERE id=? <br/>
 * 
 * @author YZC
 */
public class SysUser {
	/** 自增id(id) */
	private Long id;
	/** 用户名(user_name) */
	private String userName;
	/** 登陆名(login_name) */
	private String loginName;
	/** 登陆密码(login_pwd) */
	private String loginPwd;
	/** 用户电话(user_phone) */
	private String userPhone;
	/** 用户邮箱(user_email) */
	private String userEmail;
	/** 用户组(user_group) */
	private String userGroup;

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	/**
	 * 
	 * 获取自增id值
	 * 
	 * @return 自增id(Long)
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * 设置自增id值
	 * 
	 * @param id
	 *            自增id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * 获取用户名值
	 * 
	 * @return 用户名(String)
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 
	 * 设置用户名值
	 * 
	 * @param userName
	 *            用户名
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 
	 * 获取登陆名值
	 * 
	 * @return 登陆名(String)
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * 
	 * 设置登陆名值
	 * 
	 * @param loginName
	 *            登陆名
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * 
	 * 获取登陆密码值
	 * 
	 * @return 登陆密码(String)
	 */
	public String getLoginPwd() {
		return loginPwd;
	}

	/**
	 * 
	 * 设置登陆密码值
	 * 
	 * @param loginPwd
	 *            登陆密码
	 */
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	/**
	 * 
	 * 获取用户电话值
	 * 
	 * @return 用户电话(String)
	 */
	public String getUserPhone() {
		return userPhone;
	}

	/**
	 * 
	 * 设置用户电话值
	 * 
	 * @param userPhone
	 *            用户电话
	 */
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	/**
	 * 
	 * 获取用户邮箱值
	 * 
	 * @return 用户邮箱(String)
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * 
	 * 设置用户邮箱值
	 * 
	 * @param userEmail
	 *            用户邮箱
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}