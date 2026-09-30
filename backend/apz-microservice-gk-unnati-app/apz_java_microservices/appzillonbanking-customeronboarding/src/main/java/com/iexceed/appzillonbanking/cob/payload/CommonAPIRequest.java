package com.iexceed.appzillonbanking.cob.payload;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommonAPIRequest {

	@JsonProperty("requestObj")
	private LockInOutRequestFileds requestObj;

	@JsonProperty("interfaceName")
	private String interfaceName;

	@JsonProperty("appId")
	private String appId;
}
