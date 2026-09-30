package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class FetchAppRequest {

	@JsonProperty("requestObj")
	private FetchAppRequestFields requestObj;

	@JsonProperty("appId")
	private String appId;

	@Override
	public String toString() {
		return "FetchAppRequest [requestObj=" + requestObj + ", appId=" + appId + "]";
	}
	
}
