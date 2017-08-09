package com.sparrow.app.information.domain.met;


import com.sparrow.orm.annotation.Table;
import com.sparrow.orm.annotation.Key;
import com.sparrow.orm.annotation.Column;

@Table(table = "gif_info" , desc = "")
public class GifInfo {
	@Key(column = "id", type = -5,  generator = "auto",   notnull = true,  length = 19, comment = "")
	private Long id;
	@Column(column = "alias", type = 12, length = 128, comment = "")
	private String alias;
	@Column(column = "icons", type = 12, length = 512, comment = "")
	private String icons;
	@Column(column = "gif_url", type = 12, length = 255, comment = "")
	private String gifUrl;
	@Column(column = "gif_desc", type = 12, length = 255, comment = "")
	private String gifDesc;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getIcons() {
		return icons;
	}

	public void setIcons(String icons) {
		this.icons = icons;
	}
	public String getGifUrl() {
		return gifUrl;
	}

	public void setGifUrl(String gifUrl) {
		this.gifUrl = gifUrl;
	}
	public String getGifDesc() {
		return gifDesc;
	}

	public void setGifDesc(String gifDesc) {
		this.gifDesc = gifDesc;
	}
}