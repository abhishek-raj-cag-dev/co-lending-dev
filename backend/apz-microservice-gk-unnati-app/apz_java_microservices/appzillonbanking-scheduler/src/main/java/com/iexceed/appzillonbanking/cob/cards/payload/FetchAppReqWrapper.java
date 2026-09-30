package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FetchAppReqWrapper {

	@JsonProperty("apiRequest")
	private FetchAppReq fetchDeleteUserRequest;

}