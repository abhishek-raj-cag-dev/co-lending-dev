package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BRECBInputRequest1 {

	@Override
	public String toString() {
		return "BRECBInputRequest1 [breCBValuesRequestvalues2=" + breCBValuesRequestvalues2 + "]";
	}

	@JsonProperty("values")
	private BRECBValuesRequest2 breCBValuesRequestvalues2;
}
