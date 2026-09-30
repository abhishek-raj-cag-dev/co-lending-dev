package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendSmsAndEmailApiRequest {

	@ApiModelProperty(required = true, position = 1, example = "sendSms")
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@ApiModelProperty(required = true, position = 2, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = false, position = 3, example = "APZCOB")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 2, example = "kmuser1")
	@JsonProperty("userId")
	private String userId;
	
	@ApiModelProperty(required = true, position = 2, example = "kmuser1")
	@JsonProperty("userName")
	private String userName;
	
	@JsonProperty("requestObj")
	private SendSmsAndEmailRequestObject requestObject;
}
