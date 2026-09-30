package com.iexceed.appzillonbanking.cob.core.domain.ab;

import java.time.LocalDate;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "unnati_iexceed_renewal_lead_table")
@Getter
@Setter
@ToString
public class RenewalLeadDetails_old {

	@Id
	@JsonProperty("customerId")
	@Column(name = "Customer_id")
	private String customerId;

	@JsonProperty("customerType")
	@Column(name = "Customer_type")
	private String customerType;

	@JsonProperty("fullName")
	@Column(name = "Full_name")
	private String fullName;

	@JsonProperty("title")
	@Column(name = "Title")
	private String title;

	@JsonProperty("firstName")
	@Column(name = "First_name")
	private String firstName;

	@JsonProperty("middleName")
	@Column(name = "Middle_Name")
	private String middleName;

	@JsonProperty("lastName")
	@Column(name = "Last_name")
	private String lastName;

	@JsonProperty("dob")
	@Column(name = "Date_of_birth")
	private LocalDate dob;

	@JsonProperty("age")
	@Column(name = "Age")
	private Integer age;

	@JsonProperty("gender")
	@Column(name = "Gender")
	private String gender;

	@JsonProperty("mobileNo")
	@Column(name = "Mobile_number")
	private String mobileNo;

	@JsonProperty("maritalStatus")
	@Column(name = "Marital_status")
	private String maritalStatus;

	@JsonProperty("fathersName")
	@Column(name = "Father_name")
	private String fathersName;

	@JsonProperty("spouseName")
	@Column(name = "Spouse_Name")
	private String spouseName;

	@JsonProperty("primaryKyc")
	@Column(name = "Primary_kyc")
	private String primaryKyc;

	@JsonProperty("occupation")
	@Column(name = "Occupation")
	private String occupation;

	@JsonProperty("education")
	@Column(name = "App_education")
	private String education;

	@JsonProperty("religion")
	@Column(name = "App_Religion")
	private String religion;

	@JsonProperty("caste")
	@Column(name = "App_caste")
	private String caste;

	@JsonProperty("nameAsPerBankAcc")
	@Column(name = "Name_as_per_Bank_Account")
	private String nameAsPerBankAcc;

	@JsonProperty("ifscCode")
	@Column(name = "IFSC_Code")
	private String ifscCode;

	@JsonProperty("bankName")
	@Column(name = "Bank_Name")
	private String bankName;

	@JsonProperty("branchName")
	@Column(name = "Branch_Name")
	private String branchName;

	@JsonProperty("accNumber")
	@Column(name = "Account_Number")
	private String accNumber;

	@JsonProperty("typeOfAcc")
	@Column(name = "Type_of_account")
	private String typeOfAcc;

	@JsonProperty("presAddrType")
	@Column(name = "Present_Address_Type")
	private String presAddrType;

	@JsonProperty("presLine1")
	@Column(name = "Present_Line_1")
	private String presLine1;

	@JsonProperty("presLine2")
	@Column(name = "Present_Line_2")
	private String presLine2;

	@JsonProperty("presLine3")
	@Column(name = "Present_Line_3")
	private String presLine3;

	@JsonProperty("presPincode")
	@Column(name = "Present_Pincode")
	private String presPincode;

	@JsonProperty("presArea")
	@Column(name = "Present_Area")
	private String presArea;

	@JsonProperty("presCityTownVillage")
	@Column(name = "Present_City_Town_Village")
	private String presCityTownVillage;

	@JsonProperty("presDist")
	@Column(name = "Present_District")
	private String presDist;

	@JsonProperty("presState")
	@Column(name = "Present_state")
	private String presState;

	@JsonProperty("presCountry")
	@Column(name = "Present_Country")
	private String presCountry;

	@JsonProperty("prestAddreLandmark")
	@Column(name = "Prest_Addre_Landmark")
	private String prestAddreLandmark;

	@JsonProperty("prestAddreCurrentaddressproof")
	@Column(name = "Prest_Addre_CurrentAddressProof")
	private String prestAddreCurrentaddressproof;

	@JsonProperty("prestAddreHousetype")
	@Column(name = "Prest_Addre_Housetype")
	private String prestAddreHousetype;

	@JsonProperty("prestAddreResidentownership")
	@Column(name = "Prest_Addre_residentownership")
	private String prestAddreResidentownership;

	@JsonProperty("prestAddreResidenceaddresssince")
	@Column(name = "Prest_Addre_ResidenceAddressSince")
	private String prestAddreResidenceaddresssince;

