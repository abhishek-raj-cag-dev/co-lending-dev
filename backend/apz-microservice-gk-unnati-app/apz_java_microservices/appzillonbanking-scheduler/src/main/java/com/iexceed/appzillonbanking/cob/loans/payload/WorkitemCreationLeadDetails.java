package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkitemCreationLeadDetails {

	@JacksonXmlProperty(localName = "PreScreening_Status")
	private String preScreeningStatus;

	@JacksonXmlProperty(localName = "Previous_Loan_ID")
	private String previousLoanId;

	@JacksonXmlProperty(localName = "No_of_years_relationship")
	private String noOfYrsRel;

	@JacksonXmlProperty(localName = "Area_Mapping")
	private String areaMapping;

	@JacksonXmlProperty(localName = "Customer_Par")
	private String customerPar;

	@JacksonXmlProperty(localName = "Sourcing_Officer")
	private String sourcingOfficer;

	@JacksonXmlProperty(localName = "Type_of_Customer")
	private String typeOfCustomer;

	@JacksonXmlProperty(localName = "RelationShip_with_CAGL")
	private String relWithCagl;
	
	//@JacksonXmlProperty(localName = "No_of_years_relationship")
	//private String noOfYrsRelationship;

	@JacksonXmlProperty(localName = "Customer_ID")
	private String customerId;

	@JacksonXmlProperty(localName = "Title")
	private String title;

	@JacksonXmlProperty(localName = "First_Name")
	private String firstName;

	@JacksonXmlProperty(localName = "Middle_Name")
	private String middleName;

	@JacksonXmlProperty(localName = "Last_Name")
	private String lastName;

	@JacksonXmlProperty(localName = "Gender")
	private String gender;

	@JacksonXmlProperty(localName = "Date_of_Birth")
	private String dob;

	@JacksonXmlProperty(localName = "Primary_Phone_no")
	private String primaryPhoneNo;

	@JacksonXmlProperty(localName = "Nature_of_Buss_Emp")
	private String natureOfBussEmp;

	@JacksonXmlProperty(localName = "Buss_Emp_Activity")
	private String bussEmpActivity;
	
	@JacksonXmlProperty(localName = "Main_Loan_Product")
	private String mainLoanProduct;

	@JacksonXmlProperty(localName = "Main_Loan_Amount")
	private String mainLoanAmount;
	
	@JacksonXmlProperty(localName = "Fresh_Repeat")
	private String freshRepeat;
	
	//@JacksonXmlProperty(localName = "Previous_Loan_ID")
	//private String previousLoanId;
	
	@JacksonXmlProperty(localName = "Source_of_lead")
	private String sourceOfLead;
	
	@JacksonXmlProperty(localName = "BDO_Name")
	private String bdoName;
	
	@JacksonXmlProperty(localName = "Kendra_ID")
	private String kendraId;
	
	@JacksonXmlProperty(localName = "Kendra_Name")
	private String kendraName;
	
	@JacksonXmlProperty(localName = "KM_Name")
	private String kmName;
	
	@JacksonXmlProperty(localName = "KM_GK_ID")
	private String kmGkId;
	
	@JacksonXmlProperty(localName = "GL_Branch_Name")
	private String glBranchName;
	
	@JacksonXmlProperty(localName = "GL_Branch_ID")
	private String glBranchId;
	
	@JacksonXmlProperty(localName = "Branch_Name")
	private String branchName;
	
	@JacksonXmlProperty(localName = "Area")
	private String area;
	
	@JacksonXmlProperty(localName = "Region")
	private String region;
	
	@JacksonXmlProperty(localName = "rf_branch_desc")
	private String rfBranchDesc;
	
	@JacksonXmlProperty(localName = "CRM_Name")
	private String crmName;
	
	@JacksonXmlProperty(localName = "CRM_GKID")
	private String crmGkId;
	
	@JacksonXmlProperty(localName = "Group_ID")
	private String groupId;
	
	@JacksonXmlProperty(localName = "Kendra_Size")
	private String kendraSize;
	
	@JacksonXmlProperty(localName = "Kendra_Vintage_Yrs")
	private String kendraVintageYrs;
	
	@JacksonXmlProperty(localName = "Group_Size")
	private String groupSize;
	
	@JacksonXmlProperty(localName = "kendra_Par_Status")
	private String kendraParStatus;
	
	@JacksonXmlProperty(localName = "Kendra_Meeting_freq")
	private String kendraMeetingFreq;
	
	@JacksonXmlProperty(localName = "Kendra_Meeting_day")
	private String kendraMeetingDay;
	
	@JacksonXmlProperty(localName = "Loan_Type")
	private String loanType;

}
