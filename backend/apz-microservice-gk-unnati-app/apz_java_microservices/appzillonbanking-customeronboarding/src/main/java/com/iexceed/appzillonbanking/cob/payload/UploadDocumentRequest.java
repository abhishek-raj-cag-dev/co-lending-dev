package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter

public class UploadDocumentRequest {

	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("requestObj")
	private UploadDocumentRequestFields requestObj;
	
	@JsonProperty("interfaceName")
	private String interfaceName;	
}