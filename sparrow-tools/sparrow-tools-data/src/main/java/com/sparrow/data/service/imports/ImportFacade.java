package com.sparrow.data.service.imports;

import java.io.File;
import java.util.Map;

import com.sparrow.data.service.imports.data.ImportResult;
import com.sparrow.data.tools.store.FileType;

/**
 * 
 * 导入导出门面类，提供保存模板方法、提取excel模板输入文件、抽取excel文件数据根据模板配置，批量导入数据
 * 
 * @author YZC
 * @version 1.0 (2014-3-17)
 * @modify
 */
public interface ImportFacade {
	/**
	 * 
	 * 保存模板方法
	 * 
	 * @param name
	 *            模板名,可以是 import:test1
	 * @param importConfig
	 *            导入配置
	 * @param excelTemplate
	 *            excel数据模板
	 * @author YZC
	 */
	void saveTemplate(String name, File importConfig, File excelTemplate);

	/**
	 * 
	 * 提取excel模板输入文件
	 * 
	 * @param name
	 *            模板名,可以是 import
	 * @return 返回excel模板文件
	 * @author YZC
	 */
	File getExcelTemplate(String name) throws Exception;

	/**
	 * 
	 * 抽取excel文件数据根据模板配置，执行批量导入数据
	 * 
	 * @param excelImportFile
	 *            excel导入文件
	 * @param name
	 *            导入配置模板名
	 * @author YZC
	 */
	void batchImport(File excelImportFile, String name);

	/**
	 * 
	 * 抽取excel文件数据根据模板配置，执行批量导入数据
	 * 
	 * @param excelImportFile
	 *            excel导入文件
	 * @param name
	 *            导入配置模板名
	 * @author YZC
	 */
	ImportResult batchImport(File excelImportFile, String name,
			Map<String, Object> variables);

	/**
	 * 
	 * 抽取excel文件数据根据模板配置，执行批量导入数据
	 * 
	 * @param importFile
	 *            批量导入文件
	 * @param name
	 *            导入配置模板名
	 * @param importFileType
	 *            文件类型（csv,excel)
	 * @author YZC
	 */
	ImportResult batchImport(File importFile, String name, FileType importFileType);

	/**
	 * 
	 * 抽取excel文件数据根据模板配置，执行批量导入数据
	 * 
	 * @param importFile
	 *            批量导入文件
	 * @param name
	 *            批量导入文件
	 * @param importFileType
	 *            文件类型（csv,excel)
	 * @param variables
	 *            变量信息，sql查询的parameter参数
	 * @author YZC
	 */
	ImportResult batchImport(File importFile, String name, FileType importFileType,
			Map<String, Object> variables);
}
