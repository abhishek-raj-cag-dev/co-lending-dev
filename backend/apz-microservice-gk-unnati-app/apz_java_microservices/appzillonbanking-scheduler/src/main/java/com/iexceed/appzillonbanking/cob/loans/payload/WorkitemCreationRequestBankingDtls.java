package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter@ToString
public class WorkitemCreationRequestBankingDtls {
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("typeOfAcc")
	private String typeOfAcc;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("nameAsPerBankAcc")
	private String nameAsPerBankAcc;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("ifscCode")
	private String ifscCode;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("bankName")
	private String bankName;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("branchName")
	private String branchName;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("pincode")
	private String pincode;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("accNo")
	private String accNo;

}
