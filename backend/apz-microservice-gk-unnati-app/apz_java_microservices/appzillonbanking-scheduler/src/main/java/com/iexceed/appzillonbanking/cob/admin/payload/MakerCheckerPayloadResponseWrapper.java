package com.iexceed.appzillonbanking.cob.admin.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cob.core.payload.ResponseHeader;

import java.util.List;

public class MakerCheckerPayloadResponseWrapper {

	@JsonProperty("ResponseHeader")
	private ResponseHeader responseHeader;

	@JsonProperty("makerCheckerPayloadRes")
	private List<MakerCheckerPayloadFetchResponse> makerCheckerPayloadResponse;

	public List<MakerCheckerPayloadFetchResponse> getMakerCheckerPayloadResponse() {
		return makerCheckerPayloadResponse;
	}

	public void setMakerCheckerPayloadResponse(List<MakerCheckerPayloadFetchResponse> makerCheckerPayloadResponse) {
		this.makerCheckerPayloadResponse = makerCheckerPayloadResponse;
	}

	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
	}

}
