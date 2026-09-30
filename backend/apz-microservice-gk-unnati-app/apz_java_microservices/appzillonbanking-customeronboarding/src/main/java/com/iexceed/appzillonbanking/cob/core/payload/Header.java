package com.iexceed.appzillonbanking.cob.core.payload;

public class Header {
	
	private String userId;
	
	private String appId;
	
	private String interfaceId;
	
	private String deviceId;
	
	private String masterTxnRefNo;
	
	private String deviceType;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMasterTxnRefNo() {
		return masterTxnRefNo;
	}

	public void setMasterTxnRefNo(String masterTxnRefNo) {
		this.masterTxnRefNo = masterTxnRefNo;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public String toString() {
		return "Header [userId=" + userId + ", appId=" + appId + ", interfaceId=" + interfaceId + ", deviceId="
				+ deviceId + ", masterTxnRefNo=" + masterTxnRefNo + ", deviceType=" + deviceType + "]";
	}
}
