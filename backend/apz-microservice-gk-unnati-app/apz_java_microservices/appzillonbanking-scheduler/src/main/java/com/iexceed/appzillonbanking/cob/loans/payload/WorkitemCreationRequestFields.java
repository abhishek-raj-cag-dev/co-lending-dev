package com.iexceed.appzillonbanking.cob.loans.payload;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WorkitemCreationRequestFields {	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerId")
	private String customerId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("noOfYrsRel")
	private String noOfYrsRel;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("relApplication")
	private String relApplication;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("kmName")
	private String kmName;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("title")
	private String title;

	@ApiModelProperty(required = true, example = "First Name")
	@JsonProperty("firstName")
	private String firstName;

	@ApiModelProperty(required = false, example = "Middle Name")
	@JsonProperty("middleName")
	private String middleName;

	@ApiModelProperty(required = false, example = "Last Name")
	@JsonProperty("lastName")
	private String lastName;

	@ApiModelProperty(required = true, example = "Male")
	@JsonProperty("gender")
	private String gender;

	@ApiModelProperty(required = true, example = "dd/mm/yyyy")
	@JsonProperty("dob")
	private String dob;

	@ApiModelProperty(required = true, example = "9999999999")
	@JsonProperty("mobileNo")
	private String mobileNo;

	@ApiModelProperty(required = true, example = "9999999999")
	@JsonProperty("natureOfBussEmp")
	private String natureOfBussEmp;

	@ApiModelProperty(required = true, example = "9999999999")
	@JsonProperty("bussEmpActivity")
	private String bussEmpActivity;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("kendraId")
	private String kendraId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("kendraName")
	private String kendraName;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("kmId")
	private String kmId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("branchName")
	private String branchName;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("custBranchName")
	private String custBranchName;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("branchId")
	private String branchId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("area")
	private String area;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("region")
	private String region;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("groupId")
	private String groupId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("kendraSize")
	private String kendraSize;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("kendraVintageYrs")
	private String kendraVintageYrs;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("groupSize")
	private String groupSize;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("kendraParStatus")
	private String kendraParStatus;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("kendraMeetingFreq")
	private String kendraMeetingFreq;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("kendraMeetingDay")
	private String kendraMeetingDay;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("requestedLoanAmount")
	private String requestedLoanAmount;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("loanTenureInMonths")
	private String loanTenureInMonths;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("purposeOfLoan")
	private String purposeOfLoan;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("subPurposeOfLoan")
	private String subPurposeOfLoan;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("rateOfInterest")
	private String rateOfInterest;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("langForComm")
	private String langForComm;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("modeOfDisbursement")
	private String modeOfDisbursement;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("repaymentFrequency")
	private String repaymentFrequency;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("custDtls")
	private List<WorkitemCreationRequestCustDtls> custDtls;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("coApplicantInsuranceReq")
	private String coApplicantInsuranceReq;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("addressDtls")
	private List<WorkitemCreationRequestAddrssDtls> addressDtls;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("bankingDtl")
	private WorkitemCreationRequestBankingDtls bankingDtl;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("insuranceDtls")
	private List<WorkitemCreationRequestInsuranceDtls> insuranceDtls;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("occupationDtls")
	private List<WorkitemCreationRequestOccupationDtls> occupationDtls;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("highmarkDtls")
	private List<WorkitemCreationRequestHighmarkDtls> highmarkDtls;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("docDtls")
	private List<WorkitemCreationRequestDocDtls> docDtls;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("txnId")
	private String txnId;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("applnRefNo")
	private String applnRefNo;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("pid")
	private String pid;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("activationDate")
	private LocalDate activationDate;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("approvedAmount")
	private String approvedAmount;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("foirPercentage")
	private String foirPercentage;
}
