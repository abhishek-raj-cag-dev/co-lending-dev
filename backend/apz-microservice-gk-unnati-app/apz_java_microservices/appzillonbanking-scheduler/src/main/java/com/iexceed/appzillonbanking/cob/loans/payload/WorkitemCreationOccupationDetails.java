package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationOccupationDetails {
	
	@JacksonXmlProperty(localName = "custPrefix")
	private String custPrefix;
	
	@JacksonXmlProperty(localName = "Buss_Emp_Entity_Type")
	private String bussEmpEntityType;
	
	@JacksonXmlProperty(localName = "Nature_of_Buss_Emp")
    private String natureOfBussEmp;
    
	@JacksonXmlProperty(localName = "Buss_Emp_Activity")
    private String bussEmpActivity;
    
	@JacksonXmlProperty(localName = "Street_Vendor")
    private String streetVendor;
    
	@JacksonXmlProperty(localName = "Buss_Emp_Start_date")
    private String bussEmpStartDate;
    
	@JacksonXmlProperty(localName = "Buss_Emp_Vintage")
    private String bussEmpVintage;
    
	@JacksonXmlProperty(localName = "Buss_Address_Proof")
    private String bussAddressProof;
    
	@JacksonXmlProperty(localName = "Name_of_Org_Employer")
    private String nameOfOrgEmployer;
    
	@JacksonXmlProperty(localName = "Emp_Proof")
    private String empProof;
    
	@JacksonXmlProperty(localName = "Buss_Premise_Area")
    private String bussPremiseArea;
    
	@JacksonXmlProperty(localName = "Buss_Premise_Ownership")
    private String bussPremiseOwnership;
    
	@JacksonXmlProperty(localName = "Mode_of_Income")
    private String modeOfIncome;
    
	@JacksonXmlProperty(localName = "Freq_of_Income")
    private String freqOfIncome;
    
	@JacksonXmlProperty(localName = "Annual_Income")
    private String annualIncome;
    
	@JacksonXmlProperty(localName = "Other_Src_of_Income")
    private String otherSrcOfIncome;
    
	@JacksonXmlProperty(localName = "Other_Src_Annual_Income")
    private String otherSrcAnnualIncome;
    
	@JacksonXmlProperty(localName = "InsertionOrderId")
    private String insertionOrderID;
    
	@JacksonXmlProperty(localName = "Customer_Name")
    private String customerName;
    
	@JacksonXmlProperty(localName = "Customer_Type")
    private String customerType;
    
	@JacksonXmlProperty(localName = "Cust_Name_list")
    private String custNameList;
    
	@JacksonXmlProperty(localName = "fid")
    private String fid;
    
	@JacksonXmlProperty(localName = "Buss_Asset_Others")
    private String bussAssetOthers;
    
	@JacksonXmlProperty(localName = "GRT_Nature_Emp")
    private String grtNatureEmp;
    
	@JacksonXmlProperty(localName = "GRT_Business_Emp")
    private String grtBusinessEmp;
    
	@JacksonXmlProperty(localName = "Buss_Asset_Details")
    private String bussAssetDetails;

}
