package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyLoanRequest {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("interfaceName")
	private String interfaceName;

	@JsonProperty("userId")
	private String userId;

	@JsonProperty("requestObj")
	private ApplyLoanRequestFields requestObj;

	@Override
	public String toString() {
		return "ApplyLoanRequest [appId=" + appId + ", interfaceName=" + interfaceName + ", userId=" + userId
				+ ", requestObj=" + requestObj + "]";
	}

}
