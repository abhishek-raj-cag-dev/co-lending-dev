package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HighMarkApplicationSegment {
	
	@JacksonXmlProperty(localName = "LOAN-TYPE")
	private String loanType;
	
	@JacksonXmlProperty(localName = "LOAN-PURPOSE")
	private String loanPurpose;
	
	@JacksonXmlProperty(localName = "APPLICATION-DATE")
	private String applicationDt;
	
	@JacksonXmlProperty(localName = "CONSUMER-ID")
	private String consumerId;
	
	@JacksonXmlProperty(localName = "APPLICATION-ID")
	private String applicationId;
	
	@JacksonXmlProperty(localName = "LOAN-AMOUNT")
	private String loanAmount;
	
	@JacksonXmlProperty(localName = "BRANCH")
	private String branch;
	
	@JacksonXmlProperty(localName = "KENDRA")
	private String kendra;
	
	@JacksonXmlProperty(localName = "IFSC-CODE")
	private String ifscCode;
	
	@JacksonXmlProperty(localName = "BRANCH-STATE")
	private String branchState;
	
	@JacksonXmlProperty(localName = "OWNERSHIP-INDICATOR")
	private String ownershipIndicator;

}
