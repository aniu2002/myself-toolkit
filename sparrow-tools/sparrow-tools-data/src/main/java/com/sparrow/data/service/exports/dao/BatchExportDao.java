package com.sparrow.data.service.exports.dao;

import java.util.Map;

import com.sparrow.data.service.imports.data.ImportTemplate;
import com.sparrow.data.tools.exports.writer.DataWriter;

/**
 * 
 * 批量导出模板类，根据模板配置的sql导出
 * 
 * @author YZC
 * @version 1.0 (2014-3-29)
 * @modify
 */
public interface BatchExportDao {

	/**
	 * 
	 * 根据sql导出excel，没有模板按照，resultSet响应的meta自然顺序作为输出列的顺序
	 * 
	 * @param sql
	 *            通常在模板中配置，导出sql语句，也可以外部传入
	 * @param paramMap
	 *            sql命名参数信息
	 * @param dataWriter
	 *            导出writer写入器
	 * @author YZC
	 */
	void batchExport(String sql, Map<String, Object> paramMap,
			DataWriter dataWriter);

	/**
	 * 
	 * 根据sql导出excel，没有模板按照，resultSet响应的meta自然顺序作为输出列的顺序，如果有模板，
	 * 则根据item配置构造列位置信息与结果集的索引
	 * 
	 * @param sql
	 *            通常在模板中配置，导出sql语句，也可以外部传入
	 * @param paramMap
	 *            sql命名参数信息
	 * @param dataWriter
	 *            导出writer写入器
	 * @param template
	 *            导出模板与导入模板一致
	 * @author YZC
	 */
	void batchExport(String sql, Map<String, Object> paramMap,
			DataWriter dataWriter, ImportTemplate template);

	/**
	 * 
	 * 根据sql导出excel，没有模板按照，resultSet响应的meta自然顺序作为输出列的顺序，如果有模板，
	 * 则根据item配置构造列位置信息与结果集的索引
	 * 
	 * @param sql
	 *            通常在模板中配置，导出sql语句，也可以外部传入
	 * @param arguments
	 *            sql参数信息
	 * @param dataWriter
	 *            导出writer写入器
	 * @param template
	 *            导出模板与导入模板一致
	 * @author YZC
	 */
	void batchExport(String sql, Object arguments[], DataWriter dataWriter,
			ImportTemplate template);

}
