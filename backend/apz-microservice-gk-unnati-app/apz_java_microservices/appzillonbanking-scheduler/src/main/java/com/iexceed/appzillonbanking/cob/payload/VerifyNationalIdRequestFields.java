package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class VerifyNationalIdRequestFields {
	
	@ApiModelProperty(required = true, position = 1, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;
	
	@ApiModelProperty(required = true, position = 2, example = "16185889475433589")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 3, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 4, example = "PAN")
	@JsonProperty("nationalIdName")
	private String nationalIdName;
	
	@ApiModelProperty(required = true, position = 5, example = "BGSKJ8876H")
	@JsonProperty("nationalIdValue")
	private String nationalIdValue;
}