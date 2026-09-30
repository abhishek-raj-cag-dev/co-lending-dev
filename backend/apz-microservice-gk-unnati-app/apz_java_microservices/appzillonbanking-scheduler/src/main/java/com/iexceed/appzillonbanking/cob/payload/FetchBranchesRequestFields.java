package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class FetchBranchesRequestFields {

	@ApiModelProperty(required = true, position = 1, example = "KA")
	@JsonProperty("stateCode")
	private String stateCode;
	
	@ApiModelProperty(required = true, position = 2, example = "BN")
	@JsonProperty("cityCode")
	private String cityCode;
	
	@ApiModelProperty(required = true, position = 3, example = "562121")
	@JsonProperty("pinCode")
	private String pinCode;
}