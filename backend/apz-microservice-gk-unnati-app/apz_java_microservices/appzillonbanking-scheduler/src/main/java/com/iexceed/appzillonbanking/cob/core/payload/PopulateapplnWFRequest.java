package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopulateapplnWFRequest {

	@JsonProperty("requestObj")
	private PopulateapplnWFRequestFields requestObj;

	@Override
	public String toString() {
		return "PopulateapplnWFRequest [requestObj=" + requestObj + "]";
	}

}
