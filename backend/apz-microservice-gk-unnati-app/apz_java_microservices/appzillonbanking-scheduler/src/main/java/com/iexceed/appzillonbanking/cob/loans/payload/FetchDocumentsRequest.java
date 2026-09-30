package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchDocumentsRequest {

	@ApiModelProperty(required = true, position = 1, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;

	@ApiModelProperty(required = true, position = 2, example = "77777777")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 3, example = "77777777")
	@JsonProperty("docType")
	private String docType;

	@ApiModelProperty(required = false, position = 4, example = "77777777")
	@JsonProperty("loanId")
	private String loanId;
}
