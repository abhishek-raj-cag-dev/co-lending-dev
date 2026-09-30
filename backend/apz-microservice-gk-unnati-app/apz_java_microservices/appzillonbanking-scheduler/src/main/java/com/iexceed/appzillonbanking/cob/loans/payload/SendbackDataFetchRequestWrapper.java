package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendbackDataFetchRequestWrapper {

	@JsonProperty("apiRequest")
	private SendbackDataFetchRequest apiRequest;

}
