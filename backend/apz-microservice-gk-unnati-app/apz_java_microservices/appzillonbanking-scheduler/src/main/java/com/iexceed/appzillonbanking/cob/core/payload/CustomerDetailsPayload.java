package com.iexceed.appzillonbanking.cob.core.payload;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerDetailsPayload {

	@JsonProperty("title")
	private String title;

	@JsonProperty("dob")
	private String dob;

	@JsonProperty("age")
	private String age;

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("maritalStatus")
	private String maritalStatus;

	@JsonProperty("altMobileNumber")
	private String altMobileNumber;

	@JsonProperty("emailId")
	private String emailId;

	@JsonProperty("pan")
	private String pan;

	@JsonProperty("spouseName")
	private String spouseName;

	@JsonProperty("customerType")
	private String customerType;

	@JsonProperty("fathersName")
	private String fathersName;

	@JsonProperty("aadhaarNumber")
	private String aadhaarNumber;

	@JsonProperty("occupation")
	private String occupation;

	@JsonProperty("custId")
	private String custId;

	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("middleName")
	private String middleName;

	@JsonProperty("lastName")
	private String lastName;

	@JsonProperty("relationShipWithApplicant")
	private String relationShipWithApplicant;

	@JsonProperty("namePerKyc")
	private String namePerKyc;

	@JsonProperty("landLineNumber")
	private String landLineNumber;

	@JsonProperty("religion")
	private String religion;

	@JsonProperty("caste")
	private String caste;

	@JsonProperty("primaryKycType")
	private String primaryKycType;

	@JsonProperty("primaryKycId")
	private String primaryKycId;

	@JsonProperty("secondaryKycType")
	private String secondaryKycType;

	@JsonProperty("secondaryKycId")
	private String secondaryKycId;

	@JsonProperty("gkCustomerType")
	private String gkCustomerType;

	@JsonProperty("education")
	private String education;

	@JsonProperty("secMobileNo")
	private String secMobileNo;

	@JsonProperty("alternateVoterId")
	private String alternateVoterId;

	@JsonProperty("primaryKycIdValStatus")
	private String primaryKycIdValStatus;

	@JsonProperty("secondaryKycIdValStatus")
	private String secondaryKycIdValStatus;

	@JsonProperty("alternateVoterIdValStatus")
	private String alternateVoterIdValStatus;

	@JsonProperty("createdByName")
	private String createdByName;

	@JsonProperty("ckyc")
	private String ckyc;

	@JsonProperty("customerIndex")
	private int customerIndex;

	@JsonProperty("reason")
	private String reason;

	@JsonProperty("remarks")
	private String remarks;

	@JsonProperty("appVersion")
	private String appVersion;

	@JsonProperty("rpcEditFlag")
	private boolean rpcEditFlag;

	@JsonProperty("kycVerifiedBy")
	private String kycVerifiedBy;

	@JsonProperty("annualIncome")
	private String annualIncome;

	@CreationTimestamp
	@JsonProperty("createTs")
	private LocalDateTime createTs;

	@UpdateTimestamp
	@JsonProperty("updateTs")
	private LocalDateTime updateTs;

	@JsonProperty("secondaryKycName")
	private String secondaryKycName;
	
	@JsonProperty("secondaryKycDob")
	private String secondaryKycDob;

	@JsonProperty("panNumber")
	private String panNumber;
	
	@JsonProperty("panNumberStatus")
	private String panNumberStatus;

    @JsonProperty("isDisabled")
    private String isDisabled;

    @JsonProperty("disabilityList")
    private List<String> disabilityList;

    @JsonProperty("UDID")
    private String UDID;

    @JsonProperty("disabilityPercentage")
    private String disabilityPercentage;

	@JsonProperty("isNewCustomer")
	private String isNewCustomer;

	@JsonProperty("disabliltyOtherValue")
	private String disabliltyOtherValue;


	public void setPrimaryKycId(String primaryKycId){
		this.primaryKycId = (null == primaryKycId) ? "" : primaryKycId.trim().toUpperCase();
	}

	public void setAlternateVoterId(String alternateVoterId){
		this.alternateVoterId = (null == alternateVoterId) ? "" : alternateVoterId.trim().toUpperCase();
	}
}
