package com.iexceed.appzillonbanking.cob.cards.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FetchCustDtlReq {
	
	@ApiModelProperty(required = true, position = 1, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 2, example = "Fetch")
	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@ApiModelProperty(required = true, position = 3)
	@JsonProperty("requestObj")
	private FetchCustDtlReqFields requestObj;

}