package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FundAccountRequestFields {
	
	@ApiModelProperty(required = true, example = "25000")
	@JsonProperty("fundingAmount")
	private String fundingAmount;	
	
	@ApiModelProperty(required = true, example = "Net Banking")
	@JsonProperty("fundingMode")
	private String fundingMode;	
	
	@ApiModelProperty(required = true, example = "HDFC")
	@JsonProperty("bankName")
	private String bankName;	
	
	@ApiModelProperty(required = true, example = "upi@domain")
	@JsonProperty("upiId")
	private String upiId;
	
	@ApiModelProperty(required = true, example = "10000004")
	@JsonProperty("customerId")
	private String customerId;

	@ApiModelProperty(required = true, example = "21654526")
	@JsonProperty("accountNo")
	private String accountNo;
	
	@ApiModelProperty(required = true, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, example = "16185889475433589")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, example = "Y")
	@JsonProperty("autoPayEnabled")
	private String autoPayEnabled;
	
	@ApiModelProperty(required = true, example = "5")
	@JsonProperty("autoPayDate")
	private String autoPayDate;
	
	@ApiModelProperty(required = true, example = "5374346534")
	@JsonProperty("autopaySrcAccount")
	private String autopaySrcAccount;
	
	@ApiModelProperty(required = true, example = "SB")
	@JsonProperty("autopaySrcAccountType")
	private String autopaySrcAccountType;
	
	@ApiModelProperty(required = true, example = "5374346534")
	@JsonProperty("payoutAccount")
	private String payoutAccount;
	
	@ApiModelProperty(required = true, example = "SB")
	@JsonProperty("payoutAccountType")
	private String payoutAccountType;
	
	@ApiModelProperty(required = true, example = "5374346534")
	@JsonProperty("initialFundAccount")
	private String initialFundAccount;
	
	@ApiModelProperty(required = true, example = "SB")
	@JsonProperty("initialFundAccountType")
	private String initialFundAccountType;
}