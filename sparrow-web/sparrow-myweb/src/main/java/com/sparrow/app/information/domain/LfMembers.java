package com.sparrow.app.information.domain;

import java.sql.Timestamp;

/**
 *	表模型: "" <br/>
 *  ============================== <br/>
 *	选择: SELECT * FROM lf_members WHERE id=? <br/>
 *	统计: SELECT COUNT(1) FROM lf_members <br/>
 *	插入: INSERT INTO lf_members(name,qq,sex,age,bra,phone,province,city,district,referee_name,referee_qq,price_p,price_pp,price_desc,simple_desc,special,checked,leval,comment,images,create_date,update_date,mark) VALUES(:name,:qq,:sex,:age,:bra,:phone,:province,:city,:district,:refereeName,:refereeQq,:priceP,:pricePp,:priceDesc,:simpleDesc,:special,:checked,:leval,:comment,:images,:createDate,:updateDate,:mark) <br/>
 *	更新: UPDATE lf_members SET name=:name,qq=:qq,sex=:sex,age=:age,bra=:bra,phone=:phone,province=:province,city=:city,district=:district,referee_name=:refereeName,referee_qq=:refereeQq,price_p=:priceP,price_pp=:pricePp,price_desc=:priceDesc,simple_desc=:simpleDesc,special=:special,checked=:checked,leval=:leval,comment=:comment,images=:images,create_date=:createDate,update_date=:updateDate,mark=:mark WHERE id=:id <br/>
 *	删除: DELETE FROM lf_members WHERE id=? <br/>
 *  ============================== <br/>
 * @author YZC
 */
public class LfMembers {
	/** ID(id) */
	private Long id;
	/** 昵称(name) */
	private String name;
	/** QQ(qq) */
	private String qq;
	/** 性别(sex) */
	private String sex;
	/** 年龄(age) */
	private Integer age;
	/** 罩B(bra) */
	private String bra;
	/** 手机(phone) */
	private String phone;
	/** 省(province) */
	private String province;
	/** 市(city) */
	private String city;
	/** 区域(district) */
	private String district;
	/** 推荐人(referee_name) */
	private String refereeName;
	/** 推荐qq(referee_qq) */
	private String refereeQq;
	/** 价格P(price_p) */
	private Integer priceP;
	/** 价格PP(price_pp) */
	private Integer pricePp;
	/** 价格描述(price_desc) */
	private String priceDesc;
	/** 描述(simple_desc) */
	private String simpleDesc;
	/** 特长(special) */
	private String special;
	/** 体验否(checked) */
	private Integer checked;
	/** 级别(leval) */
	private Integer leval;
	/** 评价(comment) */
	private String comment;
	/** 图片(images) */
	private String images;
	/** 创建时间(create_date) */
	private Timestamp createDate;
	/** 更新时间(update_date) */
	private Timestamp updateDate;
	/** 标记(mark) */
	private Integer mark;

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
	 * 获取昵称值    
	 *  
	 * @return 昵称(String)
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * 设置昵称值   
	 *  
	 * @param name 
	 *        昵称
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * 获取QQ值    
	 *  
	 * @return QQ(String)
	 */
	public String getQq() {
		return qq;
	}
	
	/**
	 * 
	 * 设置QQ值   
	 *  
	 * @param qq 
	 *        QQ
	 */
	public void setQq(String qq) {
		this.qq = qq;
	}
	/**
	 * 
	 * 获取性别值    
	 *  
	 * @return 性别(String)
	 */
	public String getSex() {
		return sex;
	}
	
	/**
	 * 
	 * 设置性别值   
	 *  
	 * @param sex 
	 *        性别
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}
	/**
	 * 
	 * 获取年龄值    
	 *  
	 * @return 年龄(Integer)
	 */
	public Integer getAge() {
		return age;
	}
	
	/**
	 * 
	 * 设置年龄值   
	 *  
	 * @param age 
	 *        年龄
	 */
	public void setAge(Integer age) {
		this.age = age;
	}
	/**
	 * 
	 * 获取罩B值    
	 *  
	 * @return 罩B(String)
	 */
	public String getBra() {
		return bra;
	}
	
	/**
	 * 
	 * 设置罩B值   
	 *  
	 * @param bra 
	 *        罩B
	 */
	public void setBra(String bra) {
		this.bra = bra;
	}
	/**
	 * 
	 * 获取手机值    
	 *  
	 * @return 手机(String)
	 */
	public String getPhone() {
		return phone;
	}
	
	/**
	 * 
	 * 设置手机值   
	 *  
	 * @param phone 
	 *        手机
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * 
	 * 获取省值    
	 *  
	 * @return 省(String)
	 */
	public String getProvince() {
		return province;
	}
	
	/**
	 * 
	 * 设置省值   
	 *  
	 * @param province 
	 *        省
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * 
	 * 获取市值    
	 *  
	 * @return 市(String)
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * 
	 * 设置市值   
	 *  
	 * @param city 
	 *        市
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * 
	 * 获取区域值    
	 *  
	 * @return 区域(String)
	 */
	public String getDistrict() {
		return district;
	}
	
