package com.sparrow.weixin.message.event;

import com.sparrow.weixin.message.Message;

public class Evt4LocationMessage extends Message {
	// Location_X
	private String latitude;
	// Location_Y
	private String longitude;

	private String precision;

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}
}
