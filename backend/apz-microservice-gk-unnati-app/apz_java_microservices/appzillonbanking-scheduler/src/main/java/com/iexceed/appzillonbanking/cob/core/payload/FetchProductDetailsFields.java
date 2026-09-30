package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class FetchProductDetailsFields {
	
	@ApiModelProperty(required = true, position = 1, example = "SAVINGS")
	@JsonProperty("productGroupCode")
	private String productGroupCode;

	public String getProductGroupCode() {
		return productGroupCode;
	}

	public void setProductGroupCode(String productGroupCode) {
		this.productGroupCode = productGroupCode;
	}
}