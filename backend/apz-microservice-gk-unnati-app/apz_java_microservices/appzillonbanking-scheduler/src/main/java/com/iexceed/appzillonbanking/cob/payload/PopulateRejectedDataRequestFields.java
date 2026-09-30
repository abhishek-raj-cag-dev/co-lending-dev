package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class PopulateRejectedDataRequestFields {
	
	@ApiModelProperty(required = true, position = 1, example = "16185889475433589")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 2, example = "APZCOB")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 3, example = "john") 
	@JsonProperty("userId")
	private String userId;
	
	@ApiModelProperty(required = true, position = 4, example = "File not valid so rejecting the application") 
	@JsonProperty("remarks")
	private String remarks;
	
	@ApiModelProperty(required = true, position = 5) 
	@JsonProperty("createUserRequest")
	private CreateModifyUserRequest createUserRequest;
	
}