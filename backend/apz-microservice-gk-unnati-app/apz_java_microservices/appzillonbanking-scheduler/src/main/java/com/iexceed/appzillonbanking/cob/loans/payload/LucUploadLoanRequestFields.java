package com.iexceed.appzillonbanking.cob.loans.payload;



import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LucUploadLoanRequestFields {
	@ApiModelProperty(required = false, example = "APZDEP")
	@JsonProperty("appId")
	private String appId;
	
	@ApiModelProperty(required = true, example = "NEW00063")
	@JsonProperty("applicationId")
	private String applicationId;
	
	@ApiModelProperty(required = false, example = "1")
	@JsonProperty("versionNum")
	private int versionNum;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("payload")
	private LucPayloadRequest payload;
	
	@ApiModelProperty(required = false, example = "")
	@JsonProperty("queries")
	private String queries;
	
	@JsonProperty("createTs")
	private String createTs;
	
	@JsonProperty("updateTs")
	private String updateTs;
	
	@JsonProperty("createdBy")	
	private String createdBy;
	
	@JsonProperty("updatedBy")
	private String updatedBy;
	
	

}
