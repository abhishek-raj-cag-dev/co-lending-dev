package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyLoanRequestWrapper {

	@JsonProperty("apiRequest")
	private ApplyLoanRequest apiRequest;

	@Override
	public String toString() {
		return "ApplyLoanRequestWrapper [apiRequest=" + apiRequest + "]";
	}

}