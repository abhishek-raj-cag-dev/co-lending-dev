package com.iexceed.appzillonbanking.cob.nesl.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnachRequest {

	@Override
	public String toString() {
		return "EnachRequest [interfaceName=" + interfaceName + ", appId=" + appId + ", requestObj="
				+ requestObj + "]";
	}

	@ApiModelProperty(required = true, position = 1, example = "RppService")
	@JsonProperty("interfaceName")
	private String interfaceName;

	@ApiModelProperty(required = true, position = 2, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 3, example = "1234")
	@JsonProperty("userId")
	private String userId;

	@JsonProperty("requestObj")
	private EnachRequestFields requestObj;

}
