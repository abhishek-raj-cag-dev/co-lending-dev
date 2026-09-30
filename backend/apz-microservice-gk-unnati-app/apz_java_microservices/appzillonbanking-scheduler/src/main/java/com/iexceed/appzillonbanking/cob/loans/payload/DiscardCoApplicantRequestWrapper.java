package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscardCoApplicantRequestWrapper {

	@JsonProperty("apiRequest")
	private DiscardCoApplicantRequest apiRequest;
}