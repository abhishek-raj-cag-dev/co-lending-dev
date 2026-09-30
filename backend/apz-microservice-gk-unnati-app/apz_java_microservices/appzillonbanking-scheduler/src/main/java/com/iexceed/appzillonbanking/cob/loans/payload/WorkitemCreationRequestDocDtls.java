package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class WorkitemCreationRequestDocDtls {
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("docName")
	private String docName;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("docExtn")
	private String docExtn;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("docContent")
	private String docContent;
	
	@ApiModelProperty(required = true, example = "")
	@JsonProperty("docSize")
	private String docSize;

}
