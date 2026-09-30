package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchIFSCRequestWrapper {
	@JsonProperty("apiRequest")
	private FetchIFSCRequest apiRequest;

	@Override
	public String toString() {
		return "FetchIFSCRequestWrapper [apiRequest=" + apiRequest + "]";
	}

}