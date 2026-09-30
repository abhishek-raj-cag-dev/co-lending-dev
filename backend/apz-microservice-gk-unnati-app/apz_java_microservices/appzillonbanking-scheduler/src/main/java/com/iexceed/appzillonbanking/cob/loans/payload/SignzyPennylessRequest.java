package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignzyPennylessRequest {

	@Override
	public String toString() {
		return "SignzyPennylessRequest [interfaceName=" + interfaceName + ", appId=" + appId + ", requestObj="
				+ requestObj + "]";
	}

	@ApiModelProperty(required = true, position = 1, example = "SignzyPennylessCheck")
	@JsonProperty("interfaceName")
	private String interfaceName;

	@ApiModelProperty(required = true, position = 2, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, example = "NEW00063")
	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("requestObj")
	private SignzyPennylessRequestFields requestObj;

}