	@JsonProperty("prestAddreResidencecitysince")
	@Column(name = "Prest_Addre_ResidenceCitySince")
	private String prestAddreResidencecitysince;

	@JsonProperty("presLocationCoords")
	@Column(name = "Present_Location_Co_ordinates")
	private String presLocationCoords;

	@JsonProperty("permAddrType")
	@Column(name = "permanent_Address_Type")
	private String permAddrType;

	@JsonProperty("permLine1")
	@Column(name = "permanent_Line_1")
	private String permLine1;

	@JsonProperty("permLine2")
	@Column(name = "permanent_Line_2")
	private String permLine2;

	@JsonProperty("permLine3")
	@Column(name = "permanent_Line_3")
	private String permLine3;

	@JsonProperty("permPincode")
	@Column(name = "permanent_Pincode")
	private String permPincode;

	@JsonProperty("permArea")
	@Column(name = "permanent_Area")
	private String permArea;

	@JsonProperty("permCityTownVillage")
	@Column(name = "permanent_City_Town_Village")
	private String permCityTownVillage;

	@JsonProperty("permDist")
	@Column(name = "permanent_District")
	private String permDist;

	@JsonProperty("permState")
	@Column(name = "permanent_state")
	private String permState;

	@JsonProperty("permCountry")
	@Column(name = "permanent_Country")
	private String permCountry;

	@JsonProperty("permtAddreLandmark")
	@Column(name = "Permt_Addre_Landmark")
	private String permtAddreLandmark;

	@JsonProperty("permtAddreCurrentaddressproof")
	@Column(name = "Permt_Addre_CurrentAddressProof")
	private String permtAddreCurrentaddressproof;

	@JsonProperty("permtAddreHousetype")
	@Column(name = "Permt_Addre_Housetype")
	private String permtAddreHousetype;

	@JsonProperty("permtAddreResidentownership")
	@Column(name = "Permt_Addre_residentownership")
	private String permtAddreResidentownership;

	@JsonProperty("permtAddreResidenceaddresssince")
	@Column(name = "Permt_Addre_ResidenceAddressSince")
	private String permtAddreResidenceaddresssince;

	@JsonProperty("permtAddreResidencecitysince")
	@Column(name = "Permt_Addre_ResidenceCitySince")
	private String permtAddreResidencecitysince;

	@JsonProperty("permLocationCoords")
	@Column(name = "permanent_Location_Co_ordinates")
	private String permLocationCoords;

	@JsonProperty("kendraId")
	@Column(name = "Kendra_ID")
	private String kendraId;

	@JsonProperty("kendraName")
	@Column(name = "Kendra_Name")
	private String kendraName;

	@JsonProperty("branchId")
	@Column(name = "GL_Branch_ID")
	private String glBranchId;

	@JsonProperty("glBranchName")
	@Column(name = "GL_Branch_Name")
	private String glBranchName;

	@JsonProperty("noOfYrsRel")
	@Column(name = "No_of_years_relationship")
	private String noOfYrsRel;

	@JsonProperty("groupId")
	@Column(name = "Group_ID")
	private String groupId;

	@JsonProperty("kendraSize")
	@Column(name = "Kendra_Size")
	private String kendraSize;

	@JsonProperty("kendraVintageYrs")
	@Column(name = "Kendra_Vintage_Yrs")
	private String kendraVintageYrs;

	@JsonProperty("groupSize")
	@Column(name = "Group_Size")
	private String groupSize;

	@JsonProperty("kendraParStatus")
	@Column(name = "kendra_Par_Status")
	private String kendraParStatus;

	@JsonProperty("kendraMeetingFreq")
	@Column(name = "Kendra_Meeting_freq")
	private String kendraMeetingFreq;

	@JsonProperty("kendraMeetingDay")
	@Column(name = "Kendra_Meeting_day")
	private String kendraMeetingDay;

	@JsonProperty("glRegion")
	@Column(name = "GL_Region")
	private String glRegion;

	@JsonProperty("glArea")
	@Column(name = "Gl_Area")
	private String glArea;

	@JsonProperty("bankBranchPincode")
	@Column(name = "Bank_Branch_Pincode")
	private String bankBranchPincode;

	@JsonProperty("pid")
	@Column(name = "PID")
	private String pid;

	@Transient
	private String uniqueId;

	@JsonProperty("uniqueId")
	public String getUniqueId() {
		return pid;
	}

	@JsonProperty("caglOs")
	@Column(name = "CaglOs")
	private String caglOs;

	@JsonProperty("priority")
	@Column(name = "Priority")
	private String priority;

