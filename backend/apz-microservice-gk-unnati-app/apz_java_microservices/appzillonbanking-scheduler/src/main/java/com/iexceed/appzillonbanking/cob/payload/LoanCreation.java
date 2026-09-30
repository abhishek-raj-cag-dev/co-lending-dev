package com.iexceed.appzillonbanking.cob.payload;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoanCreation {

	@ApiModelProperty(required = false, example = "100817511")
	@JsonProperty("header")
	private Object header;

	@ApiModelProperty(required = true, example = "100817511")
	@JsonProperty("body")
	private Object body;


}