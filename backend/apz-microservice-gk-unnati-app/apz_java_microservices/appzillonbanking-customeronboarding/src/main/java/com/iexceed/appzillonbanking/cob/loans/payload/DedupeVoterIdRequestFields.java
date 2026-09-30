package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DedupeVoterIdRequestFields {

	@ApiModelProperty(required = true, position = 1, example = "1.1")
	@JsonProperty("gkv")
	private String gkv;
	
	@ApiModelProperty(required = true, example = "kyc.dedupecheck")
	@JsonProperty("method")
	private String method;
	
	@ApiModelProperty(required = true, example = "1.1")
	@JsonProperty("id")
	private String id;
	
	@ApiModelProperty(required = true)
	@JsonProperty("legalDocument")
	private DedupeVoterIdLegalDocument legalDocument;
	
}