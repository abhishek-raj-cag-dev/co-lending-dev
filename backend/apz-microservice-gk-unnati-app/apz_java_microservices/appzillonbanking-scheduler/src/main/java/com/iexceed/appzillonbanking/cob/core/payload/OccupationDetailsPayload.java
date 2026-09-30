package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OccupationDetailsPayload {

	@JsonProperty("occupationType")
	private String occupationType;

	@JsonProperty("designation")
	private String designation;
	
	@JsonProperty("annualIncome")
	private BigDecimal annualIncome;
	
	@JsonProperty("organisationName")
	private String organisationName;
	
	@JsonProperty("officePhone")
	private String officePhone;
	
	@JsonProperty("officeEmail")
	private String officeEmail;
	
	@JsonProperty("employeeId")
	private String employeeId;

	@JsonProperty("employeeSince")
	private String employeeSince;
	
	@JsonProperty("experience")
	private String experience;
	
	@JsonProperty("employer")
	private String employer;
	
	@JsonProperty("grossIncome")
	private BigDecimal grossIncome;
	
	@JsonProperty("netTakeHome")
	private BigDecimal netTakeHome;
	
	@JsonProperty("retirementAge")
	private String retirementAge;
	
	@JsonProperty("lastEmployer")
	private String lastEmployer;
	
	@JsonProperty("previousJobYears")
	private String previousJobYears;
	
	@JsonProperty("typeOfEmployer")
	private String typeOfEmployer;
	
	@JsonProperty("natureOfOccupation")
	private String natureOfOccupation;
	
	@JsonProperty("addressProof")
	private String addressProof;
	
	@JsonProperty("businessAddressProof")
	private String businessAddressProof;
	
	@JsonProperty("employmentProof")
	private String employmentProof;
	
	@JsonProperty("employeeActivity")
	private String employeeActivity;
	
	@JsonProperty("businessPremiseOwnerShip")
	private String businessPremiseOwnerShip;
	
	@JsonProperty("freqOfIncome")
	private String freqOfIncome;
	
	@JsonProperty("otherSourceIncome")
	private String otherSourceIncome;
	
	@JsonProperty("otherSourceAnnualIncome")
	private String otherSourceAnnualIncome;
	
	@JsonProperty("streetVendor")
	private String streetVendor;
	
	@JsonProperty("modeOfIncome")
	private String modeOfIncome;
	
	@JsonProperty("typeofbusiness")
	private String typeofbusiness;
	
	@JsonProperty("businessEmpStartDate")
	private String businessEmpStartDate;
	
	@JsonProperty("businessEmpVintageYear")
	private String businessEmpVintageYear;
	
	@JsonProperty("occupationTag")
	private String occupationTag;
	
	@JsonProperty("rpcEditCheck")
	private String rpcEditCheck;

	@Override
	public String toString() {
		return "OccupationDetailsPayload [occupationType=" + occupationType + ", designation=" + designation
				+ ", annualIncome=" + annualIncome + ", organisationName=" + organisationName + ", officePhone="
				+ officePhone + ", officeEmail=" + officeEmail + ", employeeId=" + employeeId + ", employeeSince="
				+ employeeSince + ", experience=" + experience + ", employer=" + employer + ", grossIncome="
				+ grossIncome + ", netTakeHome=" + netTakeHome + ", retirementAge=" + retirementAge + ", lastEmployer="
				+ lastEmployer + ", previousJobYears=" + previousJobYears + ", typeOfEmployer=" + typeOfEmployer
				+ ", natureOfOccupation=" + natureOfOccupation + ", addressProof=" + addressProof
				+ ", businessAddressProof=" + businessAddressProof + ", employmentProof=" + employmentProof
				+ ", employeeActivity=" + employeeActivity + ", businessPremiseOwnerShip=" + businessPremiseOwnerShip
				+ ", freqOfIncome=" + freqOfIncome + ", otherSourceIncome=" + otherSourceIncome
				+ ", otherSourceAnnualIncome=" + otherSourceAnnualIncome + ", streetVendor=" + streetVendor
				+ ", modeOfIncome=" + modeOfIncome + ", typeofbusiness=" + typeofbusiness + ", businessEmpStartDate="
				+ businessEmpStartDate + ", businessEmpVintageYear=" + businessEmpVintageYear + ", occupationTag="
				+ occupationTag + ", rpcEditCheck=" + rpcEditCheck + "]";
	}
	
}
