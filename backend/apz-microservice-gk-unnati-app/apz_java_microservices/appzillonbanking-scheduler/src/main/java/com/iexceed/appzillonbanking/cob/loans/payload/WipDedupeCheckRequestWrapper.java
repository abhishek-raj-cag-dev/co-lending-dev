package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WipDedupeCheckRequestWrapper {
	@JsonProperty("apiRequest")
	private WipDedupeCheckRequest apiRequest;
}
