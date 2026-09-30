package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class SearchAppRequestFields {

	@ApiModelProperty(required = true, position = 2, example = "995544871")
	@JsonProperty("mobileOrApplnId")
	private String mobileOrApplnId;
	
	@ApiModelProperty(required = true, position = 3, example = "john")
	@JsonProperty("userId")
	private String userId;	
}