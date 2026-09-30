package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BRECBValuesRequest1 {

	@Override
	public String toString() {
		return "BRECBValuesRequest1 [breCBInputRequestinput1=" + breCBInputRequestinput1 + "]";
	}

	@JsonProperty("input")
	private BRECBInputRequest1 breCBInputRequestinput1;
	
}
