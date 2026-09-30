package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadLoanRequestWrapper {

	@JsonProperty("apiRequest")
	private UploadLoanRequest apiRequest;

	@Override
	public String toString() {
		return "UploadLoanRequestWrapper [apiRequest=" + apiRequest + "]";
	}

}