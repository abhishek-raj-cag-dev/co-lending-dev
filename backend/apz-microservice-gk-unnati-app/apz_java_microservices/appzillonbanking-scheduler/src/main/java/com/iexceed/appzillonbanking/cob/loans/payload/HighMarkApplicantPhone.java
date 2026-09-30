package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@JacksonXmlRootElement(localName = "PHONE")
public class HighMarkApplicantPhone {
	
	@JacksonXmlProperty(localName = "TYPE")
	private String type;
	
	@JacksonXmlProperty(localName = "VALUE")
	private String value;

}
