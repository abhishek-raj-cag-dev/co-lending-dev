package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CheckApplicationRequest {
	
	@ApiModelProperty(required = true, position = 1, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 2, example = "Fetch")
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@ApiModelProperty(required = true, position = 3)
	@JsonProperty("requestObj")
	private CheckAppRequestFields requestObj;
}