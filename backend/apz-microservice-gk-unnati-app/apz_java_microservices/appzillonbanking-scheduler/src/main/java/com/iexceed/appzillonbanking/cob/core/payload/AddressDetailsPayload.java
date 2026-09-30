package com.iexceed.appzillonbanking.cob.core.payload;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddressDetailsPayload {
	
	@JsonProperty("addressList")
	private List<Address> addressList;
	
	@JsonProperty("commAddEqPerAdd")
	private String commAddEqPerAdd;
	
	@JsonProperty("nomineeAddEqPerAdd")
	private String nomineeAddEqPerAdd;	
	
	@JsonProperty("guardianAddEqPerAdd")
	private String guardianAddEqPerAdd;

}