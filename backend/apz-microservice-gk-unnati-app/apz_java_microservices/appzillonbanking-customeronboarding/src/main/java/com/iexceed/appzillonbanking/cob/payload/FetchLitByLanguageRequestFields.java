package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class FetchLitByLanguageRequestFields {
	
	@ApiModelProperty(required = true, position = 1, example = "EN")
	@JsonProperty("languageCode")
	private String languageCode;
}