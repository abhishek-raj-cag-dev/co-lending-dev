package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ExtractOcrDataRequest {
	
	@ApiModelProperty(required = true, position = 1, example = "extractocrdata")
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("requestObj")
	private ExtractOcrDataRequesttFields requestObj;
	
	@JsonProperty("appId")
	private String appId;
}