	/**
	 * 
	 * 设置区域值   
	 *  
	 * @param district 
	 *        区域
	 */
	public void setDistrict(String district) {
		this.district = district;
	}
	/**
	 * 
	 * 获取推荐人值    
	 *  
	 * @return 推荐人(String)
	 */
	public String getRefereeName() {
		return refereeName;
	}
	
	/**
	 * 
	 * 设置推荐人值   
	 *  
	 * @param refereeName 
	 *        推荐人
	 */
	public void setRefereeName(String refereeName) {
		this.refereeName = refereeName;
	}
	/**
	 * 
	 * 获取推荐qq值    
	 *  
	 * @return 推荐qq(String)
	 */
	public String getRefereeQq() {
		return refereeQq;
	}
	
	/**
	 * 
	 * 设置推荐qq值   
	 *  
	 * @param refereeQq 
	 *        推荐qq
	 */
	public void setRefereeQq(String refereeQq) {
		this.refereeQq = refereeQq;
	}
	/**
	 * 
	 * 获取价格P值    
	 *  
	 * @return 价格P(Integer)
	 */
	public Integer getPriceP() {
		return priceP;
	}
	
	/**
	 * 
	 * 设置价格P值   
	 *  
	 * @param priceP 
	 *        价格P
	 */
	public void setPriceP(Integer priceP) {
		this.priceP = priceP;
	}
	/**
	 * 
	 * 获取价格PP值    
	 *  
	 * @return 价格PP(Integer)
	 */
	public Integer getPricePp() {
		return pricePp;
	}
	
	/**
	 * 
	 * 设置价格PP值   
	 *  
	 * @param pricePp 
	 *        价格PP
	 */
	public void setPricePp(Integer pricePp) {
		this.pricePp = pricePp;
	}
	/**
	 * 
	 * 获取价格描述值    
	 *  
	 * @return 价格描述(String)
	 */
	public String getPriceDesc() {
		return priceDesc;
	}
	
	/**
	 * 
	 * 设置价格描述值   
	 *  
	 * @param priceDesc 
	 *        价格描述
	 */
	public void setPriceDesc(String priceDesc) {
		this.priceDesc = priceDesc;
	}
	/**
	 * 
	 * 获取描述值    
	 *  
	 * @return 描述(String)
	 */
	public String getSimpleDesc() {
		return simpleDesc;
	}
	
	/**
	 * 
	 * 设置描述值   
	 *  
	 * @param simpleDesc 
	 *        描述
	 */
	public void setSimpleDesc(String simpleDesc) {
		this.simpleDesc = simpleDesc;
	}
	/**
	 * 
	 * 获取特长值    
	 *  
	 * @return 特长(String)
	 */
	public String getSpecial() {
		return special;
	}
	
	/**
	 * 
	 * 设置特长值   
	 *  
	 * @param special 
	 *        特长
	 */
	public void setSpecial(String special) {
		this.special = special;
	}
	/**
	 * 
	 * 获取体验否值    
	 *  
	 * @return 体验否(Integer)
	 */
	public Integer getChecked() {
		return checked;
	}
	
	/**
	 * 
	 * 设置体验否值   
	 *  
	 * @param checked 
	 *        体验否
	 */
	public void setChecked(Integer checked) {
		this.checked = checked;
	}
	/**
	 * 
	 * 获取级别值    
	 *  
	 * @return 级别(Integer)
	 */
	public Integer getLeval() {
		return leval;
	}
	
	/**
	 * 
	 * 设置级别值   
	 *  
	 * @param leval 
	 *        级别
	 */
	public void setLeval(Integer leval) {
		this.leval = leval;
	}
	/**
	 * 
	 * 获取评价值    
	 *  
	 * @return 评价(String)
	 */
	public String getComment() {
		return comment;
	}
	
	/**
	 * 
	 * 设置评价值   
	 *  
	 * @param comment 
	 *        评价
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * 
	 * 获取图片值    
	 *  
	 * @return 图片(String)
	 */
	public String getImages() {
		return images;
	}
	
	/**
	 * 
	 * 设置图片值   
	 *  
	 * @param images 
	 *        图片
	 */
	public void setImages(String images) {
		this.images = images;
	}
	/**
	 * 
	 * 获取创建时间值    
	 *  
	 * @return 创建时间(Timestamp)
	 */
	public Timestamp getCreateDate() {
		return createDate;
	}
	
	/**
	 * 
	 * 设置创建时间值   
	 *  
	 * @param createDate 
	 *        创建时间
	 */
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	/**
	 * 
	 * 获取更新时间值    
	 *  
	 * @return 更新时间(Timestamp)
	 */
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	
	/**
	 * 
	 * 设置更新时间值   
	 *  
	 * @param updateDate 
	 *        更新时间
	 */
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	/**
	 * 
	 * 获取标记值    
	 *  
	 * @return 标记(Integer)
	 */
	public Integer getMark() {
		return mark;
	}
	
	/**
	 * 
	 * 设置标记值   
	 *  
	 * @param mark 
	 *        标记
	 */
	public void setMark(Integer mark) {
		this.mark = mark;
	}
}