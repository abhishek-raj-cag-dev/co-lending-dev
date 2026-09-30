package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WorkitemCreationRequestHighmarkDtls {

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerType")
	private String customerType;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerName")
	private String customerName;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("highmarkScore")
	private String highmarkScore;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("generatedOn")
	private String generatedOn;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("highmarkStatus")
	private String highmarkStatus;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("totalIndebitness")
	private String totalIndebitness;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("loanLimit")
	private String loanLimit;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("cbReport")
	private String cbReport;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("foir")
	private String foir;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("loanId")
	private String loanId;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("memberId")
	private String memberId;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("kycId")
	private String kycId;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("bureauName")
	private String bureauName;

	@ApiModelProperty(required = false, example = "")
	@JsonProperty("appIndebtednessLimit")
	private String appIndebtednessLimit;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("coappIndebtednessLimit")
	private String coappIndebtednessLimit;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("appMaxLoanLimit")
	private String appMaxLoanLimit;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("coappMaxLoanLimit")
	private String coappMaxLoanLimit;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("approvedAmount")
	private String approvedAmount;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("foirPercentage")
	private String foirPercentage;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("applicantIndebtedness")
	private String applicantIndebtedness;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("coApplicantIndebtedness")
	private String coApplicantIndebtedness;

}
