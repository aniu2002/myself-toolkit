package com.sparrow.orm.trans;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {
	private Connection connection;
	private boolean autoComit;
	private boolean hasBegin = false;
	private int oldLevel;
	private int level;

	public Transaction(Connection connection) {
		this.connection = connection;
		if (this.connection != null)
			this.beginTranscation();
	}

	void beginTranscation() {
		if (this.hasBegin)
			return;
		try {
			this.hasBegin = true;
			this.oldLevel = this.connection.getTransactionIsolation();
			this.autoComit = this.connection.getAutoCommit();
			this.connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			this.hasBegin = false;
		}
	}

	public void commit() {
		if (!this.hasBegin)
			return;
		try {
			this.connection.commit();
			this.connection.setAutoCommit(this.autoComit);
			// 恢复旧的事务级别
			if (this.connection.getTransactionIsolation() != this.oldLevel)
				this.connection.setTransactionIsolation(this.oldLevel);
			this.hasBegin = false;
		} catch (SQLException e) {
			e.printStackTrace();
			this.rollBack();
		}
	}

	public void rollBack() {
		try {
			this.connection.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isBegin() {
		return hasBegin;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		if (this.level <= 0) {
			this.level = level;
			if (this.oldLevel != level)
				try {
					this.connection.setTransactionIsolation(level);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
}
