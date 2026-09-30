package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HighMarkCheckRequestFields {

	@ApiModelProperty(required = true, example = "First Name")
	@JsonProperty("firstName")
	private String firstName;
	
	@ApiModelProperty(required = false, example = "Middle Name")
	@JsonProperty("middleName")
	private String middleName;
	
	@ApiModelProperty(required = false, example = "Last Name")
	@JsonProperty("lastName")
	private String lastName;
	
	@ApiModelProperty(required = true, example = "dd/mm/yyyy")
	@JsonProperty("dob")
	private String dob;
	
	@ApiModelProperty(required = true, example = "40")
	@JsonProperty("age")
	private String age;
	
	@ApiModelProperty(required = true, example = "XCX2872851")
	@JsonProperty("primaryKycId")
	private String primaryKycId;
	
	@ApiModelProperty(required = false, example = "voter")
	@JsonProperty("secondaryKycType")
	private String secondaryKycType;
	
	@ApiModelProperty(required = false, example = "XCX2872851")
	@JsonProperty("secondaryKycId")
	private String secondaryKycId;
	
	@ApiModelProperty(required = true, example = "Fathers Name")
	@JsonProperty("fathersName")
	private String fathersName;
	
	@ApiModelProperty(required = true, example = "Mothers Name")
	@JsonProperty("mothersName")
	private String mothersName;
	
	@ApiModelProperty(required = false, example = "Spouse Name")
	@JsonProperty("spouseName")
	private String spouseName;
	
	@ApiModelProperty(required = true, example = "9999999999")
	@JsonProperty("mobileNo")
	private String mobileNo;
	
	@ApiModelProperty(required = true, example = "Male")
	@JsonProperty("gender")
	private String gender;
	
	@ApiModelProperty(required = true, example = "Married")
	@JsonProperty("maritalStatus")
	private String maritalStatus;
	
	@JsonProperty("addressDetails")
	private List<HighMarkAddressDetails> addressDetails;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("loanPurpose")
	private String loanPurpose;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("loanAmount")
	private String loanAmount;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("branch")
	private String branch;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("kendra")
	private String kendra;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("ifsc")
	private String ifsc;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("branchState")
	private String branchState;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerType")
	private String customerType;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerId")
	private String customerId;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("cbLoanId")
	private String cbLoanId;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("applicationId")
	private String applicationId;
}
