package com.sparrow.data.service.imports.data;

/**
 * 
 * 批量导入的处理结果
 * 
 * @author YZC
 * @version 1.0 (2014-3-31)
 * @modify
 */
public class ImportResult {
	public static final ImportResult SUCCESS = new ImportResult("success", true);
	public static final ImportResult FAILURED = new ImportResult("failured",
			false);
	/** 导入名称 */
	private String name;
	/** 导入的处理结果，可能是validateError的list 或者是 一个写入错误记录的CSV文件 */
	private Object result;
	/** 导入成功记录数 */
	private int successNum;
	/** 校验失败的记录数 */
	private int failureNum;
	/** 导入总记录数 */
	private int totalRecords;
	/** 本次处理是否都成功 */
	private boolean ok;

	public ImportResult() {

	}

	public ImportResult(String name, boolean ok) {
		this.name = name;
		this.ok = ok;
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}

	public int getFailureNum() {
		return failureNum;
	}

	public void setFailureNum(int failureNum) {
		this.failureNum = failureNum;
	}

	public void clear() {
		this.result = null;
	}
}
