package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class FetchAppReq {

	@JsonProperty("requestObj")
	private FetchAppReqFields requestObj;

	@JsonProperty("appId")
	private String appId;
}