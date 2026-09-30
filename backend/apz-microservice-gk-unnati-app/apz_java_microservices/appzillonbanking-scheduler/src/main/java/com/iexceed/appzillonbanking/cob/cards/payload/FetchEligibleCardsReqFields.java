package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FetchEligibleCardsReqFields {
	
	@ApiModelProperty(required = true, example = "22114451")
	@JsonProperty("kycID")
	private String kycID;	
}