package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoterBackOcrRequestWrapper {
	@Override
	public String toString() {
		return "VoterBackOcrRequestWrapper [apiRequest=" + apiRequest + "]";
	}

	@JsonProperty("apiRequest")
	private VoterBackOcrRequest apiRequest;

}
