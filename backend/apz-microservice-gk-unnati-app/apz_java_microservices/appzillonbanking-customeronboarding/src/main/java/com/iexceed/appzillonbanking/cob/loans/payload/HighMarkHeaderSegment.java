package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HighMarkHeaderSegment {

	@JacksonXmlProperty(localName = "PRODUCT-TYPE")
	private String productType;

	@JacksonXmlProperty(localName = "RES-FRMT")
	private String resFrmt;

	@JacksonXmlProperty(localName = "LOS-NAME")
	private String losName;

	@JacksonXmlProperty(localName = "LOS-VENDER")
	private String losVender;

	@JacksonXmlProperty(localName = "LOS-VERSION")
	private String losVersion;

	@JacksonXmlProperty(localName = "USER-ID")
	private String userId;
	
	@JacksonXmlProperty(localName = "MFI-CONSUMER")
	private HighMarkMfiConsumer mfiConsumer;

}
