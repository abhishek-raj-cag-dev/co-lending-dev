package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FetchCustDtlReqWrapper {

	@JsonProperty("apiRequest")
	private FetchCustDtlReq apiRequest;
}