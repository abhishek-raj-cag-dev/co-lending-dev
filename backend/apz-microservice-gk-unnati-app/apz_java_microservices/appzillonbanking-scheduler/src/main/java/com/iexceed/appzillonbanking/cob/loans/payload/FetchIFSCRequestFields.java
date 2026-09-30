package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchIFSCRequestFields {

	@ApiModelProperty(required = true, position = 1, example = "ABHY0065003")
	@JsonProperty("ifsc")
	private String ifsc;

	@ApiModelProperty(required = false, position = 2, example = "1.1")
	@JsonProperty("gkv")
	private String gkv;

	@ApiModelProperty(required = false, position = 3, example = "ifscfetch")
	@JsonProperty("method")
	private String method;

	@ApiModelProperty(required = true, position = 4, example = "3")
	@JsonProperty("id")
	private String id;

	@Override
	public String toString() {
		return "FetchIFSCRequestFields [ifsc=" + ifsc + ", gkv=" + gkv + ", method=" + method + ", id=" + id + "]";
	}

}