package com.sparrow.app.information.domain.school;


import com.sparrow.orm.annotation.Table;
import com.sparrow.orm.annotation.Key;
import com.sparrow.orm.annotation.Column;

@Table(table = "primary_school" , desc = "")
public class PrimarySchool {
	@Key(column = "id", type = -5,  generator = "auto",   notnull = true,  length = 19, comment = "ID")
	private Long id;
	@Column(column = "open_id", type = 12, length = 36, comment = "唯一号")
	private String openId;
	@Column(column = "name", type = 12, length = 36, comment = "姓名")
	private String name;
	@Column(column = "phone", type = 12, length = 32, comment = "电话")
	private String phone;
	@Column(column = "nick_name", type = 12, length = 36, comment = "昵称")
	private String nickName;
	@Column(column = "sex", type = 4, length = 10, comment = "性别")
	private Integer sex;
	@Column(column = "language", type = 12, length = 8, comment = "语言")
	private String language;
	@Column(column = "city", type = 12, length = 36, comment = "城市")
	private String city;
	@Column(column = "province", type = 12, length = 36, comment = "省份")
	private String province;
	@Column(column = "country", type = 12, length = 36, comment = "国家")
	private String country;
	@Column(column = "head_image", type = 12, length = 256, comment = "头像")
	private String headImage;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}
}