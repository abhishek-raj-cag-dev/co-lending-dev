package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LucUploadLoanRequest {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("interfaceName")
	private String interfaceName;

	@JsonProperty("userId")
	private String userId;

	@JsonProperty("requestObj")
	private LucUploadLoanRequestFields requestObj;

	@Override
	public String toString() {
		return "LucUploadLoanRequest [appId=" + appId + ", interfaceName=" + interfaceName + ", userId=" + userId
				+ ", requestObj=" + requestObj + "]";
	}

}
