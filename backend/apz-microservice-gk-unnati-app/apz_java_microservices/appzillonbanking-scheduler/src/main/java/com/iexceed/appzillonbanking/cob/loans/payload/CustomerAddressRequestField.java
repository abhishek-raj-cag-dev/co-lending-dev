package com.iexceed.appzillonbanking.cob.loans.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CustomerAddressRequestField {
	@JsonProperty("address1")
	private String address1 = "";

	@JsonProperty("addressType")
	private String addressType = "";

	@JsonProperty("pinCode")
	private String pinCode = "";

	@JsonProperty("residenceArea")
	private String residenceArea = "";

	@JsonProperty("residenceCity")
	private String residenceCity = "";

	@JsonProperty("residenceCountry")
	private String residenceCountry = "";

	@JsonProperty("residenceState")
	private String residenceState = "";

	@JsonProperty("address2")
	private String address2 = "";

	@JsonProperty("address3")
	private String address3 = "";

	@JsonProperty("landmark")
	private String landmark = "";
}
