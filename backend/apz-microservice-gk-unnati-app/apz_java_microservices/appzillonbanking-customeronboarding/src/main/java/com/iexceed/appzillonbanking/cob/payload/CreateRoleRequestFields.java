package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateRoleRequestFields {
	
	@ApiModelProperty(required = true, position = 1, example = "APZCBO")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position = 2, example = "initiator")
	@JsonProperty("roleId")
	private String roleId;
	
	@ApiModelProperty(required = true, position = 3, example = "Approver")
	@JsonProperty("accessPermission")
	private String accessPermission;
	
	@ApiModelProperty(required = true, position = 4, example = "")
	@JsonProperty("featureStatusMap")
	private String featureStatusMap;

		
}