package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class LoanDetailsPayload {
	
	@JsonProperty("loanPurpose")
	private String loanPurpose;

	@JsonProperty("modeOfSecurity")
	private String modeOfSecurity;
	
	@JsonProperty("subCategory")
	private String subCategory;

	@JsonProperty("modeOfDisbursement")
	private String modeOfDisbursement;
	
	@JsonProperty("language")
	private String language;
	
	@JsonProperty("frequencyOfRepayment")
	private String frequencyOfRepayment;

	@JsonProperty("initialBrePayload")
	private CibilDetailsPayload initialBrePayload;
	
}
