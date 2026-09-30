package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WorkitemCreationRequestCustDtls {

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("passportVfyFlag")
	private String passportVfyFlag;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("landline")
	private String landline;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("otpStatus")
	private String otpStatus;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerType")
	private String customerType;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerName")
	private String customerName;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("customerId")
	private String customerId;

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

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("fullName")
	private String fullName;

	@ApiModelProperty(required = true, example = "Male")
	@JsonProperty("gender")
	private String gender;

	@ApiModelProperty(required = true, example = "dd/mm/yyyy")
	@JsonProperty("dob")
	private String dob;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("age")
	private String age;

	@ApiModelProperty(required = true, example = "9999999999")
	@JsonProperty("mobileNo")
	private String mobileNo;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("maritalStatus")
	private String maritalStatus;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("fathersName")
	private String fathersName;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("spouseName")
	private String spouseName;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("mothersName")
	private String mothersName;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("applicantRel")
	private String applicantRel;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("emailId")
	private String emailId;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("religion")
	private String religion;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("caste")
	private String caste;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("education")
	private String education;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("primaryKycId")
	private String primaryKycId;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("alternateKycId")
	private String alternateKycId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("secondaryKycType")
	private String secondaryKycType;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("secondaryKycId")
	private String secondaryKycId;

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("occupation")
	private String occupation;
	
	@ApiModelProperty(required = false, example = "ckyc")
	@JsonProperty("ckyc")
	private String ckyc;

	@Override
	public String toString() {
		return "WorkitemCreationRequestCustDtls [passportVfyFlag=" + passportVfyFlag + ", landline=" + landline
				+ ", otpStatus=" + otpStatus + ", customerType=" + customerType + ", customerName=" + customerName
				+ ", customerId=" + customerId + ", title=" + title + ", firstName=" + firstName + ", middleName="
				+ middleName + ", lastName=" + lastName + ", fullName=" + fullName + ", gender=" + gender + ", dob="
				+ dob + ", age=" + age + ", mobileNo=" + mobileNo + ", maritalStatus=" + maritalStatus
				+ ", fathersName=" + fathersName + ", spouseName=" + spouseName + ", mothersName=" + mothersName
				+ ", applicantRel=" + applicantRel + ", emailId=" + emailId + ", religion=" + religion + ", caste="
				+ caste + ", education=" + education + ", primaryKycId=" + primaryKycId + ", alternateKycId="
				+ alternateKycId + ", secondaryKycType=" + secondaryKycType + ", secondaryKycId=" + secondaryKycId
				+ ", occupation=" + occupation + ", ckyc=" + ckyc + "]";
	}

}
