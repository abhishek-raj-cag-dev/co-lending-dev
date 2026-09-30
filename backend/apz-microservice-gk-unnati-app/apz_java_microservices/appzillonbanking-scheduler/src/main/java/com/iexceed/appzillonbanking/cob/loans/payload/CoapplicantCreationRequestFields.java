package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoapplicantCreationRequestFields {

	@JsonProperty("customerOccupation")
	private String customerOccupation = "";

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("dateOfBirth")
	private Long dateOfBirth;

	@ApiModelProperty(required = false, position = 2, example = "male")
	@JsonProperty("gender")
	private String gender = "";

	@ApiModelProperty(required = false, position = 2, example = "F")
	@JsonProperty("maritalstatus")
	private String maritalstatus = "";

	@ApiModelProperty(required = false, position = 2, example = "00000000001")
	@JsonProperty("phoneNumber")
	private List<PhoneNumberRequestField> phoneNumber = new ArrayList<>();

	@ApiModelProperty(required = false, position = 2, example = "STNE24242")
	@JsonProperty("voterId")
	private String voterId = "";

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("customerFirstName")
	private List<CustomerFirstNameRequestField> customerFirstName = new ArrayList<>();

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("customerShortName")
	private List<CustomerShortNameRequestField> customerShortName = new ArrayList<>();

	@JsonProperty("customerAddress")
	private List<CustomerAddressRequestField> customerAddress = new ArrayList<>();

	@JsonProperty("coApplicantDetails")
	private List<CoApplicantDetailRequestField> coApplicantDetails = new ArrayList<>();

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("title")
	private String title = "";

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("familyName")
	private String familyName = "";

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("rationCardNumber")
	private String rationCardNumber= "";

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("panIdNumber")
	private String panIdNumber = "";

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("dlNumber")
	private String dlNumber = "";

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("passport")
	private String passport = "";

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("otherGovernmentId")
	private String otherGovernmentId = "";

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("nameOfBSN")
	private String nameOfBSN = "";

	@ApiModelProperty(required = false, position = 2, example = "20000101")
	@JsonProperty("employeeNumber")
	private String employeeNumber = "";

	@JsonProperty("beneficiaryBankAccountNum")
	private String beneficiaryBankAccountNum = "";

	@JsonProperty("beneficirayIfscCode")
	private String beneficirayIfscCode = "";

	@JsonProperty("beneficiaryBankName")
	private String beneficiaryBankName = "";

	@JsonProperty("beneficiaryBranchkName")
	private String beneficiaryBranchkName = "";
	
	//temp header values 
	@JsonProperty("customerIdTemp")
	private String customerIdTemp;
	
	//temp header values 
	@JsonProperty("companyIdTemp")
	private String companyIdTemp;
	
	@JsonProperty("isUpdateCall")
	private String isUpdateCall;
	
	@JsonProperty("lastName")
	private String lastName = "";

	@JsonProperty("prospectCustomerId")
	private String prospectCustomerId = "";
	
}
