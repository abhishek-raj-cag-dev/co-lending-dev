package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PinCodeApiRequest {

	@JsonProperty("interfaceName")
	private String interfaceName;
	
	@JsonProperty("requestObj")
	private PinCodeRequestObject requestObject;
}
