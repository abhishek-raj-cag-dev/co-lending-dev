package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Unnati_Iexceed_Occp_Insr")
@Getter
@Setter
@ToString
public class RenewalLeadOccpInsDetails {

	@Id
	@JsonProperty("customerId")
	@Column(name = "Customer_id")
	private String customerId;

	@JsonProperty("pid")
	@Column(name = "PID")
	private String pid;

	@JsonProperty("age")
	@Column(name = "age")
	private Integer age;

	@JsonProperty("coApplicantInsurance")
	@Column(name = "Co_Applicant_insurance")
	private String coApplicantInsurance;

	@JsonProperty("insuranceReqd")
	@Column(name = "Insurance_reqd")
	private String insuranceReqd;

	@JsonProperty("insuredName")
	@Column(name = "Insured_name")
	private String insuredName;

	@JsonProperty("nomineeDob")
	@Column(name = "Nominee_Dob")
	private LocalDate nomineeDob;

	@JsonProperty("nomineeName")
	@Column(name = "Nominee_Name")
	private String nomineeName;

	@JsonProperty("nomineeRelation")
	@Column(name = "Nominee_relation")
	private String nomineeRelation;

	@JsonProperty("employer")
	@Column(name = "Employer")
	private String employer;

	@JsonProperty("occupationType")
	@Column(name = "occupation_type")
	private String occupationType;

	@JsonProperty("organisationName")
	@Column(name = "organisation_name")
	private String organisationName;

	@JsonProperty("streetVendor")
	@Column(name = "street_vendor")
	private String streetVendor;

	@JsonProperty("typeofbusiness")
	@Column(name = "Type_of_Buss")
	private String typeofbusiness;

	@JsonProperty("occupationTag")
	@Column(name = "Occupation_tag")
	private String occupationTag;

	@JsonProperty("employeeActivity")
	@Column(name = "employee_activity")
	private String employeeActivity;

	@JsonProperty("businessAddressProof")
	@Column(name = "Buss_add_proof")
	private String businessAddressProof;

	@JsonProperty("employmentProof")
	@Column(name = "employment_proof")
	private String employmentProof;

	@JsonProperty("businessPremiseOwnerShip")
	@Column(name = "Buss_prem_ownship")
	private String businessPremiseOwnerShip;

	@JsonProperty("freqOfIncome")
	@Column(name = "freq_of_income")
	private String freqOfIncome;

	@JsonProperty("annualIncome")
	@Column(name = "Annual_income")
	private String annualIncome;

	@JsonProperty("otherSourceIncome")
	@Column(name = "other_src_income")
	private String otherSourceIncome;

	@JsonProperty("otherSourceAnnualIncome")
	@Column(name = "other_src_ann_inc")
	private String otherSourceAnnualIncome;

	@JsonProperty("businessEmpStartDate")
	@Column(name = "Buss_emp_start_date")
	private String businessEmpStartDate;

	@JsonProperty("businessEmpVintageYear")
	@Column(name = "Buss_emp_vintage")
	private String businessEmpVintageYear;

	@JsonProperty("natureOfOccupation")
	@Column(name = "nature_of_occpn")
	private String natureOfOccupation;

	@JsonProperty("designation")
	@Column(name = "Designation")
	private String designation;

	@JsonProperty("employeeSince")
	@Column(name = "Emp_since")
	private String employeeSince;

	@JsonProperty("experience")
	@Column(name = "Experience")
	private String experience;

	@JsonProperty("retirementAge")
	@Column(name = "retirement_age")
	private Integer retirementAge;

	@JsonProperty("lastEmployer")
	@Column(name = "last_employer")
	private String lastEmployer;

	@JsonProperty("previousJobYears")
	@Column(name = "prev_job_years")
	private String previousJobYears;

	@JsonProperty("typeOfEmployer")
	@Column(name = "Type_of_employer")
	private String typeOfEmployer;

	@JsonProperty("addressLine1")
	@Column(name = "Com_add_line1")
	private String addressLine1;

	@JsonProperty("addressLine2")
	@Column(name = "Com_add_line2")
	private String addressLine2;

	@JsonProperty("addressLine3")
	@Column(name = "Com_add_line3")
	private String addressLine3;

	@JsonProperty("addressSameAs")
	@Column(name = "com_address_same_as")
	private String addressSameAs;

	@JsonProperty("addressType")
	@Column(name = "com_add_type")
	private String addressType;

	@JsonProperty("area")
	@Column(name = "Com_area")
	private String area;

	@JsonProperty("city")
	@Column(name = "Com_City")
	private String city;

	@JsonProperty("country")
	@Column(name = "Com_Country")
	private String country;

	@JsonProperty("district")
	@Column(name = "com_district")
	private String district;

	@JsonProperty("landMark")
	@Column(name = "com_landmark")
	private String landMark;

