package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class FetchCitiesRequestFields {
	
	@ApiModelProperty(required = true, position = 1, example = "KA")
	@JsonProperty("stateCode")
	private String stateCode;
}