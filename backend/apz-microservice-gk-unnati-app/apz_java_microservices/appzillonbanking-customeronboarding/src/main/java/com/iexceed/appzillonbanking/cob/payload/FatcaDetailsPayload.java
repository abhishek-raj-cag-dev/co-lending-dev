package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FatcaDetailsPayload {

	@JsonProperty("usCitizenFlag")
	private String usCitizenFlag;
	
	@JsonProperty("documentIdName")
	private String documentIdName;
	
	@JsonProperty("documentIdValue")
	private String documentIdValue;

	@Override
	public String toString() {
		return "FatcaDetailsPayload{" +
				"usCitizenFlag='" + usCitizenFlag + '\'' +
				", documentIdName='" + documentIdName + '\'' +
				", documentIdValue='" + documentIdValue + '\'' +
				'}';
	}
}