	@JsonProperty("pinCode")
	@Column(name = "Com_Pincode")
	private String pinCode;

	@JsonProperty("state")
	@Column(name = "Com_state")
	private String state;

	@JsonProperty("offAddressLine1")
	@Column(name = "Off_add_line1")
	private String offAddressLine1;

	@JsonProperty("offAddressLine2")
	@Column(name = "Off_add_line2")
	private String offAddressLine2;

	@JsonProperty("offAddressLine3")
	@Column(name = "Off_add_line3")
	private String offAddressLine3;

	@JsonProperty("offAddressSameAs")
	@Column(name = "Off_address_same_as")
	private String offAddressSameAs;

	@JsonProperty("offAddressType")
	@Column(name = "Off_add_type")
	private String offAddressType;

	@JsonProperty("offArea")
	@Column(name = "Off_area")
	private String offArea;

	@JsonProperty("offCity")
	@Column(name = "Off_City")
	private String offCity;

	@JsonProperty("offCountry")
	@Column(name = "Off_Country")
	private String offCountry;

	@JsonProperty("offDistrict")
	@Column(name = "Off_district")
	private String offDistrict;

	@JsonProperty("offLandMark")
	@Column(name = "Off_landmark")
	private String offLandMark;

	@JsonProperty("offPinCode")
	@Column(name = "Off_Pincode")
	private String offPinCode;

	@JsonProperty("offState")
	@Column(name = "Off_state")
	private String offState;

	@JsonProperty("dobAsperAddressProof")
	@Column(name = "Dob_as_per_add_proof")
	private LocalDate dobAsperAddressProof;

	@JsonProperty("nameAsperAddressProof")
	@Column(name = "name_as_per_add_proof")
	private String nameAsperAddressProof;

	@JsonProperty("residenceAddressSince")
	@Column(name = "residence_add_since")
	private String residenceAddressSince;

	@JsonProperty("residenceCitySince")
	@Column(name = "residence_city_since")
	private String residenceCitySince;

	@JsonProperty("residenceOwnership")
	@Column(name = "residence_ownership")
	private String residenceOwnership;

	@JsonProperty("coAge")
	@Column(name = "CO_age")
	private Integer coAge;

	@JsonProperty("coCoapplicantinsurance")
	@Column(name = "CO_Co_Applicant_insurance")
	private String coCoapplicantinsurance;

	@JsonProperty("coInsurancereqd")
	@Column(name = "CO_Insurance_reqd")
	private String coInsurancereqd;

	@JsonProperty("coInsuredname")
	@Column(name = "CO_Insured_name")
	private String coInsuredname;

	@JsonProperty("coNomineedob")
	@Column(name = "CO_Nominee_Dob")
	private LocalDate coNomineedob;

	@JsonProperty("coNomineename")
	@Column(name = "CO_Nominee_Name")
	private String coNomineename;

	@JsonProperty("coNomineerelation")
	@Column(name = "CO_Nominee_relation")
	private String coNomineerelation;

	@JsonProperty("coEmployer")
	@Column(name = "CO_Employer")
	private String coEmployer;

	@JsonProperty("coOccupationtype")
	@Column(name = "CO_occupation_type")
	private String coOccupationtype;

	@JsonProperty("coOrganisationname")
	@Column(name = "CO_organisation_name")
	private String coOrganisationname;

	@JsonProperty("coStreetvendor")
	@Column(name = "CO_street_vendor")
	private String coStreetvendor;

	@JsonProperty("coTypeofbusiness")
	@Column(name = "CO_Type_of_Buss")
	private String coTypeofbusiness;

	@JsonProperty("coOccupationtag")
	@Column(name = "CO_Occupation_tag")
	private String coOccupationtag;

	@JsonProperty("coEmployeeactivity")
	@Column(name = "CO_employee_activity")
	private String coEmployeeactivity;

	@JsonProperty("coBusinessaddressproof")
	@Column(name = "CO_Buss_add_proof")
	private String coBusinessaddressproof;

	@JsonProperty("coEmploymentproof")
	@Column(name = "CO_employment_proof")
	private String coEmploymentproof;

	@JsonProperty("coBusinesspremiseownership")
	@Column(name = "CO_Buss_prem_ownship")
	private String coBusinesspremiseownership;

	@JsonProperty("coFreqofincome")
	@Column(name = "CO_freq_of_income")
	private String coFreqofincome;

	@JsonProperty("coAnnualincome")
	@Column(name = "CO_Annual_income")
	private String coAnnualincome;

	@JsonProperty("coOthersourceincome")
	@Column(name = "CO_other_src_income")
	private String coOthersourceincome;

	@JsonProperty("coOthersourceannualincome")
	@Column(name = "CO_other_src_ann_inc")
	private String coOthersourceannualincome;

