package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class LockInOutRequestFileds {
	
	@ApiModelProperty(required = true, position = 1, example = "John")
	@JsonProperty("userId")
	private String userId;
	
	@ApiModelProperty(required = false, position = 2, example = "16185889475433589")
	@JsonProperty("applicationId")
	private String applicationId;
	
	

}
