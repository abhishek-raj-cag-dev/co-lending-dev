package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignzyPennylessRequestWrapper {

	@Override
	public String toString() {
		return "SignzyPennylessRequestWrapper [apiRequest=" + apiRequest + "]";
	}

	@JsonProperty("apiRequest")
	private SignzyPennylessRequest apiRequest;

}