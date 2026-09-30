package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@JacksonXmlRootElement(localName = "WI-REQ")
public class WipDedupeRequestExt {
	
	@JacksonXmlProperty(localName = "Type")
	private String customerType;
	
	@JacksonXmlProperty(localName = "CustomerID")
	private String customerId;
	
	@JacksonXmlProperty(localName = "VoterID")
	private String primaryKycId;
}
