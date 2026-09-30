

package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoanRejectionReqFields {

	@ApiModelProperty(required = true, position = 1, example = "rejected")
	@JsonProperty("remarks")
	private String remarks;

}