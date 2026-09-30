package com.iexceed.appzillonbanking.cob.loans.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BRECBReportRequestFields {

	@Override
	public String toString() {
		return "BRECBReportRequestFields [customerId=" + customerId + ", userId=" + userId + "]";
	}

	@ApiModelProperty(required = true, position = 1, example = "1002027")
	@JsonProperty("customer_id")
	private String customerId;
	
	@ApiModelProperty(required = true, position = 2, example = "YA777183100819")
	@JsonProperty("user_id")
	private String userId; 
	
}
