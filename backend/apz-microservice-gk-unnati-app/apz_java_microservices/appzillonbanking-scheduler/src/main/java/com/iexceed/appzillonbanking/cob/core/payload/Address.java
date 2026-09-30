package com.iexceed.appzillonbanking.cob.core.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Address {
	
	@JsonProperty("addressSameAs")
	private String addressSameAs;
	
	@JsonProperty("addressType")
	private String addressType;
	
	@JsonProperty("doorNum")
	private String doorNum;

	@JsonProperty("addressLine1")
	private String addressLine1;
	
	@JsonProperty("addressLine2")
	private String addressLine2;	
	
	@JsonProperty("addressLine3")
	private String addressLine3;
	
	@JsonProperty("district")
	private String district;
	
	@JsonProperty("city")
	private String city;
	
	@JsonProperty("state")
	private String state;
	
	@JsonProperty("country")
	private String country;
	
	@JsonProperty("pinCode")
	private String pinCode;
	
	@JsonProperty("landMark")
	private String landMark;
	
	@JsonProperty("mntYrCurrRes")
	private String mntYrCurrRes;
	
	@JsonProperty("mntYrCurrCity")
	private String mntYrCurrCity;
	
	@JsonProperty("area")
	private String area;
	
	@JsonProperty("currentAddressProof")
	private String currentAddressProof;
	
	@JsonProperty("houseType")
	private String houseType;
	
	@JsonProperty("communctionAddress")
	private String communctionAddress;
	
	@JsonProperty("locateCoOrdinatesFor")
	private String locateCoOrdinatesFor;
	
	@JsonProperty("locateCoOrdinates")
	private String locateCoOrdinates;
	
	@JsonProperty("nameAsperAddressProof")
	private String nameAsperAddressProof;
	
	@JsonProperty("dobAsperAddressProof")
	private String dobAsperAddressProof;
	
	@JsonProperty("residenceOwnership")
	private String residenceOwnership;
	
	@JsonProperty("residenceAddressSince")
	private String residenceAddressSince;
	
	@JsonProperty("residenceCitySince")
	private String residenceCitySince;
	
}
