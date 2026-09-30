package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SmsRequestFields {

	@ApiModelProperty(required = true, position = 1, example = "876876876868768")
	@JsonProperty("transId")
	private String transId;
	
	@ApiModelProperty(required = true, example = "7777777777")
	@JsonProperty("msisdn")
	private String mobileNo;
	
	@ApiModelProperty(required = true, example = "sms content")
	@JsonProperty("msg")
	private String msg;
	
	@ApiModelProperty(required = true, example = "GKMFSI")
	@JsonProperty("senderId")
	private String senderId;
	
	@ApiModelProperty(required = true, example = "kmuser1")
	@JsonProperty("customerid")
	private String customerId;
	
	@ApiModelProperty(required = true, example = "kmuser1")
	@JsonProperty("customerName")
	private String customerName;
	
	@ApiModelProperty(required = true, example = "otp")
	@JsonProperty("actiontypes")
	private String actionTypes;
}