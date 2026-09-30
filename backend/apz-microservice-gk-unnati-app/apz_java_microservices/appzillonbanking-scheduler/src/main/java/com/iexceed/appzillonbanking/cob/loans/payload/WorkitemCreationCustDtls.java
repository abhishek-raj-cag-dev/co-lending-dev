package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationCustDtls {
	
	@JacksonXmlProperty(localName = "PID")
	private String pid;
	
	@JacksonXmlProperty(localName = "No_of_years_relationship")
    private String noOfYrsRel;
    
	@JacksonXmlProperty(localName = "Passport_FileNo")
    private String passportFileNo;
    
	@JacksonXmlProperty(localName = "Passport_DOI")
    private String passportDoi;
	
	@JacksonXmlProperty(localName = "Passport_VFY_Flag")
    private String passportVfyFlag;
    
	@JacksonXmlProperty(localName = "Relationship_Others")
    private String relationshipOthers;
    
	@JacksonXmlProperty(localName = "Aadhar_Vfy_status")
    private String aadharVfyStatus;
    
	@JacksonXmlProperty(localName = "Aaadhar")
    private String aaadhar;
    
	@JacksonXmlProperty(localName = "Nrega")
    private String nrega;
    
	@JacksonXmlProperty(localName = "Landline")
    private String landline;
    
	@JacksonXmlProperty(localName = "OTP_STATUS")
    private String otpStatus;
    
	@JacksonXmlProperty(localName = "Customer_Type")
    private String customerType;
    
	@JacksonXmlProperty(localName = "Customer_Name")
    private String customerName;
    
	@JacksonXmlProperty(localName = "Existing_Customer")
    private String existingCustomer;
    
	@JacksonXmlProperty(localName = "Customer_ID")
    private String customerId;
    
	@JacksonXmlProperty(localName = "Title")
    private String title;
    
	@JacksonXmlProperty(localName = "First_Name")
    private String firstName;
    
	@JacksonXmlProperty(localName = "Middle_Name")
    private String middleName;
    
	@JacksonXmlProperty(localName = "Last_name")
    private String lastName;
    
	@JacksonXmlProperty(localName = "Full_Name")
    private String fullName;
    
	@JacksonXmlProperty(localName = "Gender")
    private String gender;
    
	@JacksonXmlProperty(localName = "Date_Of_Birth")
    private String dateOfBirth;
    
	@JacksonXmlProperty(localName = "Age")
    private String age;
    
	@JacksonXmlProperty(localName = "Mobile_No")
    private String mobileNo;
    
	@JacksonXmlProperty(localName = "Marital_status")
    private String maritalStatus;
    
	@JacksonXmlProperty(localName = "Father_Name")
    private String fatherName;
    
	@JacksonXmlProperty(localName = "Spouse_Name")
    private String spouseName;
    
	@JacksonXmlProperty(localName = "Mother_Maiden_Name")
    private String motherMaidenName;
    
	@JacksonXmlProperty(localName = "Relationship_to_Applicant")
    private String relationshipToApplicant;
    
	@JacksonXmlProperty(localName = "Sec_Mobile_Number")
    private String secMobileNo;
    
	@JacksonXmlProperty(localName = "Email_Id")
    private String emailId;
    
	@JacksonXmlProperty(localName = "Religion")
    private String religion;
    
	@JacksonXmlProperty(localName = "Others_religion")
    private String othersReligion;
    
	@JacksonXmlProperty(localName = "Caste")
    private String caste;
    
	@JacksonXmlProperty(localName = "Education")
    private String education;
    
	@JacksonXmlProperty(localName = "Voter_ID_No")
    private String voterIdNo;
    
	@JacksonXmlProperty(localName = "PAN")
    private String pan;
    
	@JacksonXmlProperty(localName = "Driving_License_No")
    private String drivingLicenseNo;
    
	@JacksonXmlProperty(localName = "Passport_No")
    private String passportNo;
    
	@JacksonXmlProperty(localName = "LPG_Gas_Connection_No")
    private String lpgGasConnectionNo;
    
	@JacksonXmlProperty(localName = "Other_Govt_ID_Desc")
    private String otherGovtIdDesc;
	
	@JacksonXmlProperty(localName = "Govt_Id_No")
    private String govtIdNo;
    
	@JacksonXmlProperty(localName = "Occupation")
    private String occupation;
    
	@JacksonXmlProperty(localName = "insertionOrderId")
    private String insertionOrderId;
    
	@JacksonXmlProperty(localName = "Cust_Prefix")
    private String custPrefix;
    
	@JacksonXmlProperty(localName = "Ration_District")
    private String rationDistrict;
    
	@JacksonXmlProperty(localName = "Ration_Taluk")
    private String rationTaluk;
    
	@JacksonXmlProperty(localName = "Ration_Shop")
    private String rationShop;
    
	@JacksonXmlProperty(localName = "Sec_Voter_ID")
    private String secVoterId;

	@JacksonXmlProperty(localName = "CKYC")
    private String ckyc;
}
