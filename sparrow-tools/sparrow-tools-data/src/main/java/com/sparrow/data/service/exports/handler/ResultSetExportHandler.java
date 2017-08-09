package com.sparrow.data.service.exports.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetExportHandler extends ExportHandler<ResultSet> {

	/**
	 * 
	 * 从resultSet获取记录
	 * 
	 * @param t
	 *            数据库记录
	 * @param dataIndex
	 *            避免创建重复的数据
	 * @throws SQLException
	 * @author YZC
	 */
	@Override
	public String fetchValue(ResultSet t, int dataIndex) {
		try {
			return t.getString(dataIndex + 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
