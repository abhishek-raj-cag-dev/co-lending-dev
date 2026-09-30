package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HighMarkApplicantName {
	
	@JacksonXmlProperty(localName = "NAME1")
	private String name1;
	
	@JacksonXmlProperty(localName = "NAME2")
	private String name2;
	
	@JacksonXmlProperty(localName = "NAME3")
	private String name3;
	
	@JacksonXmlProperty(localName = "NAME4")
	private String name4;
	
	@JacksonXmlProperty(localName = "NAME5")
	private String name5;

}
