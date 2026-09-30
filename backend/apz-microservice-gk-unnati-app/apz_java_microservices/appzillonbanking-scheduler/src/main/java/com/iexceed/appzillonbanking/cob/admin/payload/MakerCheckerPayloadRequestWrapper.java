package com.iexceed.appzillonbanking.cob.admin.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MakerCheckerPayloadRequestWrapper {

	@JsonProperty("makerCheckerPayloadReq")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private MakerCheckerPayloadRequest makerCheckerPayloadRequest;

	public MakerCheckerPayloadRequest getMakerCheckerPayloadRequest() {
		return makerCheckerPayloadRequest;
	}

	public void setMakerCheckerPayloadRequest(MakerCheckerPayloadRequest makerCheckerPayloadRequest) {
		this.makerCheckerPayloadRequest = makerCheckerPayloadRequest;
	}

	@Override
	public String toString() {
		return "MakerCheckerPayloadRequestWrapper [makerCheckerPayloadRequest=" + makerCheckerPayloadRequest + "]";
	}
	
	
	
}
