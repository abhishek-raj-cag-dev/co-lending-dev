package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendbackDataFetchRequestFields {
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("workitemNumber")
	private String workitemNumber;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("versionNo")
	private int versionNo;

}
