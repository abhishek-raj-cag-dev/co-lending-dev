package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationOwnedLandDtls {
	
	@JacksonXmlProperty(localName = "others")
	private String others;
	
	@JacksonXmlProperty(localName = "Asset_type")
    private String assetType;
    
	@JacksonXmlProperty(localName = "Asset_Items")
    private String assetItems;
    
	@JacksonXmlProperty(localName = "No_Of_Unit")
    private String noOfUnit;
    
	@JacksonXmlProperty(localName = "Estimated_Value")
    private String estimatedValue;
    
	@JacksonXmlProperty(localName = "insertionOrderId")
    private String insertionOrderId;
    
	@JacksonXmlProperty(localName = "Pid")
    private String pid;

}