	@JsonProperty("coBusinessempstartdate")
	@Column(name = "CO_Buss_emp_start_date")
	private String coBusinessempstartdate;

	@JsonProperty("coBusinessempvintageyear")
	@Column(name = "CO_Buss_emp_vintage")
	private String coBusinessempvintageyear;

	@JsonProperty("coNatureofoccupation")
	@Column(name = "CO_nature_of_occpn")
	private String coNatureofoccupation;

	@JsonProperty("coDesignation")
	@Column(name = "CO_Designation")
	private String coDesignation;

	@JsonProperty("coEmployeesince")
	@Column(name = "CO_Emp_since")
	private String coEmployeesince;

	@JsonProperty("coExperience")
	@Column(name = "CO_Experience")
	private String coExperience;

	@JsonProperty("coRetirementage")
	@Column(name = "CO_retirement_age")
	private Integer coRetirementage;

	@JsonProperty("coLastemployer")
	@Column(name = "CO_last_employer")
	private String coLastemployer;

	@JsonProperty("coPreviousjobyears")
	@Column(name = "CO_prev_job_years")
	private String coPreviousjobyears;

	@JsonProperty("coTypeofemployer")
	@Column(name = "CO_Type_of_employer")
	private String coTypeofemployer;

	@JsonProperty("coAddressline1")
	@Column(name = "CO_Com_add_line1")
	private String coAddressline1;

	@JsonProperty("coAddressline2")
	@Column(name = "CO_Com_add_line2")
	private String coAddressline2;

	@JsonProperty("coAddressline3")
	@Column(name = "CO_Com_add_line3")
	private String coAddressline3;

	@JsonProperty("coAddresssameas")
	@Column(name = "CO_com_address_same_as")
	private String coAddresssameas;

	@JsonProperty("coAddresstype")
	@Column(name = "CO_com_add_type")
	private String coAddresstype;

	@JsonProperty("coArea")
	@Column(name = "CO_Com_area")
	private String coArea;

	@JsonProperty("coCity")
	@Column(name = "CO_Com_City")
	private String coCity;

	@JsonProperty("coCountry")
	@Column(name = "CO_Com_Country")
	private String coCountry;

	@JsonProperty("coDistrict")
	@Column(name = "CO_com_district")
	private String coDistrict;

	@JsonProperty("coLandmark")
	@Column(name = "CO_com_landmark")
	private String coLandmark;

	@JsonProperty("coPincode")
	@Column(name = "CO_Com_Pincode")
	private String coPincode;

	@JsonProperty("coState")
	@Column(name = "CO_Com_state")
	private String coState;

	@JsonProperty("coOffAddressline1")
	@Column(name = "CO_Off_add_line1")
	private String coOffAddressline1;

	@JsonProperty("coOffAddressline2")
	@Column(name = "CO_Off_add_line2")
	private String coOffAddressline2;

	@JsonProperty("coOffAddressline3")
	@Column(name = "CO_Off_add_line3")
	private String coOffAddressline3;

	@JsonProperty("coOffAddresssameas")
	@Column(name = "CO_Off_address_same_as")
	private String coOffAddresssameas;

	@JsonProperty("coOffAddresstype")
	@Column(name = "CO_Off_add_type")
	private String coOffAddresstype;

	@JsonProperty("coOffArea")
	@Column(name = "CO_Off_area")
	private String coOffArea;

	@JsonProperty("coOffCity")
	@Column(name = "CO_Off_City")
	private String coOffCity;

	@JsonProperty("coOffCountry")
	@Column(name = "CO_Off_Country")
	private String coOffCountry;

	@JsonProperty("coOffDistrict")
	@Column(name = "CO_Off_district")
	private String coOffDistrict;

	@JsonProperty("coOffLandmark")
	@Column(name = "CO_Off_landmark")
	private String coOffLandmark;

	@JsonProperty("coOffPincode")
	@Column(name = "CO_Off_Pincode")
	private String coOffPincode;

	@JsonProperty("coOffState")
	@Column(name = "CO_Off_state")
	private String coOffState;

	@JsonProperty("coDobasperaddressproof")
	@Column(name = "CO_Dob_as_per_add_proof")
	private LocalDate coDobasperaddressproof;

	@JsonProperty("coNameasperaddressproof")
	@Column(name = "CO_name_as_per_add_proof")
	private String coNameasperaddressproof;

	@JsonProperty("coResidenceaddresssince")
	@Column(name = "CO_residence_add_since")
	private String coResidenceaddresssince;

	@JsonProperty("coResidencecitysince")
	@Column(name = "CO_residence_city_since")
	private String coResidencecitysince;

	@JsonProperty("coResidenceownership")
	@Column(name = "CO_residence_ownership")
	private String coResidenceownership;

    @JsonProperty("coCommunicationAddressSameAs")
    @Column(name = "co_com_addrsameas")
    private String coCommunicationAddressSameAs;

}
