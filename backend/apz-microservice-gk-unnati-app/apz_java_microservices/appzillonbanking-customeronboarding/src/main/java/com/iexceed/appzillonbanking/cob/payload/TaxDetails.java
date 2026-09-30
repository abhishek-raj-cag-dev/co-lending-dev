package com.iexceed.appzillonbanking.cob.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TaxDetails {

	@JsonProperty("country")
	private String country;
	
	@JsonProperty("countryCode")
	private String countryCode;
	
	@JsonProperty("customerHasTin")
	private String customerHasTin;
	
	@JsonProperty("tinType")
	private String tinType;
	
	@JsonProperty("tin")
	private String tin;
	
	@JsonProperty("taxDtlId")
	private String taxDtlId;
	
	@JsonProperty("reason")
	private String reason;
	
	@JsonProperty("remarks")
	private String remarks;

	@Override
	public String toString() {
		return "TaxDetails{" +
				"country='" + country + '\'' +
				", countryCode='" + countryCode + '\'' +
				", customerHasTin='" + customerHasTin + '\'' +
				", tinType='" + tinType + '\'' +
				", tin='" + tin + '\'' +
				", taxDtlId='" + taxDtlId + '\'' +
				", reason='" + reason + '\'' +
				", remarks='" + remarks + '\'' +
				'}';
	}
}