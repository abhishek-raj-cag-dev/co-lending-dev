package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateCommonCodeRequest {
	
	@ApiModelProperty(required = true, position = 1)
	@JsonProperty("requestObj")
	private UpdateCommonCodeReqFields requestObj;

}
