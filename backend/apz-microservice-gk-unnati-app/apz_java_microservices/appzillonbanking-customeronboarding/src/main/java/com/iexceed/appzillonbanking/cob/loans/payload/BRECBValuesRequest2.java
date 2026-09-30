package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BRECBValuesRequest2 {

	@Override
	public String toString() {
		return "BRECBValuesRequest2 [breCBInputRequestinput2=" + breCBInputRequestinput2 + "]";
	}

	@JsonProperty("input")
	private BRECBInputRequest2 breCBInputRequestinput2;
	
}
