package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationChequeDtls {
	
	@JacksonXmlProperty(localName = "Type")
	private String type;
	
	@JacksonXmlProperty(localName = "Cheque_Number")
    private String chequeNumber;
    
	@JacksonXmlProperty(localName = "Cheque_Date")
    private String chequeDate;
    
	@JacksonXmlProperty(localName = "Amount")
    private String amount;
    
	@JacksonXmlProperty(localName = "insertionOrderId")
    private String insertionOrderId;
    
	@JacksonXmlProperty(localName = "Primary_Bank_name")
    private String primaryBankName;
    
	@JacksonXmlProperty(localName = "Primary_Branch_Name")
    private String primaryBranchName;
    
	@JacksonXmlProperty(localName = "sno")
    private String sno;
    
	@JacksonXmlProperty(localName = "Pid")
    private String pid;

}
