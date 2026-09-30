package com.iexceed.appzillonbanking.cob.loans.payload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LucPayloadRequest {
	@JsonProperty("totalTransferedAmount")
	private BigDecimal totalTransferedAmount;
	
	@JsonProperty("totalUnutilisedAmount")
	private BigDecimal totalUnutilisedAmount;
	
	@JsonProperty("isAmountUtilised")
	private String isAmountUtilised;
	
	@JsonProperty("lucRemarks")
	private String lucRemarks;
	
	@JsonProperty("purposes")
	List<LucPayloadPurposesRequest> purposes;
	

}
