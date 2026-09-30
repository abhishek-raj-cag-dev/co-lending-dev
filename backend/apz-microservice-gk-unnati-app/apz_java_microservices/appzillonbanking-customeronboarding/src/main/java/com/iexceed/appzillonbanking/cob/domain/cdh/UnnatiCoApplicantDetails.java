package com.iexceed.appzillonbanking.cob.domain.cdh;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unnati_co_applicant_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnnatiCoApplicantDetails {

	@Id
	@Column(name = "unique_id")
	private Long uniqueId;

	@Column(name = "reference_id", length = 50)
	private String referenceId;

	@Column(name = "application_id")
	private String applicationId;

	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "co_customer_id", length = 50)
	private String coCustomerId;

	@Column(name = "co_title", length = 10)
	private String coTitle;

	@Column(name = "co_full_name", length = 255)
	private String coFullName;

	@Column(name = "co_date_of_birth")
	private LocalDate coDateOfBirth;

	@Column(name = "co_age")
	private Integer coAge;

	@Column(name = "co_mobile_no", length = 20)
	private String coMobileNo;

	@Column(name = "co_marital_status", length = 20)
	private String coMaritalStatus;

	@Column(name = "co_father_name", length = 255)
	private String coFatherName;

	@Column(name = "co_spouse_name", length = 255)
	private String coSpouseName;

	@Column(name = "co_voter_id_no", length = 50)
	private String coVoterIdNo;

	@Column(name = "co_occupation", length = 100)
	private String coOccupation;

	// Present Address
	@Column(name = "co_present_line_1")
	private String coPresentLine1;

	@Column(name = "co_present_line_2")
	private String coPresentLine2;

	@Column(name = "co_present_line_3")
	private String coPresentLine3;

	@Column(name = "co_present_pincode")
	private Integer coPresentPincode;

	@Column(name = "co_present_area")
	private String coPresentArea;

	@Column(name = "co_present_city_town_village")
	private String coPresentCityTownVillage;

	@Column(name = "co_present_district")
	private String coPresentDistrict;

	@Column(name = "co_present_state")
	private String coPresentState;

	@Column(name = "co_present_country")
	private String coPresentCountry;

	@Column(name = "co_present_location_co_ordinates")
	private String coPresentLocationCoOrdinates;

	// Permanent Address
	@Column(name = "co_permanent_line_1")
	private String coPermanentLine1;

	@Column(name = "co_permanent_line_2")
	private String coPermanentLine2;

	@Column(name = "co_permanent_line_3")
	private String coPermanentLine3;

	@Column(name = "co_permanent_pincode")
	private Integer coPermanentPincode;

	@Column(name = "co_permanent_area")
	private String coPermanentArea;

	@Column(name = "co_permanent_city_town_village")
	private String coPermanentCityTownVillage;

	@Column(name = "co_permanent_district")
	private String coPermanentDistrict;

	@Column(name = "co_permanent_state")
	private String coPermanentState;

	@Column(name = "co_permanent_country")
	private String coPermanentCountry;

	@Column(name = "co_permanent_location_co_ordinates")
	private String coPermanentLocationCoOrdinates;

	@Column(name = "co_gender", length = 10)
	private String coGender;

	@Column(name = "co_firstname")
	private String coFirstname;

	@Column(name = "co_middlename")
	private String coMiddlename;

	@Column(name = "co_lastname")
	private String coLastname;

	@Column(name = "co_relationshipwithapplicant")
	private String coRelationshipwithapplicant;

	@Column(name = "co_nameperkyc")
	private String coNameperkyc;

	@Column(name = "co_religion")
	private String coReligion;

	@Column(name = "co_caste")
	private String coCaste;

	@Column(name = "co_education")
	private String coEducation;

	// Present Address Extra
	@Column(name = "co_prest_addresaddresstype")
	private String coPrestAddresAddresstype;

	@Column(name = "co_prest_addreslandmark")
	private String coPrestAddresLandmark;

	@Column(name = "co_prest_addrescurrentaddressproof")
	private String coPrestAddresCurrentaddressproof;

	@Column(name = "co_prest_addreshousetype")
	private String coPrestAddresHousetype;

	@Column(name = "co_prest_addresresidentownership")
	private String coPrestAddresResidentownership;

	@Column(name = "co_prest_addresresidenceaddresssince")
	private String coPrestAddresResidenceaddresssince;

	@Column(name = "co_prest_addresresidencecitysince")
	private String coPrestAddresResidencecitysince;

	// Permanent Address Extra
	@Column(name = "co_permt_addresaddresstype")
	private String coPermtAddresAddresstype;

	@Column(name = "co_permt_addreslandmark")
	private String coPermtAddresLandmark;

	@Column(name = "co_permt_addrescurrentaddressproof")
	private String coPermtAddresCurrentaddressproof;

	@Column(name = "co_permt_addreshousetype")
	private String coPermtAddresHousetype;

	@Column(name = "co_permt_addresresidentownership")
	private String coPermtAddresResidentownership;

	@Column(name = "co_permt_addresresidenceaddresssince")
	private String coPermtAddresResidenceaddresssince;

	@Column(name = "co_permt_addresresidencecitysince")
	private String coPermtAddresResidencecitysince;

	@Column(name = "co_applicant_c_kyc")
	private Integer coApplicantCKyc;

	@Column(name = "co_permt_addrsameas", length = 3)
	private String coPermtAddrsameas;

	@Column(name = "co_location_co_ordinates_for")
	private String coLocationCoOrdinatesFor;

}