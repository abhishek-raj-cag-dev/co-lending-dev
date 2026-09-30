package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoanCreationRequest {

	@JsonProperty("requestObj")
	private LoanCreationRequestFields requestObj;
	
	@JsonProperty("interfaceName")
	private String interfaceName;

}
