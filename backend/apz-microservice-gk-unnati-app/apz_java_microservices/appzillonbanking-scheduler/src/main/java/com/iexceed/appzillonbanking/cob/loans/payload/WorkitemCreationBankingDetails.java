package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationBankingDetails {
	
	@JacksonXmlProperty(localName = "custPrefix")
	private String custPrefix;
	
	@JacksonXmlProperty(localName = "Customer_ID")
    private String customerId;
    
	@JacksonXmlProperty(localName = "Account_Holder")
    private String accountHolder;
    
	@JacksonXmlProperty(localName = "Type_of_Account")
    private String typeOfAccount;
    
	@JacksonXmlProperty(localName = "Category_of_Business")
    private String categoryOfBusiness;
    
	@JacksonXmlProperty(localName = "Name_as_per_Bank_Account")
    private String nameAsPerBankAccount;
    
	@JacksonXmlProperty(localName = "IFSC_Code")
    private String ifscCode;
    
	@JacksonXmlProperty(localName = "Bank_Name")
    private String bankName;
    
	@JacksonXmlProperty(localName = "Branch_Name")
    private String branchName;
    
	@JacksonXmlProperty(localName = "Bank_Branch_Pincode")
    private String bankBranchPincode;
    
	@JacksonXmlProperty(localName = "MICR_Code")
    private String micrCode;
    
	@JacksonXmlProperty(localName = "Account_Number")
    private String accountNumber;
    
	@JacksonXmlProperty(localName = "Banking_since")
    private String bankingSince;
    
	@JacksonXmlProperty(localName = "Primary_Bank_Account")
    private String primaryBankAccount;
    
	@JacksonXmlProperty(localName = "UPI")
    private String upi;
    
	@JacksonXmlProperty(localName = "insertionOrderId")
    private String insertionOrderId;
    
	@JacksonXmlProperty(localName = "Cust_Name_list")
    private String custNameList;
    
	@JacksonXmlProperty(localName = "fid")
    private String fid;

}
