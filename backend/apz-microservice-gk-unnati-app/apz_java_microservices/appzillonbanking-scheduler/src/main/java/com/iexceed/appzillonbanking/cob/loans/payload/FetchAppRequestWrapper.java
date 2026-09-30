package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FetchAppRequestWrapper {

	@JsonProperty("apiRequest")
	private FetchAppRequest apiRequest;

	@Override
	public String toString() {
		return "FetchAppRequestWrapper [apiRequest=" + apiRequest + "]";
	}
	
}
