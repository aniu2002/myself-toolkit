package com.sparrow.collect.domain;

import java.sql.Timestamp;

/**
 *	表模型: "" <br/>
 *  ============================== <br/>
 *	选择: SELECT * FROM crawl_data WHERE uuid=? <br/>
 *	统计: SELECT COUNT(1) FROM crawl_data <br/>
 *	插入: INSERT INTO crawl_data(site_id,site_name,site_url,page_url,detail_url,title,subject,content,publish_time,time) VALUES(:siteId,:siteName,:siteUrl,:pageUrl,:detailUrl,:title,:subject,:content,:publishTime,:time) <br/>
 *	更新: UPDATE crawl_data SET site_id=:siteId,site_name=:siteName,site_url=:siteUrl,page_url=:pageUrl,detail_url=:detailUrl,title=:title,subject=:subject,content=:content,publish_time=:publishTime,time=:time WHERE uuid=:uuid <br/>
 *	删除: DELETE FROM crawl_data WHERE uuid=? <br/>
 *  ============================== <br/>
 * @author YZC
 */
public class CrawlData {
	/** 唯一id(uuid) */
	private String uuid;
	/** 站点id(site_id) */
	private String siteId;
	/** 站点名称(site_name) */
	private String siteName;
	/** 站点url(site_url) */
	private String siteUrl;
	/** 分页入口(page_url) */
	private String pageUrl;
	/** 详情页(detail_url) */
	private String detailUrl;
	/** 标题(title) */
	private String title;
	/** 主题(subject) */
	private String subject;
	/** 内容(content) */
	private String content;
	/** 发布时间(publish_time) */
	private Timestamp publishTime;
	/** 抓取时间(time) */
	private Timestamp time;

	/**
	 * 
	 * 获取唯一id值    
	 *  
	 * @return 唯一id(String)
	 */
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * 
	 * 设置唯一id值   
	 *  
	 * @param uuid 
	 *        唯一id
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	/**
	 * 
	 * 获取站点id值    
	 *  
	 * @return 站点id(String)
	 */
	public String getSiteId() {
		return siteId;
	}
	
	/**
	 * 
	 * 设置站点id值   
	 *  
	 * @param siteId 
	 *        站点id
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	/**
	 * 
	 * 获取站点名称值    
	 *  
	 * @return 站点名称(String)
	 */
	public String getSiteName() {
		return siteName;
	}
	
	/**
	 * 
	 * 设置站点名称值   
	 *  
	 * @param siteName 
	 *        站点名称
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	/**
	 * 
	 * 获取站点url值    
	 *  
	 * @return 站点url(String)
	 */
	public String getSiteUrl() {
		return siteUrl;
	}
	
	/**
	 * 
	 * 设置站点url值   
	 *  
	 * @param siteUrl 
	 *        站点url
	 */
	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}
	/**
	 * 
	 * 获取分页入口值    
	 *  
	 * @return 分页入口(String)
	 */
	public String getPageUrl() {
		return pageUrl;
	}
	
	/**
	 * 
	 * 设置分页入口值   
	 *  
	 * @param pageUrl 
	 *        分页入口
	 */
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	/**
	 * 
	 * 获取详情页值    
	 *  
	 * @return 详情页(String)
	 */
	public String getDetailUrl() {
		return detailUrl;
	}
	
	/**
	 * 
	 * 设置详情页值   
	 *  
	 * @param detailUrl 
	 *        详情页
	 */
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}
	/**
	 * 
	 * 获取标题值    
	 *  
	 * @return 标题(String)
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * 
	 * 设置标题值   
	 *  
	 * @param title 
	 *        标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 
	 * 获取主题值    
	 *  
	 * @return 主题(String)
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * 
	 * 设置主题值   
	 *  
	 * @param subject 
	 *        主题
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * 
	 * 获取内容值    
	 *  
	 * @return 内容(String)
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * 
	 * 设置内容值   
	 *  
	 * @param content 
	 *        内容
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 
	 * 获取发布时间值    
	 *  
	 * @return 发布时间(Timestamp)
	 */
	public Timestamp getPublishTime() {
		return publishTime;
	}
	
	/**
	 * 
	 * 设置发布时间值   
	 *  
	 * @param publishTime 
	 *        发布时间
	 */
	public void setPublishTime(Timestamp publishTime) {
		this.publishTime = publishTime;
	}
	/**
	 * 
	 * 获取抓取时间值    
	 *  
	 * @return 抓取时间(Timestamp)
	 */
	public Timestamp getTime() {
		return time;
	}
	
	/**
	 * 
	 * 设置抓取时间值   
	 *  
	 * @param time 
	 *        抓取时间
	 */
	public void setTime(Timestamp time) {
		this.time = time;
	}
}