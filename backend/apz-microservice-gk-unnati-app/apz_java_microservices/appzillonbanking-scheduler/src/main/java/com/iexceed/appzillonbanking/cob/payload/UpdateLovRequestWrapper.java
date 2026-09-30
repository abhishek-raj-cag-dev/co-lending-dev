package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateLovRequestWrapper {

	@JsonProperty("apiRequest")
	private UpdateLovRequest apiRequest;
}