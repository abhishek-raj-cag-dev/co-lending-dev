package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class WorkitemCreationRequest {
	
	@ApiModelProperty(required = true, position = 1, example = "validateKyc")
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@ApiModelProperty(required = true, position = 2, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("requestObj")
	private WorkitemCreationRequestFields requestObj;

	@Override
	public String toString() {
		return "WorkitemCreationRequest [interfaceName=" + interfaceName + ", appId=" + appId + ", requestObj="
				+ requestObj + "]";
	}

}
