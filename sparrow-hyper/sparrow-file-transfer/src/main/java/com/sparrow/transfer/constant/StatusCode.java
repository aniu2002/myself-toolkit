package com.sparrow.transfer.constant;

public class StatusCode {
	/** 命令操作成功 */
	public final static String OPERATE_SUCCESS = "100";
	/** 协议错误 */
	public final static String PROTOCOL_UNKOWN = "301";
	/** 协议解析错误 */
	public final static String PROTOCOL_PARSER_ERROR = "302";
	/** 任务接收套接字读取错误 */
	public final static String SOCKET_ERROR = "303";
	/** 不可预知的错误 */
	public final static String UNKNOW_ERROR = "304";
	/** 无效的操作命令 */
	public final static String UNKNOW_OPERATE = "305";
	/** 无效的 EMB TYPE */
	public final static String UNKNOW_EMB_TYPE = "306";
	/** 无效的 EMB 请求 */
	public final static String UNKNOW_EMB_REQUEST = "307";
	/** 资源错误创建错误 */
	public final static String TARGET_CREATE_ERROR = "400";
	/** 资源错误,未初始化 */
	public final static String TARGET_NOT_INIT = "401";
	/** 资源错误,资源不存在 */
	public final static String TARGET_NOT_EXIST = "402";
	/** 资源错误,host不存在 */
	public final static String HOST_EMPTY = "403";
	/** 资源错误,path不存在 */
	public final static String PATH_EMPTY = "404";
	/** 资源错误,FTP服务器连接错误 */
	public final static String FTP_CONNECT_ERROR = "405";
	/** 资源错误,FTP登陆错误 */
	public final static String FTP_LOGIN_ERROR = "406";
	/** 资源错误,文件无法映射 */
	public final static String FILE_MAPPING_ERROR = "407";
	/** 文件创建错误 */
	public final static String FILE_CREATE_ERROR = "408";
	/** 任务解析错误 */
	public final static String TASK_PARSER_ERROR = "409";
	/** 任务迁移时错误 */
	public final static String TRANSFER_ERROR = "410";
	/** 任务源错误 */
	public final static String TASK_SOURCE_ERROR = "411";
	/** 任务目标错误 */
	public final static String TASK_TARGET_ERROR = "412";
}
