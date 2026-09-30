package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JacksonXmlRootElement(localName = "ADDRESS")
public class HighMarkApplicantAddress {
	
	@JacksonXmlProperty(localName = "TYPE")
	private String type;
	
	@JacksonXmlCData
	@JacksonXmlProperty(localName = "ADDRESS-1")
	private String addressLine1;
	
	@JacksonXmlProperty(localName = "CITY")
	private String city;
	
	@JacksonXmlProperty(localName = "STATE")
	private String state;
	
	@JacksonXmlProperty(localName = "PINCODE")
	private String pincode;
}
