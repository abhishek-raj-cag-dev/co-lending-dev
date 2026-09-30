package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter @Setter
@ToString
public class FetchRoleRequest {
	
	@JsonProperty("requestObj")
	private FetchRoleRequestFields requestObj;

	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("appId")
	private String appId;
}
