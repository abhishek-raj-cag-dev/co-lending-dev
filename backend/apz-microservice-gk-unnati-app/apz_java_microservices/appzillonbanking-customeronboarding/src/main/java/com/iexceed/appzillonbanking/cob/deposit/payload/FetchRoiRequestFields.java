package com.iexceed.appzillonbanking.cob.deposit.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class FetchRoiRequestFields {

	@ApiModelProperty(required = true, example = "productCode")
	@JsonProperty("productCode")
	private String productCode;
	
	@ApiModelProperty(required = true, example = "25")
	@JsonProperty("tenureDtls")
	private List<TenureDetails> tenureDtls;
	
	@ApiModelProperty(required = true, example = "120000")
	@JsonProperty("amount")
	private String amount;
}