package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class LucUploadLoanRequestWrapper {


	@JsonProperty("apiRequest")
	private LucUploadLoanRequest apiRequest;

	@Override
	public String toString() {
		return "LucUploadLoanRequestWrapper [apiRequest=" + apiRequest + "]";
	}
}