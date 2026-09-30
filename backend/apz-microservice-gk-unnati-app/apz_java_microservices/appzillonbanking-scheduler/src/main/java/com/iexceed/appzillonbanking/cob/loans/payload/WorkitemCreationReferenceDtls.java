package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationReferenceDtls {
	
	@JacksonXmlProperty(localName = "Reference_No")
	private String referenceNo;
	
	@JacksonXmlProperty(localName = "Name")
    private String name;
    
	@JacksonXmlProperty(localName = "Customer_ID")
    private String customerId;
    
	@JacksonXmlProperty(localName = "Rship_with_Applicant")
    private String rshipWithApplicant;
    
	@JacksonXmlProperty(localName = "Rship_in_Years")
    private String rshipInYears;
    
	@JacksonXmlProperty(localName = "Mobile_No")
    private String mobileNo;
    
	@JacksonXmlProperty(localName = "Resi_Address")
    private String resiAddress;
    
	@JacksonXmlProperty(localName = "insertionOrderId")
    private String insertionOrderId;

}
