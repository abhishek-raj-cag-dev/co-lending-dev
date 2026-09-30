package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FetchCustDtlReqFields {
	
	@ApiModelProperty(required = true, example = "22114451")
	@JsonProperty("customerId")
	private String customerId;	

}