	@JsonProperty("glBranchState")
	@Column(name = "GL_Branch_state")
	private String glBranchState;

	@JsonProperty("oldPid")
	@Column(name = "OLD_PID")
	private String oldPid;

	@JsonProperty("coCustomerId")
	@Column(name = "Co_Customer_ID")
	private String coCustomerId;

	@JsonProperty("coTitle")
	@Column(name = "Co_Title")
	private String coTitle;

	@JsonProperty("coFullName")
	@Column(name = "Co_Full_Name")
	private String coFullName;

	@JsonProperty("coDateOfBirth")
	@Column(name = "Co_Date_Of_Birth")
	private LocalDate coDateOfBirth;

	@JsonProperty("coAge")
	@Column(name = "Co_age")
	private String coAge;

	@JsonProperty("coMobileNo")
	@Column(name = "Co_Mobile_No")
	private String coMobileNo;

	@JsonProperty("coMaritalStatus")
	@Column(name = "Co_Marital_status")
	private String coMaritalStatus;

	@JsonProperty("coFatherName")
	@Column(name = "Co_Father_Name")
	private String coFatherName;

	@JsonProperty("coSpouseName")
	@Column(name = "Co_Spouse_Name")
	private String coSpouseName;

	@JsonProperty("coVoterIdNo")
	@Column(name = "Co_Voter_ID_No")
	private String coVoterIdNo;

	@JsonProperty("coOccupation")
	@Column(name = "Co_Occupation")
	private String coOccupation;

	@JsonProperty("coGender")
	@Column(name = "Co_Gender")
	private String coGender;

	@JsonProperty("coFirstname")
	@Column(name = "Co_FirstName")
	private String coFirstname;

	@JsonProperty("coMiddlename")
	@Column(name = "Co_MiddleName")
	private String coMiddlename;

	@JsonProperty("coLastname")
	@Column(name = "Co_LastName")
	private String coLastname;

	@JsonProperty("coRelationshipwithapplicant")
	@Column(name = "Co_RelationShipWithApplicant")
	private String coRelationshipwithapplicant;

	@JsonProperty("coNameperkyc")
	@Column(name = "Co_NamePerKyc")
	private String coNameperkyc;

	@JsonProperty("coReligion")
	@Column(name = "Co_Religion")
	private String coReligion;

	@JsonProperty("coCaste")
	@Column(name = "Co_Caste")
	private String coCaste;

	@JsonProperty("coEducation")
	@Column(name = "Co_Education")
	private String coEducation;

	@JsonProperty("coPresentLine1")
	@Column(name = "Co_Present_Line_1")
	private String coPresentLine1;

	@JsonProperty("coPresentLine2")
	@Column(name = "Co_Present_Line_2")
	private String coPresentLine2;

	@JsonProperty("coPresentLine3")
	@Column(name = "Co_Present_Line_3")
	private String coPresentLine3;

	@JsonProperty("coPresentPincode")
	@Column(name = "Co_Present_Pincode")
	private String coPresentPincode;

	@JsonProperty("coPresentArea")
	@Column(name = "Co_Present_Area")
	private String coPresentArea;

	@JsonProperty("coPresentCityTownVillage")
	@Column(name = "Co_Present_City_Town_Village")
	private String coPresentCityTownVillage;

	@JsonProperty("coPresentDistrict")
	@Column(name = "Co_Present_District")
	private String coPresentDistrict;

	@JsonProperty("coPresentState")
	@Column(name = "Co_Present_state")
	private String coPresentState;

	@JsonProperty("coPresentCountry")
	@Column(name = "Co_Present_Country")
	private String coPresentCountry;

	@JsonProperty("coPrestAddresaddresstype")
	@Column(name = "Co_Prest_AddresAddresstype")
	private String coPrestAddresaddresstype;

	@JsonProperty("coPrestAddreslandmark")
	@Column(name = "Co_Prest_AddresLandmark")
	private String coPrestAddreslandmark;

	@JsonProperty("coPrestAddrescurrentaddressproof")
	@Column(name = "Co_Prest_AddresCurrentAddressProof")
	private String coPrestAddrescurrentaddressproof;

	@JsonProperty("coPrestAddreshousetype")
	@Column(name = "Co_Prest_AddresHousetype")
	private String coPrestAddreshousetype;

	@JsonProperty("coPrestAddresresidentownership")
	@Column(name = "Co_Prest_Addresresidentownership")
	private String coPrestAddresresidentownership;

