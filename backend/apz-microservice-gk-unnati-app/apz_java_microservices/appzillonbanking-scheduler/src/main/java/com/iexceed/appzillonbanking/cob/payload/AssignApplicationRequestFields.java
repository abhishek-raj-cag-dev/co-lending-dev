package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.iexceed.appzillonbanking.cob.core.payload.WorkFlowDetails;
@Getter @Setter
public class AssignApplicationRequestFields {
	
	@ApiModelProperty(required = true, position = 1, example = "16185889475433589")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, position = 2, example = "APZCBO")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, position =3, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;	
	
	@ApiModelProperty(required = true, position = 4, example = "John")
	@JsonProperty("userId")
	private String userId;
	
	@ApiModelProperty(required = true, position =5, example = "")
	@JsonProperty("workflow")
	private WorkFlowDetails workFlow;
}