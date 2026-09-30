package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class PopulateapplnWFRequestFields {

	@ApiModelProperty(required = true, example = "")
	@JsonProperty("workflow")
	private WorkFlowDetails workflow;

	@Override
	public String toString() {
		return "PopulateapplnWFRequestFields [workflow=" + workflow + ", createdBy=" + createdBy + ", applicationId="
				+ applicationId + ", applicationStatus=" + applicationStatus + ", appId=" + appId + ", versionNum="
				+ versionNum + "]";
	}

	@ApiModelProperty(required = true, example = "John")
	@JsonProperty("createdBy")
	private String createdBy;
	
	@ApiModelProperty(required = true, example = "12121454")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = true, example = "PENDING")
	@JsonProperty("applicationStatus")
	private String applicationStatus;
	
	@ApiModelProperty(required = true, example = "APZCBO")
	@JsonProperty("appId")
	private String appId;	
	
	@ApiModelProperty(required = true, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;	
}