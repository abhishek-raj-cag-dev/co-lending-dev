package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DedupeVoterIdLegalDocument {

	@ApiModelProperty(required = true, position = 1, example = "")
	@JsonProperty("id")
	private String id;
	
}