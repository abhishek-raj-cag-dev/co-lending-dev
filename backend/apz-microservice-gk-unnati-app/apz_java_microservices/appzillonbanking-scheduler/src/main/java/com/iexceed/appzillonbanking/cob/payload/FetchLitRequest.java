package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FetchLitRequest {
	

	/*@ApiModelProperty(required = true, position = 1, example = "APZRMB")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 3, example = "000000000002") 
	@JsonProperty("userId")
	private String userId;
	
	@ApiModelProperty(required = true, position = 4, example = "fetchLitCode") 
	@JsonProperty("interfaceId")
	private String interfaceId;
	
	@ApiModelProperty(required = true, position = 5, example = "WEB") 
	@JsonProperty("deviceId")
	private String deviceId;*/
	
	@JsonProperty("requestObj")
	private FetchLitRequestFields requestObj;

}
