package com.iexceed.appzillonbanking.cob.loans.payload;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LucPayloadPurposesRequest {
	@JsonProperty("purpose")
	private String purpose;
	
	@JsonProperty("subPurpose")
	private String subPurpose;
	
	@JsonProperty("additionalDetails")
	private String additionalDetails;
	
	@JsonProperty("utilisedAmount")
	private BigDecimal utilisedAmount;
				
}
