package com.iexceed.appzillonbanking.cob.nesl.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnachRequestWrapper {

	@Override
	public String toString() {
		return "EnachRequestWrapper [apiRequest=" + apiRequest + "]";
	}

	@JsonProperty("apiRequest")
	private EnachRequest apiRequest;

}