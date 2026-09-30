package com.iexceed.appzillonbanking.cob.domain.cdh;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "unnati_iexceed_occp_insr_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnnatiIexceedOccpInsr {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unique_id")
	private Long uniqueId;

	@Column(name = "reference_id")
	private String referenceId;

	@Column(name = "application_id")
	private String applicationId;

	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "age")
	private Integer age;

	@Column(name = "co_applicant_insurance")
	private String coApplicantInsurance;

	@Column(name = "insurance_reqd")
	private String insuranceReqd;

	@Column(name = "insured_name")
	private String insuredName;

	@Formula("NULLIF(nominee_dob, '0000-00-00')")
	private LocalDate nomineeDob;

	@Column(name = "nominee_name")
	private String nomineeName;

	@Column(name = "nominee_relation")
	private String nomineeRelation;

	@Column(name = "employer")
	private String employer;

	@Column(name = "occupation_type")
	private String occupationType;

	@Column(name = "organisation_name")
	private String organisationName;

	@Column(name = "street_vendor")
	private String streetVendor;

	@Column(name = "type_of_buss")
	private String typeOfBuss;

	@Column(name = "occupation_tag")
	private String occupationTag;

	@Column(name = "employee_activity")
	private String employeeActivity;

	@Column(name = "buss_add_proof")
	private String bussAddProof;

	@Column(name = "employment_proof")
	private String employmentProof;

	@Column(name = "buss_prem_ownship")
	private String bussPremOwnship;

	@Column(name = "freq_of_income")
	private String freqOfIncome;

	@Column(name = "annual_income")
	private Integer annualIncome;

	@Column(name = "other_src_income")
	private String otherSrcIncome;

	@Column(name = "other_src_ann_inc")
	private Integer otherSrcAnnInc;

	@Formula("NULLIF(buss_emp_start_date, '0000-00-00')")
	private LocalDate bussEmpStartDate;

	@Column(name = "buss_emp_vintage")
	private Integer bussEmpVintage;

	@Column(name = "nature_of_occpn")
	private String natureOfOccpn;

	@Column(name = "designation")
	private String designation;

	@Formula("NULLIF(emp_since, '0000-00-00')")
	private LocalDate empSince;

	@Column(name = "experience")
	private Integer experience;

	@Column(name = "retirement_age")
	private Integer retirementAge;

	@Column(name = "last_employer")
	private String lastEmployer;

	@Column(name = "prev_job_years")
	private Integer prevJobYears;

	@Column(name = "type_of_employer")
	private String typeOfEmployer;

	// Communication Address
	@Column(name = "com_add_line1")
	private String comAddLine1;

	@Column(name = "com_add_line2")
	private String comAddLine2;

	@Column(name = "com_add_line3")
	private String comAddLine3;

	@Column(name = "com_address_same_as")
	private String comAddressSameAs;

	@Column(name = "com_add_type")
	private String comAddType;

	@Column(name = "com_area")
	private String comArea;

	@Column(name = "com_city")
	private String comCity;

	@Column(name = "com_country")
	private String comCountry;

	@Column(name = "com_district")
	private String comDistrict;

	@Column(name = "com_landmark")
	private String comLandmark;

	@Column(name = "com_pincode")
	private Integer comPincode;

	@Column(name = "com_state")
	private String comState;

	// Office Address
	@Column(name = "off_add_line1")
	private String offAddLine1;

	@Column(name = "off_add_line2")
	private String offAddLine2;

	@Column(name = "off_add_line3")
	private String offAddLine3;

	@Column(name = "off_address_same_as")
	private String offAddressSameAs;

	@Column(name = "off_add_type")
	private String offAddType;

	@Column(name = "off_area")
	private String offArea;

	@Column(name = "off_city")
	private String offCity;

	@Column(name = "off_country")
	private String offCountry;

	@Column(name = "off_district")
	private String offDistrict;

	@Column(name = "off_landmark")
	private String offLandmark;

	@Column(name = "off_pincode")
	private Integer offPincode;

	@Column(name = "off_state")
	private String offState;

	@Column(name = "dob_as_per_add_proof")
	private String dobAsPerAddProof;

	@Column(name = "name_as_per_add_proof")
	private String nameAsPerAddProof;

	@Column(name = "residence_add_since")
	private String residenceAddSince;

	@Column(name = "residence_city_since")
	private String residenceCitySince;

	@Column(name = "residence_ownership")
	private String residenceOwnership;

	// Co-applicant section
	@Column(name = "co_customer_id")
	private Integer coCustomerId;

	@Column(name = "co_age")
	private Integer coAge;

	@Column(name = "co_co_applicant_insurance")
	private String coCoApplicantInsurance;

	@Column(name = "co_insurance_reqd")
	private String coInsuranceReqd;

	@Column(name = "co_insured_name")
	private String coInsuredName;

	@Formula("NULLIF(co_nominee_dob, '0000-00-00')")
	private LocalDate coNomineeDob;

	@Column(name = "co_nominee_name")
	private String coNomineeName;

	@Column(name = "co_nominee_relation")
	private String coNomineeRelation;

	@Column(name = "co_employer")
	private String coEmployer;

	@Column(name = "co_occupation_type")
	private String coOccupationType;

	@Column(name = "co_organisation_name")
	private String coOrganisationName;

	@Column(name = "co_street_vendor")
	private String coStreetVendor;

	@Column(name = "co_type_of_buss")
	private String coTypeOfBuss;

	@Column(name = "co_occupation_tag")
	private String coOccupationTag;

	@Column(name = "co_employee_activity")
	private String coEmployeeActivity;

	@Column(name = "co_buss_add_proof")
	private String coBussAddProof;

	@Column(name = "co_employment_proof")
	private String coEmploymentProof;

	@Column(name = "co_buss_prem_ownship")
	private String coBussPremOwnship;

	@Column(name = "co_freq_of_income")
	private String coFreqOfIncome;

	@Column(name = "co_annual_income")
	private Integer coAnnualIncome;

	@Column(name = "co_other_src_income")
	private String coOtherSrcIncome;

	@Column(name = "co_other_src_ann_inc")
	private Integer coOtherSrcAnnInc;

	@Formula("NULLIF(co_buss_emp_start_date, '0000-00-00')")
	private LocalDate coBussEmpStartDate;

	@Column(name = "co_buss_emp_vintage")
	private Integer coBussEmpVintage;

	@Column(name = "co_nature_of_occpn")
	private String coNatureOfOccpn;

	@Column(name = "co_designation")
	private String coDesignation;

	@Formula("NULLIF(co_emp_since, '0000-00-00')")
	private LocalDate coEmpSince;

	@Column(name = "co_experience")
	private Integer coExperience;

	@Column(name = "co_retirement_age")
	private Integer coRetirementAge;

	@Column(name = "co_last_employer")
	private String coLastEmployer;

	@Column(name = "co_prev_job_years")
	private Integer coPrevJobYears;

	@Column(name = "co_type_of_employer")
	private String coTypeOfEmployer;

