package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BRECBCheckRequestExt {

	@Override
	public String toString() {
		return "BRECBCheckRequestExt [interfaceName=" + interfaceName + ", appId=" + appId + ", requestObj="
				+ requestObj + "]";
	}

	@ApiModelProperty(required = true, position = 1, example = "validateKyc")
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@ApiModelProperty(required = true, position = 2, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("requestObj")
	private Object requestObj;
}