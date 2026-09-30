package com.iexceed.appzillonbanking.cob.deposit.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.FundAccountRequestFields;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FundAccountRequest {
	
	@ApiModelProperty(required = true, position = 1, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 2, example = "FetchNominee")
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@ApiModelProperty(required = true, position = 3)
	@JsonProperty("requestObj")
	private FundAccountRequestFields requestObj;

}