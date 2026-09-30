package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkitemCreationHighmarkDetails {
	
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "ASHA_CMPLX_CB_DETAILS")
	private List<WorkitemCreationAshaCmplxCbDetails> ashaCmplxCbDetails;
	
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "Unnathi_CB_Values")
	private List<WorkitemCreationUnnatiCbValues> unnatiCbValues;
	
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "CAGL_CIBILIntegrationUtility_Status")
	private List<WorkitemCreationCibilIntegrationUtility> cibilIntegrationUtility;

}
