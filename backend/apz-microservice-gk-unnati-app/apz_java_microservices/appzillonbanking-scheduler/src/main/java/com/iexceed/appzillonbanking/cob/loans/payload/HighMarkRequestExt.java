package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;

@JacksonXmlRootElement(localName = "REQUEST-REQUEST-FILE")
@Getter @Setter
public class HighMarkRequestExt {
	
	@JacksonXmlProperty(localName = "HEADER-SEGMENT")
	private HighMarkHeaderSegment headerSegment;
	
	@JacksonXmlProperty(localName = "APPLICANT-SEGMENT")
	private HighMarkApplicantSegment applicantSegment;
	
	@JacksonXmlProperty(localName = "APPLICATION-SEGMENT")
	private HighMarkApplicationSegment applicationSegment;
	
}
