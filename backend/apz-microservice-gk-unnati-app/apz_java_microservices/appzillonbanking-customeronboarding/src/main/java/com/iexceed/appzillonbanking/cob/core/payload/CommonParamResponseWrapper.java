package com.iexceed.appzillonbanking.cob.core.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommonParamResponseWrapper {
	
	@JsonProperty("ResponseHeader")
	private ResponseHeader responseHeader;

	@JsonProperty("commonParamRes")
	private List<CommonParamResponse> commonParamResponse;

	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
	}

	public List<CommonParamResponse> getCommonParamResponse() {
		return commonParamResponse;
	}

	public void setCommonParamResponse(List<CommonParamResponse> commonParamResponse) {
		this.commonParamResponse = commonParamResponse;
	}

}
