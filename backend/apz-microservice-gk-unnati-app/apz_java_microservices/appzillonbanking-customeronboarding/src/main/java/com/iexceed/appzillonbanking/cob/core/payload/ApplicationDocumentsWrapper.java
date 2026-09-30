package com.iexceed.appzillonbanking.cob.core.payload;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.domain.ab.ApplicationDocuments;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApplicationDocumentsWrapper {
	
	@JsonProperty("custDtlId")
	private BigDecimal custDtlId;
	
	@JsonProperty("applicationDocumentsList")
	private List<ApplicationDocuments> applicationDocumentsList;
}