	@JsonProperty("coPrestAddresresidenceaddresssince")
	@Column(name = "Co_Prest_AddresResidenceAddressSince")
	private String coPrestAddresresidenceaddresssince;

	@JsonProperty("coPrestAddresresidencecitysince")
	@Column(name = "Co_Prest_AddresResidenceCitySince")
	private String coPrestAddresresidencecitysince;

	@JsonProperty("coPresentLocationCoOrdinates")
	@Column(name = "Co_Present_Location_Co_ordinates")
	private String coPresentLocationCoOrdinates;

	@JsonProperty("coPermanentLine1")
	@Column(name = "Co_Permanent_Line_1")
	private String coPermanentLine1;

	@JsonProperty("coPermanentLine2")
	@Column(name = "Co_Permanent_Line_2")
	private String coPermanentLine2;

	@JsonProperty("coPermanentLine3")
	@Column(name = "Co_Permanent_Line_3")
	private String coPermanentLine3;

	@JsonProperty("coPermanentPincode")
	@Column(name = "Co_Permanent_Pincode")
	private String coPermanentPincode;

	@JsonProperty("coPermanentArea")
	@Column(name = "Co_Permanent_Area")
	private String coPermanentArea;

	@JsonProperty("coPermanentCityTownVillage")
	@Column(name = "Co_Permanent_City_Town_Village")
	private String coPermanentCityTownVillage;

	@JsonProperty("coPermanentDistrict")
	@Column(name = "Co_Permanent_District")
	private String coPermanentDistrict;

	@JsonProperty("coPermanentState")
	@Column(name = "Co_Permanent_state")
	private String coPermanentState;

    @JsonProperty("coPermanentSameAs")
    @Column(name = "co_permt_addrsameas")
    private String coPermanentSameAs;

    @JsonProperty("coLocationCoordinatesFor")
    @Column(name = "co_location_co_ordinates_for ")
    private String coLocationCoordinatesFor;

	@JsonProperty("coPermanentCountry")
	@Column(name = "Co_Permanent_Country")
	private String coPermanentCountry;

	@JsonProperty("coPermtAddresaddresstype")
	@Column(name = "Co_Permt_AddresAddresstype")
	private String coPermtAddresaddresstype;

	@JsonProperty("coPermtAddreslandmark")
	@Column(name = "Co_Permt_AddresLandmark")
	private String coPermtAddreslandmark;

	@JsonProperty("coPermtAddrescurrentaddressproof")
	@Column(name = "Co_Permt_AddresCurrentAddressProof")
	private String coPermtAddrescurrentaddressproof;

	@JsonProperty("coPermtAddreshousetype")
	@Column(name = "Co_Permt_AddresHousetype")
	private String coPermtAddreshousetype;

	@JsonProperty("coPermtAddresresidentownership")
	@Column(name = "Co_Permt_Addresresidentownership")
	private String coPermtAddresresidentownership;

	@JsonProperty("coPermtAddresresidenceaddresssince")
	@Column(name = "Co_Permt_AddresResidenceAddressSince")
	private String coPermtAddresresidenceaddresssince;

	@JsonProperty("coPermtAddresresidencecitysince")
	@Column(name = "Co_Permt_AddresResidenceCitySince")
	private String coPermtAddresresidencecitysince;

	@JsonProperty("coPermanentLocationCoOrdinates")
	@Column(name = "Co_Permanent_Location_Co_ordinates")
	private String coPermanentLocationCoOrdinates;

	@JsonProperty("emailId")
	@Column(name = "Branch_E_mail_ID")
	private String emailId;

	@JsonProperty("applicantCkyc")
	@Column(name = "Applicant_C_KYC")
	private String applicantCkyc;

	@JsonProperty("coApplicantCkyc")
	@Column(name = "Co_Applicant_C_KYC")
	private String coApplicantCkyc;
	
	@JsonProperty("activationDate")
	@Column(name = "first_act_date")
	private LocalDate activationDate;

	@JsonProperty("urn")
	@Column(name = "urn")
	private String urn;

	public RenewalLeadDetails_old() {
	}

	public RenewalLeadDetails_old(String pid, String firstName, String kendraName, String kendraId, String kendraVintageYrs,
			String kendraMeetingDay, String customerId, String caglOs, String priority) {
		this.pid = pid;
		this.firstName = firstName;
		this.kendraName = kendraName;
		this.kendraId = kendraId;
		this.kendraVintageYrs = kendraVintageYrs;
		this.kendraMeetingDay = kendraMeetingDay;
		this.customerId = customerId;
		this.caglOs = caglOs;
		this.priority = priority;
	}

}
