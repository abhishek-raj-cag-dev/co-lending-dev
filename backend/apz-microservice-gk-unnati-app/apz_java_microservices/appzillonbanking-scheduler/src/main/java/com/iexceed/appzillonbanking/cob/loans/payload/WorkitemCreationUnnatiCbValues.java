package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationUnnatiCbValues {
	
	@JacksonXmlProperty(localName = "PID")
	private String pid;
	
	@JacksonXmlProperty(localName = "AppType")
    private String appType;
    
	@JacksonXmlProperty(localName = "Service")
    private String service;
    
	@JacksonXmlProperty(localName = "Rulename")
    private String rulename;
    
	@JacksonXmlProperty(localName = "VAL")
    private String val;
    
	@JacksonXmlProperty(localName = "Limit")
    private String limit;
    
	@JacksonXmlProperty(localName = "LoanLimit")
    private String loanLimit;

}
