package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationDocumentDetails {
	
	@JacksonXmlProperty(localName = "docName")
	private String docName;
	
	@JacksonXmlProperty(localName = "docExtn")
    private String docExtn;
    
	@JacksonXmlProperty(localName = "docContent")
    private String docContent;
    
	@JacksonXmlProperty(localName = "docSize")
    private String docSize;

}
