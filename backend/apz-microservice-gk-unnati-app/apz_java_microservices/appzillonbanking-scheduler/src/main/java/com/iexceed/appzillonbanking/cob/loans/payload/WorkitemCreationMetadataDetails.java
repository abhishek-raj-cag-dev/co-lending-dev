package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationMetadataDetails {
	
	@JacksonXmlProperty(localName = "req_ref_no")
	private String txnId;
	
	@JacksonXmlProperty(localName = "RefNo")
    private String applnRefNo;
    
	@JacksonXmlProperty(localName = "processinstID")
    private String pid;

}
