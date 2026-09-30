package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationBorrowingDetails {
	
	@JacksonXmlProperty(localName = "custPrefix")
	private String custPrefix;
    
	@JacksonXmlProperty(localName = "Name_of_Financier")
	private String nameOfFinancier;
    
	@JacksonXmlProperty(localName = "Loan_amount")
	private String loanAmount;
    
	@JacksonXmlProperty(localName = "POS")
	private String pos;
    
	@JacksonXmlProperty(localName = "EMI")
	private String emi;
    
	@JacksonXmlProperty(localName = "insertionOrderId")
	private String insertionOrderId;
    
	@JacksonXmlProperty(localName = "Applicant_Type")
	private String applicantType;
    
	@JacksonXmlProperty(localName = "Cust_Name_list")
	private String custNameList;
    
	@JacksonXmlProperty(localName = "fid")
	private String fid;

}
