package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FetchEligibleCardsReq {

	@ApiModelProperty(required = true, position = 1, example = "Fetch")
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@ApiModelProperty(required = true, position = 2)
	@JsonProperty("requestObj")
	private FetchEligibleCardsReqFields requestObj;
}