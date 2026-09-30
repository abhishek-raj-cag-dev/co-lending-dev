package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendSmsAndEmailRequestObject {

	@ApiModelProperty(required = true, example = "SEND_OTP")
	@JsonProperty("actionType")
	private String actionType;
	
	@ApiModelProperty(required = true, example = "EN")
	@JsonProperty("language")
	private String language;
	
	@ApiModelProperty(required = false, example = "7777777777")
	@JsonProperty("mobileNo")
	private String mobileNo;
	
	@ApiModelProperty(required = false, example = "test@gmail.com")
	@JsonProperty("emailId")
	private String emailId;
	
	@ApiModelProperty(required = false, example = "true")
	@JsonProperty("attachmentReq")
	private boolean attachmentReq;
	
	@ApiModelProperty(required = false, example = "PDF")
	@JsonProperty("attachmentType")
	private String attachmentType;
	
	@ApiModelProperty(required = false, example = "base64 content")
	@JsonProperty("attachmentContent")
	private String attachmentContent;
	
	@ApiModelProperty(required = false, example = "Name")
	@JsonProperty("custName")
	private String custName;
	
	@ApiModelProperty(required = false, example = "1234")
	@JsonProperty("otp")
	private String otp;
	
	@ApiModelProperty(required = false, example = "10")
	@JsonProperty("validTill")
	private String validTill;
}
