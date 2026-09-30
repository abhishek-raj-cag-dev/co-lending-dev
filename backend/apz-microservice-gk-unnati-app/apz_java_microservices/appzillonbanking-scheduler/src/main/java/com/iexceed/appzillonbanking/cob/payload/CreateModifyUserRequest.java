package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class CreateModifyUserRequest {
	
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("requestObj")
	private CustomerDataFields requestObj;
	
	@Override
	public String toString() {
		return "CreateModifyUserRequest [interfaceName=" + interfaceName + ", userId=" + userId + ", appId=" + appId
				+ ", requestObj=" + requestObj + "]";
	}
}