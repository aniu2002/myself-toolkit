package com.sparrow.app.information.domain;


/**
 *	表模型: "" <br/>
 *  ============================== <br/>
 *	选择: SELECT * FROM primary_school WHERE id=? <br/>
 *	统计: SELECT COUNT(1) FROM primary_school <br/>
 *	插入: INSERT INTO primary_school(id,open_id,name,phone,nick_name,sex,language,city,province,country,head_image) VALUES(:id,:openId,:name,:phone,:nickName,:sex,:language,:city,:province,:country,:headImage) <br/>
 *	更新: UPDATE primary_school SET open_id=:openId,name=:name,phone=:phone,nick_name=:nickName,sex=:sex,language=:language,city=:city,province=:province,country=:country,head_image=:headImage WHERE id=:id <br/>
 *	删除: DELETE FROM primary_school WHERE id=? <br/>
 *  ============================== <br/>
 * @author YZC
 */
public class PrimarySchool {
	/** ID(id) */
	private Long id;
	/** 唯一号(open_id) */
	private String openId;
	/** 姓名(name) */
	private String name;
	/** 电话(phone) */
	private String phone;
	/** 昵称(nick_name) */
	private String nickName;
	/** 性别(sex) */
	private Integer sex;
	/** 语言(language) */
	private String language;
	/** 城市(city) */
	private String city;
	/** 省份(province) */
	private String province;
	/** 国家(country) */
	private String country;
	/** 头像(head_image) */
	private String headImage;

	/**
	 * 
	 * 获取ID值    
	 *  
	 * @return ID(Long)
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * 
	 * 设置ID值   
	 *  
	 * @param id 
	 *        ID
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 
	 * 获取唯一号值    
	 *  
	 * @return 唯一号(String)
	 */
	public String getOpenId() {
		return openId;
	}
	
	/**
	 * 
	 * 设置唯一号值   
	 *  
	 * @param openId 
	 *        唯一号
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	/**
	 * 
	 * 获取姓名值    
	 *  
	 * @return 姓名(String)
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * 设置姓名值   
	 *  
	 * @param name 
	 *        姓名
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * 获取电话值    
	 *  
	 * @return 电话(String)
	 */
	public String getPhone() {
		return phone;
	}
	
	/**
	 * 
	 * 设置电话值   
	 *  
	 * @param phone 
	 *        电话
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * 
	 * 获取昵称值    
	 *  
	 * @return 昵称(String)
	 */
	public String getNickName() {
		return nickName;
	}
	
	/**
	 * 
	 * 设置昵称值   
	 *  
	 * @param nickName 
	 *        昵称
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	/**
	 * 
	 * 获取性别值    
	 *  
	 * @return 性别(Integer)
	 */
	public Integer getSex() {
		return sex;
	}
	
	/**
	 * 
	 * 设置性别值   
	 *  
	 * @param sex 
	 *        性别
	 */
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	/**
	 * 
	 * 获取语言值    
	 *  
	 * @return 语言(String)
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * 
	 * 设置语言值   
	 *  
	 * @param language 
	 *        语言
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	/**
	 * 
	 * 获取城市值    
	 *  
	 * @return 城市(String)
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * 
	 * 设置城市值   
	 *  
	 * @param city 
	 *        城市
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * 
	 * 获取省份值    
	 *  
	 * @return 省份(String)
	 */
	public String getProvince() {
		return province;
	}
	
	/**
	 * 
	 * 设置省份值   
	 *  
	 * @param province 
	 *        省份
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * 
	 * 获取国家值    
	 *  
	 * @return 国家(String)
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * 
	 * 设置国家值   
	 *  
	 * @param country 
	 *        国家
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * 
	 * 获取头像值    
	 *  
	 * @return 头像(String)
	 */
	public String getHeadImage() {
		return headImage;
	}
	
	/**
	 * 
	 * 设置头像值   
	 *  
	 * @param headImage 
	 *        头像
	 */
	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}
}