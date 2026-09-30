package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter @Setter
public class ResponseWrapper {
	
	@JsonProperty("apiResponse")
	private Response apiResponse;
	
	public ResponseWrapper() {
		super();
	}
	
	public ResponseWrapper(Response apiResponse) {
		super();
		this.apiResponse = apiResponse;
	}

	@Override
	public String toString() {
		return "ResponseWrapper [apiResponse=" + apiResponse + "]";
	}
}
