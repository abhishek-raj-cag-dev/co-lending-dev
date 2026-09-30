package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BRECBRequestFields {

	
	@Override
	public String toString() {
		return "BRECBRequestFields [breCBValuesRequestvalues1=" + breCBValuesRequestvalues1 + "]";
	}

	@JsonProperty("values")
	private BRECBValuesRequest1 breCBValuesRequestvalues1;
	
	
}
