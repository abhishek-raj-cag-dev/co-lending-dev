package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class PopulateapplnWFRequestWrapper {

	@JsonProperty("apiRequest")
	private PopulateapplnWFRequest apiRequest;
}