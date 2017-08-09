package com.sparrow.service.interceptor;

import com.sparrow.core.aop.MethodInterceptor;
import com.sparrow.core.log.SysLogger;
import com.sparrow.orm.trans.TransManager;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * 用于 事务 进出拦截器的Aop调用,这个拦截器不会改变原有方法的行为
 * 
 * @author aniu
 * 
 */
public class TranscationInterceptor implements MethodInterceptor {
	private TransManager transManager;
	private String name;

	public TransManager getTransManager() {
		return transManager;
	}

	public void setTransManager(TransManager transManager) {
		this.transManager = transManager;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		if (this.transManager != null) {
			this.transManager.begin(Connection.TRANSACTION_READ_COMMITTED);
			this.log("--- Open transaction   ---");
		}
		return true;
	}

	@Override
	public Object afterInvoke(Object obj, Object returnObj, Method method,
			Object... args) {
		if (this.transManager != null) {
			this.log("--- Commit transaction ---");
			this.transManager.commit();
		}
		return returnObj;
	}

	@Override
	public boolean whenException(Exception e, Object obj, Method method,
			Object... args) {
		SysLogger.error(e.getMessage());
		if (this.transManager != null) {
			SysLogger.error("--- Rollback transaction ---");
			this.transManager.rollback();
		}
		return true;
	}

	@Override
	public boolean whenError(Throwable e, Object obj, Method method,
			Object... args) {
		if (this.transManager != null) {
			SysLogger.error("--- Rollback transaction ---");
			this.transManager.rollback();
		}
		return true;
	}

	void log(String info) {
		SysLogger.info(info);
	}
}
