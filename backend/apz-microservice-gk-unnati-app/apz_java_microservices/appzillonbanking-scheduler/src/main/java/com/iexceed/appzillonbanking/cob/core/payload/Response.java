package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class Response {

	@JsonProperty("ResponseHeader")
	private ResponseHeader responseHeader;
	
	@JsonProperty("ResponseBody")
	private ResponseBody responseBody;
	
	public Response() {
		super();
	}

	public Response(ResponseHeader responseHeader, ResponseBody responseBody) {
		super();
		this.responseHeader = responseHeader;
		this.responseBody = responseBody;
	}

	@Override
	public String toString() {
		return "Response [responseHeader=" + responseHeader + ", responseBody=" + responseBody + "]";
	}
}
