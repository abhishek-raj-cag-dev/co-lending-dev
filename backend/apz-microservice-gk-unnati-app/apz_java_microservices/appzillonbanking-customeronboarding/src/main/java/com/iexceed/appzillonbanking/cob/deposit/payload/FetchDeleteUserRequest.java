package com.iexceed.appzillonbanking.cob.deposit.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class FetchDeleteUserRequest {

	@JsonProperty("requestObj")
	private FetchDeleteUserFields requestObj;

	@JsonProperty("appId")
	private String appId;
}