package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KycDedupeRequestWrapper {
	@JsonProperty("apiRequest")
	private KycDedupeRequest apiRequest;
}