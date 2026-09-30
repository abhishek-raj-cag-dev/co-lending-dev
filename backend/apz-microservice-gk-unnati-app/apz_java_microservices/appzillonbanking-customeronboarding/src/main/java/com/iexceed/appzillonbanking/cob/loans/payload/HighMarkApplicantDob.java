package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HighMarkApplicantDob {
	
	@JacksonXmlProperty(localName = "DOB-DATE")
	private String dobDate;
	
	@JacksonXmlProperty(localName = "AGE")
	private String age;
	
	@JacksonXmlProperty(localName = "AGE-AS-ON")
	private String ageAsOn;
	

}
