package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class VerifyNationalIdRequestWrapper {
	
	@JsonProperty("apiRequest")
	private VerifyNationalIdRequest verifyNationalIdRequest;
}