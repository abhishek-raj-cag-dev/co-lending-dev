package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ViewAllRecordsRequestFields {
	
	@ApiModelProperty(required = true, example = "john")
	@JsonProperty("userId")
	private String userId;
	
	@ApiModelProperty(required = true, example = "INPROGRESS")
	@JsonProperty("status")
	private String status;
	
	@ApiModelProperty(required = true, example = "initiator")
	@JsonProperty("roleId")
	private String roleId;
	
	@ApiModelProperty(example = "")
	@JsonProperty("branchCode")
	private String branchCode;
	
	@ApiModelProperty(required = true, example = "NEW/RENEWAL")
	@JsonProperty("fetchType")
	private String fetchType;
	
	
}