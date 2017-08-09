package com.sparrow.app.information.domain.met;

import java.util.Date;

import com.sparrow.orm.annotation.Table;
import com.sparrow.orm.annotation.Key;
import com.sparrow.orm.annotation.Column;

@Table(table = "lf_members" , desc = "")
public class LfMembers {
	@Key(column = "id", type = -5,  generator = "auto",   notnull = true,  length = 20, comment = "ID")
	private Long id;
	@Column(column = "name", type = 12, length = 32, comment = "昵称")
	private String name;
	@Column(column = "qq", type = 12, length = 32, comment = "QQ")
	private String qq;
	@Column(column = "sex", type = 1, length = 1, comment = "性别")
	private String sex;
	@Column(column = "age", type = 4, length = 10, comment = "年龄")
	private Integer age;
	@Column(column = "bra", type = 1, length = 1, comment = "罩B")
	private String bra;
	@Column(column = "phone", type = 12, length = 32, comment = "手机")
	private String phone;
	@Column(column = "province", type = 12, length = 32, comment = "省")
	private String province;
	@Column(column = "city", type = 12, length = 32, comment = "市")
	private String city;
	@Column(column = "district", type = 12, length = 32, comment = "区域")
	private String district;
	@Column(column = "referee_name", type = 12, length = 32, comment = "推荐人")
	private String refereeName;
	@Column(column = "referee_qq", type = 12, length = 32, comment = "推荐qq")
	private String refereeQq;
	@Column(column = "price_p", type = 4, length = 10, comment = "价格P")
	private Integer priceP;
	@Column(column = "price_pp", type = 4, length = 10, comment = "价格PP")
	private Integer pricePp;
	@Column(column = "price_desc", type = 12, length = 64, comment = "价格描述")
	private String priceDesc;
	@Column(column = "simple_desc", type = 12, length = 128, comment = "描述")
	private String simpleDesc;
	@Column(column = "special", type = 12, length = 64, comment = "特长")
	private String special;
	@Column(column = "checked", type = 4, length = 10, comment = "体验否")
	private Integer checked;
	@Column(column = "leval", type = 4, length = 10, comment = "级别")
	private Integer leval;
	@Column(column = "comment", type = 12, length = 256, comment = "评价")
	private String comment;
	@Column(column = "images", type = 12, length = 512, comment = "图片")
	private String images;
	@Column(column = "create_date", type = 93, notnull = true,length = 19, comment = "创建时间")
	private Date createDate;
	@Column(column = "update_date", type = 93, length = 19, comment = "更新时间")
	private Date updateDate;
	@Column(column = "mark", type = 4, length = 10, comment = "标记")
	private Integer mark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	public String getBra() {
		return bra;
	}

	public void setBra(String bra) {
		this.bra = bra;
	}
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}
	public String getRefereeName() {
		return refereeName;
	}

	public void setRefereeName(String refereeName) {
		this.refereeName = refereeName;
	}
	public String getRefereeQq() {
		return refereeQq;
	}

	public void setRefereeQq(String refereeQq) {
		this.refereeQq = refereeQq;
	}
	public Integer getPriceP() {
		return priceP;
	}

	public void setPriceP(Integer priceP) {
		this.priceP = priceP;
	}
	public Integer getPricePp() {
		return pricePp;
	}

	public void setPricePp(Integer pricePp) {
		this.pricePp = pricePp;
	}
	public String getPriceDesc() {
		return priceDesc;
	}

	public void setPriceDesc(String priceDesc) {
		this.priceDesc = priceDesc;
	}
	public String getSimpleDesc() {
		return simpleDesc;
	}

	public void setSimpleDesc(String simpleDesc) {
		this.simpleDesc = simpleDesc;
	}
	public String getSpecial() {
		return special;
	}

	public void setSpecial(String special) {
		this.special = special;
	}
	public Integer getChecked() {
		return checked;
	}

	public void setChecked(Integer checked) {
		this.checked = checked;
	}
	public Integer getLeval() {
		return leval;
	}

	public void setLeval(Integer leval) {
		this.leval = leval;
	}
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}
}