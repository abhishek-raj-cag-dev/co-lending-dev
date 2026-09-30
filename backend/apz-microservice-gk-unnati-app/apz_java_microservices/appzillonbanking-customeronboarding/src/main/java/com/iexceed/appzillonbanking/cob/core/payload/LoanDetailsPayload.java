package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

	@JsonProperty("bmRecommendationBrePayload")
	private CibilDetailsPayload bmRecommendationBrePayload;

	@JsonProperty("initialBrePayload")
	private CibilDetailsPayload initialBrePayload;

	@JsonProperty("remittanceAmount")
	private String remittanceAmount;
	
}