//----------
	@Column(name = "co_com_add_line1")
	private String coComAddLine1;

	@Column(name = "co_com_add_line2")
	private String coComAddLine2;

	@Column(name = "co_com_add_line3")
	private String coComAddLine3;

	@Column(name = "co_com_address_same_as")
	private String coComAddressSameAs;

	@Column(name = "co_com_add_type")
	private String coComAddType;

	@Column(name = "co_com_area")
	private String coComArea;

	@Column(name = "co_com_city")
	private String coComCity;

	@Column(name = "co_com_country")
	private String coComCountry;

	@Column(name = "co_com_district")
	private String coComDistrict;

	@Column(name = "co_com_landmark")
	private String coComLandmark;

	@Column(name = "co_com_pincode")
	private Integer coComPincode;

	@Column(name = "co_com_state")
	private String coComState;

//----2--

	@Column(name = "co_off_add_line1")
	private String coOffAddLine1;

	@Column(name = "co_off_add_line2")
	private String coOffAddLine2;

	@Column(name = "co_off_add_line3")
	private String coOffAddLine3;

	@Column(name = "co_off_address_same_as")
	private String coOffAddressSameAs;

	@Column(name = "co_off_add_type")
	private String coOffAddType;

	@Column(name = "co_off_area")
	private String coOffArea;

	@Column(name = "co_off_city")
	private String coOffCity;

	@Column(name = "co_off_country")
	private String coOffCountry;

	@Column(name = "co_off_district")
	private String coOffDistrict;

	@Column(name = "co_off_landmark")
	private String coOffLandmark;

	@Column(name = "co_off_pincode")
	private Integer coOffPincode;

	@Column(name = "co_off_state")
	private String coOffState;

	// ---3----
	
	@Column(name = "co_dob_as_per_add_proof")
	private String coDobAsPerAddProof;

	@Column(name = "co_name_as_per_add_proof")
	private String coNameAsPerAddProof;

	@Column(name = "co_residence_add_since")
	private String coResidenceAddSince;

	@Column(name = "co_residence_city_since")
	private String coResidenceCitySince;

	@Column(name = "co_residence_ownership")
	private String coResidenceOwnership;
	
	//---4---
	@Column(name = "co_com_addrsameas")
	private String coComAddrsameas;

	@Column(name = "co_pan_no")
	private String coPanNo;

	@Column(name = "co_Driving_licence_no")
	private String coDrivingLicenceNo;

}
