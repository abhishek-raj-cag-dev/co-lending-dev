package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class FetchBranchesRequest {
	
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("requestObj")
	private FetchBranchesRequestFields requestObj;
}